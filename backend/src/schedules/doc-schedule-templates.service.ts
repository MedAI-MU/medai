import {
  BadRequestException,
  Injectable,
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
    if (user.role === 'doctor') {
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

  async create(dto: CreateDocScheduleTemplateDto, user: TokenUser) {
    if (user.role == 'doctor' && dto.doctorId !== user.id)
      throw new UnauthorizedException(
        'Doctors can only create schedule templates for themselves',
      );

    let doctor: Doctor;
    if (user.role !== 'doctor') {
      const doctorEntity = await this.doctorRepository.findOneBy({
        userId: dto.doctorId,
      });
      if (!doctorEntity) throw new BadRequestException('Doctor not found');
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
    user: TokenUser,
  ) {
    return this.dataSource.transaction(async (manager) => {
      const templateRepo = manager.getRepository(DocScheduleTemplate);
      const slotRepo = manager.getRepository(DocScheduleTemplateSlot);
      const doctorRepo = manager.getRepository(Doctor);

      const scheduleTemplate = await templateRepo.findOne({
        where: { id: templateId },
        relations: ['doctor'],
      });

      if (!scheduleTemplate)
        throw new BadRequestException('Schedule template not found');

      // Authorization: Only doctor can update their own templates created by them or assigned to them, secretary can update everyone's
      if (user.role == 'doctor' && scheduleTemplate.doctor.userId !== user.id) {
        throw new UnauthorizedException(
          'Doctors can only update their own schedule templates',
        );
      }

      if (dto.doctorId) {
        // Doctors cannot change the doctor ID of a template
        if (user.role === 'doctor') {
          throw new UnauthorizedException(
            'Doctors cannot change the doctor of a schedule template',
          );
        }

        const doctor = await doctorRepo.findOneBy({
          userId: dto.doctorId,
        });

        if (!doctor) throw new BadRequestException('Doctor not found');

        scheduleTemplate.doctor = doctor;
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

  async delete(templateId: number, user: TokenUser) {
    const scheduleTemplate = await this.scheduleTemplateRepository.findOne({
      where: { id: templateId },
      relations: ['doctor'],
    });

    if (!scheduleTemplate)
      throw new BadRequestException('Schedule template not found');

    // Authorization: Only doctor can delete their own templates, secretary can delete everyone's
    if (user.role === 'doctor' && scheduleTemplate.doctor.userId !== user.id) {
      throw new UnauthorizedException(
        'Doctors can only delete their own schedule templates',
      );
    }

    const result = await this.scheduleTemplateRepository.delete({
      id: templateId,
    });

    if (result.affected === 0)
      throw new BadRequestException('Schedule template not found');
  }
}
