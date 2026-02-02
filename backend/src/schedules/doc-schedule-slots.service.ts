import {
  BadRequestException,
  Injectable,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import {
  Repository,
  In,
  Between,
  MoreThanOrEqual,
  LessThanOrEqual,
} from 'typeorm';
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';
import { areValidTimeRanges } from './utils/are-valid-time-ranges.utils';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { DocSchedule } from './entities/doc-schedule.entity';
import { DocScheduleDto, DocScheduleDayDto } from './dtos/doc-schedule.dto';
import { PagedListDto } from '../shared/dtos/paged-list.dto';
import { DocScheduleSlotDto } from './dtos/doc-schedule-slot.dot';

@Injectable()
export class DocScheduleSlotsService {
  constructor(
    @InjectRepository(DocScheduleSlot)
    private readonly docScheduleSlotsRepository: Repository<DocScheduleSlot>,
    @InjectRepository(DocSchedule)
    private readonly docSchedulesRepository: Repository<DocSchedule>,
    @InjectRepository(Doctor)
    private readonly doctorsRepository: Repository<Doctor>,
  ) {}

  async getByDoctorId(
    doctorId: number,
    fromDate?: string,
    toDate?: string,
    pageNo: number = 1,
    pageSize: number = 10,
  ) {
    const doctor = await this.doctorsRepository.findOne({
      where: { userId: doctorId },
      relations: ['user'],
    });
    if (!doctor) throw new NotFoundException('Doctor not found');

    const [schedules, total] = await this.docSchedulesRepository.findAndCount({
      where: {
        doctor: { userId: doctorId },
        ...(fromDate && toDate
          ? { dayDate: Between(fromDate, toDate) }
          : fromDate
            ? { dayDate: MoreThanOrEqual(fromDate) }
            : toDate
              ? { dayDate: LessThanOrEqual(toDate) }
              : {}),
      },
      order: { dayDate: 'ASC' },
      skip: (pageNo - 1) * pageSize,
      take: pageSize,
    });

    const schedulesSlots = await this.docSchedulesRepository.find({
      where: { id: In(schedules.map((s) => s.id)) },
      relations: ['slots', 'doctor'],
      order: { dayDate: 'ASC', slots: { startTime: 'ASC' } },
    });

    const daysDto: DocScheduleDayDto[] = schedulesSlots.map((schedule) => ({
      day: schedule.dayDate,
      slots: schedule.slots.map(
        (slot) =>
          new DocScheduleSlotDto({
            id: slot.id,
            startTime: slot.startTime,
            endTime: slot.endTime,
            status: slot.status,
          }),
      ),
    }));

    const pagedDays = new PagedListDto<DocScheduleDayDto>(
      daysDto,
      total,
      pageNo,
      pageSize,
    );

    return new DocScheduleDto({
      doctorId: doctor.userId,
      name: doctor.user.name,
      speciality: doctor.specialty,
      days: pagedDays,
    });
  }

  async create(dto: CreateDocScheduleDto, user: TokenUser) {
    // Authorization: Secretary can create for any doctor, Doctor can only create for themselves
    if (user.role === 'doctor' && user.id !== dto.doctorId)
      throw new UnauthorizedException(
        'Doctors can only create schedules for themselves',
      );

    let doctor: Doctor;
    if (user.role !== 'doctor') {
      const doctorEntity = await this.doctorsRepository.findOneBy({
        userId: dto.doctorId,
      });
      if (!doctorEntity) throw new NotFoundException('Doctor not found');
      doctor = doctorEntity;
    } else doctor = new Doctor({ userId: dto.doctorId });

    const flattenedSlots = dto.days.flatMap((day) =>
      day.slots.map((slot) => ({
        day: day.date,
        startTime: slot.startTime,
        endTime: slot.endTime,
      })),
    );

    const dayDates = Array.from(new Set(dto.days.map((day) => day.date)));

    const existingSchedules = await this.docSchedulesRepository.find({
      where: {
        doctor: { userId: dto.doctorId },
        dayDate: In(dayDates.map((dayDate) => new Date(dayDate))),
      },
      relations: ['slots'],
    });

    const scheduleByDay = new Map(
      existingSchedules.map((schedule) => [schedule.dayDate, schedule]),
    );

    const existingSlots = existingSchedules.flatMap((schedule) =>
      schedule.slots.map((slot) => ({
        startTime: slot.startTime.substring(0, 5),
        endTime: slot.endTime.substring(0, 5),
        day: schedule.dayDate,
      })),
    );

    if (areValidTimeRanges([...flattenedSlots, ...existingSlots]) === false)
      throw new BadRequestException(
        'Invalid or overlapping time ranges detected',
      );

    const newSchedules = dayDates
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
      const savedSchedules =
        await this.docSchedulesRepository.save(newSchedules);
      savedSchedules.forEach((schedule) => {
        scheduleByDay.set(schedule.dayDate, schedule);
      });
    }

    const slotsToSave = flattenedSlots.map(
      (slot) =>
        new DocScheduleSlot({
          schedule: scheduleByDay.get(slot.day)!,
          startTime: slot.startTime,
          endTime: slot.endTime,
        }),
    );

    return this.docScheduleSlotsRepository.save(slotsToSave);
  }

  async update(dto: UpdateDocScheduleSlotDto, slotId: number, user: TokenUser) {
    const slot = await this.docScheduleSlotsRepository.findOne({
      where: { id: slotId },
      relations: ['schedule', 'schedule.doctor'],
    });
    if (!slot) throw new NotFoundException('Schedule slot not found');

    // Authorization: Secretary can update any slot, Doctor can only update their own slots
    if (user.role === 'doctor' && slot.schedule.doctor.userId !== user.id) {
      throw new UnauthorizedException(
        'Doctors can only update their own schedule slots',
      );
    }

    const targetDay = dto.day ?? slot.schedule.dayDate;
    const startTime = dto.startTime ?? slot.startTime;
    const endTime = dto.endTime ?? slot.endTime;

    let targetSchedule = await this.docSchedulesRepository.findOne({
      where: {
        doctor: { userId: slot.schedule.doctor.userId },
        dayDate: targetDay,
      },
      relations: ['slots', 'doctor'],
    });

    if (!targetSchedule) {
      targetSchedule = await this.docSchedulesRepository.save(
        new DocSchedule({
          doctor: slot.schedule.doctor,
          dayDate: targetDay,
          createdBy: new User({ id: user.id }),
        }),
      );
      targetSchedule.slots = [];
    }

    const existingSlots = (targetSchedule.slots ?? []).filter(
      (s) => s.id !== slot.id,
    );

    if (
      !areValidTimeRanges([
        { startTime, endTime, day: targetDay },
        ...existingSlots.map((s) => ({
          startTime: s.startTime.substring(0, 5),
          endTime: s.endTime.substring(0, 5),
          day: targetSchedule.dayDate,
        })),
      ])
    ) {
      throw new BadRequestException(
        'Invalid or overlapping time ranges detected',
      );
    }

    slot.schedule = targetSchedule;
    slot.startTime = startTime;
    slot.endTime = endTime;

    return this.docScheduleSlotsRepository.save(slot);
  }

  async delete(slotId: number, user: TokenUser) {
    const qb = this.docScheduleSlotsRepository
      .createQueryBuilder()
      .delete()
      .from(DocScheduleSlot)
      .where('id = :slotId', { slotId })
      .andWhere('status = :status', {
        status: 'available',
      });

    if (user.role === 'doctor') {
      qb.andWhere(
        `
      "docScheduleId" IN (
        SELECT ds.id
        FROM doc_schedule ds
        WHERE ds."doctorId" = :doctorId
      )
    `,
      ).setParameter('doctorId', user.id);
    }

    const result = await qb.execute();

    if (result.affected === 0) {
      throw new BadRequestException(
        'Slot not found, not available, or not authorized to delete',
      );
    }
  }
}
