import {
  BadRequestException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Doctor } from '../doctors/entities/doctor.entity';
import { In, Repository } from 'typeorm';
import { areValidTimeRanges } from './utils/are-valid-time-ranges.utils';
import { User } from '../users/entities/user.entity';
import { DocScheduleTemplate } from './entities/doc-schedule-template.entity';
import { DocScheduleTemplateSlot } from './entities/doc-schedule-template-slot.entity';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';
import { DataSource } from 'typeorm';
import { TokenUser } from '../auth/interfaces/token-user.interface';
import { ApplyDocScheduleTemplateDto } from './dtos/apply-doc-schedule-template.dto';
import { DocSchedule } from './entities/doc-schedule.entity';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import { UserRoles } from '../users/types/role.types';

@Injectable()
export class DocScheduleTemplatesService {
  constructor(
    @InjectRepository(Doctor) private doctorRepository: Repository<Doctor>,
    @InjectRepository(DocScheduleTemplate)
    private scheduleTemplateRepository: Repository<DocScheduleTemplate>,
    private readonly dataSource: DataSource,
  ) {}

  async getAll(
    user: TokenUser,
    pageNo: number,
    pageSize: number,
    name?: string,
    doctorId?: number,
  ) {
    if (user.role === UserRoles.DOCTOR) {
      doctorId = user.id;
    }
    const templateQuery =
      this.scheduleTemplateRepository.createQueryBuilder('t');
    if (name) {
      templateQuery.addSelect(`similarity(t.name, :name)`, 'similarity');
      templateQuery.where('t.name ILIKE :name', { name: `%${name}%` });
      templateQuery.orderBy('similarity', 'DESC');
    }
    if (doctorId) {
      templateQuery.andWhere('t.doctorId = :doctorId', { doctorId });
    }
    const [templates, count] = await templateQuery
      .addOrderBy('t.createdAt', 'DESC')
      .skip((pageNo - 1) * pageSize)
      .take(pageSize)
      .getManyAndCount();

    const templateIds = templates.map((t) => t.id);

    const fullTemplates = await this.scheduleTemplateRepository.find({
      where: { id: In(templateIds) },
      relations: {
        doctor: true,
        createdBy: true,
        slots: true,
      },
      order: {
        createdAt: 'DESC',
        slots: { weekDay: 'ASC', startTime: 'ASC' },
      },
    });

    const templatesDto = fullTemplates.map((template) => ({
      id: template.id,
      name: template.name,
      doctor: {
        id: template.doctor.userId,
        name: template.doctor.user.name,
        specialty: template.doctor.specialty,
      },
      slots: template.slots.map((slot) => ({
        weekDay: slot.weekDay,
        startTime: slot.startTime,
        endTime: slot.endTime,
      })),
      createdBy: {
        id: template.createdBy.id,
        name: template.createdBy.name,
      },
      createdAt: template.createdAt,
      updatedAt: template.updatedAt,
    }));

    return {
      data: templatesDto,
      total: count,
    };
  }

  async create(
    doctorId: number,
    dto: CreateDocScheduleTemplateDto,
    user: TokenUser,
  ) {
    if (user.role == UserRoles.DOCTOR && doctorId !== user.id)
      throw new UnauthorizedException(
        'Doctors can only create schedule templates for themselves',
      );

    let doctor: Doctor;
    if (user.role !== UserRoles.DOCTOR) {
      const doctorEntity = await this.doctorRepository.findOneBy({
        userId: doctorId,
      });
      if (!doctorEntity) throw new NotFoundException('Doctor not found');
      doctor = doctorEntity;
    } else doctor = new Doctor({ userId: user.id });

    if (!areValidTimeRanges(dto.slots))
      throw new BadRequestException(
        'Invalid or overlapping time ranges in slots',
      );

    const scheduleTemplate = new DocScheduleTemplate({
      name: dto.name,
      doctor,
      createdBy: new User({ id: user.id }),
      slots: dto.slots.map(
        (slot) =>
          new DocScheduleTemplateSlot({
            ...slot,
            weekDay: slot.day,
          }),
      ),
    });

    return this.scheduleTemplateRepository.save(scheduleTemplate);
  }

  async update(
    dto: UpdateDocScheduleTemplateDto,
    templateId: number,
    doctorId: number,
    user: TokenUser,
  ) {
    return this.dataSource.transaction(async (manager) => {
      const templateRepo = manager.getRepository(DocScheduleTemplate);
      const slotRepo = manager.getRepository(DocScheduleTemplateSlot);

      const scheduleTemplate = await templateRepo.findOne({
        where: { id: templateId, doctor: { userId: doctorId } },
        relations: ['doctor'],
      });

      if (!scheduleTemplate)
        throw new NotFoundException('Schedule template not found');

      // Authorization: Only doctor can update their own templates created by them or assigned to them, secretary can update everyone's
      if (
        user.role == UserRoles.DOCTOR &&
        scheduleTemplate.doctor.userId !== user.id
      ) {
        throw new UnauthorizedException(
          'Doctors can only update their own schedule templates',
        );
      }

      scheduleTemplate.name = dto.name ?? scheduleTemplate.name;

      if (dto.slots) {
        if (!areValidTimeRanges(dto.slots))
          throw new BadRequestException(
            'Invalid or overlapping time ranges in slots',
          );

        await slotRepo.delete({
          template: { id: templateId },
        });

        const newSlots = dto.slots.map((slot) =>
          slotRepo.create({
            ...slot,
            template: scheduleTemplate,
            weekDay: slot.day,
          }),
        );
        await slotRepo.save(newSlots);
      }

      return templateRepo.save(scheduleTemplate);
    });
  }

  async delete(templateId: number, doctorId: number, user: TokenUser) {
    const scheduleTemplate = await this.scheduleTemplateRepository.findOne({
      where: { id: templateId, doctor: { userId: doctorId } },
      relations: ['doctor'],
    });

    if (!scheduleTemplate)
      throw new NotFoundException('Schedule template not found');

    // Authorization: Only doctor can delete their own templates, secretary can delete everyone's
    if (
      user.role === UserRoles.DOCTOR &&
      scheduleTemplate.doctor.userId !== user.id
    ) {
      throw new UnauthorizedException(
        'Doctors can only delete their own schedule templates',
      );
    }

    const result = await this.scheduleTemplateRepository.delete({
      id: templateId,
      doctor: { userId: doctorId },
    });

    if (result.affected === 0)
      throw new NotFoundException('Schedule template not found');
  }

  async applyTemplate(
    templateId: number,
    doctorId: number,
    dto: ApplyDocScheduleTemplateDto,
    user: TokenUser,
  ) {
    return this.dataSource.transaction(async (manager) => {
      // Authorization: Secretary can apply for any doctor, Doctor can only apply for themselves
      if (user.role === UserRoles.DOCTOR && user.id !== doctorId)
        throw new UnauthorizedException(
          'Doctors can only apply templates to their own schedules',
        );

      // Get the template
      const templateRepo = manager.getRepository(DocScheduleTemplate);
      const scheduleRepo = manager.getRepository(DocSchedule);
      const slotRepo = manager.getRepository(DocScheduleSlot);

      const template = await templateRepo.findOne({
        where: { id: templateId, doctor: { userId: doctorId } },
        relations: ['doctor', 'slots'],
      });

      if (!template) throw new NotFoundException('Schedule template not found');

      // Parse dates
      const startDate = new Date(dto.startDate);
      const endDate = new Date(dto.endDate);

      if (startDate > endDate) {
        throw new BadRequestException(
          'startDate must be before or equal to endDate',
        );
      }

      // Generate dates based on template weekDays
      const applicableDates: string[] = [];
      const currentDate = new Date(startDate);

      while (currentDate <= endDate) {
        const weekDay = currentDate.getUTCDay(); // 0 = Sunday, 1 = Monday, etc. (UTC)
        if (template.slots.some((slot) => slot.weekDay === weekDay)) {
          applicableDates.push(currentDate.toISOString().split('T')[0]);
        }
        currentDate.setUTCDate(currentDate.getUTCDate() + 1);
      }

      // Get doctor entity
      const doctor = template.doctor;

      // Check for existing schedules
      const existingSchedules = await scheduleRepo.find({
        where: {
          doctor: { userId: doctorId },
          dayDate: In(applicableDates),
        },
        relations: ['slots'],
      });

      const scheduleByDay = new Map(
        existingSchedules.map((schedule) => [schedule.dayDate, schedule]),
      );

      // Collect all slots to validate
      const allSlotsToValidate = applicableDates.flatMap((day) => {
        const weekDay = new Date(day).getDay();
        return template.slots
          .filter((slot) => slot.weekDay === weekDay)
          .map((slot) => ({
            day,
            startTime: slot.startTime.substring(0, 5),
            endTime: slot.endTime.substring(0, 5),
          }));
      });

      // Add existing slots for validation
      const existingSlotsForValidation = existingSchedules.flatMap((schedule) =>
        schedule.slots.map((slot) => ({
          day: schedule.dayDate,
          startTime: slot.startTime.substring(0, 5),
          endTime: slot.endTime.substring(0, 5),
        })),
      );

      if (
        !areValidTimeRanges([
          ...allSlotsToValidate,
          ...existingSlotsForValidation,
        ])
      )
        throw new BadRequestException(
          'Invalid or overlapping time ranges detected when applying template',
        );

      // Create new schedules for days that don't exist
      const newSchedules = applicableDates
        .filter((day) => !scheduleByDay.has(day))
        .map(
          (day) =>
            new DocSchedule({
              doctor,
              dayDate: day,
              createdBy: new User({ id: user.id }),
            }),
        );

      if (newSchedules.length > 0) {
        const savedSchedules = await scheduleRepo.save(newSchedules);
        savedSchedules.forEach((schedule) => {
          scheduleByDay.set(schedule.dayDate, schedule);
        });
      }

      // Create slots from template
      const slotsToSave = applicableDates.flatMap((day) => {
        const weekDay = new Date(day).getUTCDay();
        return template.slots
          .filter((slot) => slot.weekDay === weekDay)
          .map(
            (slot) =>
              new DocScheduleSlot({
                schedule: scheduleByDay.get(day)!,
                startTime: slot.startTime,
                endTime: slot.endTime,
              }),
          );
      });

      return slotRepo.save(slotsToSave);
    });
  }
}
