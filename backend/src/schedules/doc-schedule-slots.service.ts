import { BadRequestException, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import { Repository, In } from 'typeorm';
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';
import { areValidTimeRanges } from './utils/are-valid-time-ranges.utils';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';

@Injectable()
export class DocScheduleSlotsService {
  constructor(
    @InjectRepository(DocScheduleSlot)
    private readonly docScheduleSlotsRepository: Repository<DocScheduleSlot>,
    @InjectRepository(Doctor)
    private readonly doctorsRepository: Repository<Doctor>,
    @InjectRepository(User)
    private readonly usersRepository: Repository<User>,
  ) {}

  async create(dto: CreateDocScheduleDto, secretaryId: number) {
    const doctor = await this.doctorsRepository.findOneBy({
      userId: dto.doctorId,
    });
    if (!doctor) throw new BadRequestException('Doctor not found');

    const secretary = await this.usersRepository.findOneBy({
      id: secretaryId,
    });
    if (!secretary) throw new BadRequestException('Secretary not found');

    const slotsDays = new Set<string>();

    const slotsToSave = dto.slots.map((slotDto) => {
      if (!slotsDays.has(slotDto.day)) slotsDays.add(slotDto.day);
      return new DocScheduleSlot({
        doctor,
        createdBy: secretary,
        dayDate: slotDto.day,
        startTime: slotDto.startTime,
        endTime: slotDto.endTime,
      });
    });

    const existingSlots = await this.docScheduleSlotsRepository.find({
      where: {
        doctor: { userId: dto.doctorId },
        dayDate: In(Array.from(slotsDays).map((day) => new Date(day))),
      },
    });

    if (
      areValidTimeRanges([
        ...dto.slots,
        ...existingSlots.map((slot) => ({
          startTime: slot.startTime.substring(0, 5),
          endTime: slot.endTime.substring(0, 5),
          day: slot.dayDate,
        })),
      ]) === false
    ) {
      throw new BadRequestException(
        'Invalid or overlapping time ranges detected',
      );
    }

    return this.docScheduleSlotsRepository.save(slotsToSave);
  }

  async update(dto: UpdateDocScheduleSlotDto, slotId: number) {
    const slot = await this.docScheduleSlotsRepository.findOne({
      where: { id: slotId },
      relations: ['doctor'],
    });
    if (!slot) throw new BadRequestException('Schedule slot not found');

    slot.startTime = dto.startTime ?? slot.startTime;
    slot.endTime = dto.endTime ?? slot.endTime;
    slot.dayDate = dto.day ?? slot.dayDate;

    const existingSlots = await this.docScheduleSlotsRepository.find({
      where: {
        dayDate: slot.dayDate,
        doctor: { userId: slot.doctor.userId },
      },
    });

    if (
      !areValidTimeRanges([
        { ...slot, day: slot.dayDate },
        ...existingSlots
          .filter((s) => s.id !== slot.id)
          .map((s) => ({
            startTime: s.startTime.substring(0, 5),
            endTime: s.endTime.substring(0, 5),
            day: s.dayDate,
          })),
      ])
    ) {
      throw new BadRequestException(
        'Invalid or overlapping time ranges detected',
      );
    }

    return this.docScheduleSlotsRepository.save(slot);
  }

  async delete(slotId: number) {
    const slot = await this.docScheduleSlotsRepository.findOneBy({
      id: slotId,
    });
    if (!slot) throw new BadRequestException('Schedule slot not found');

    const result = await this.docScheduleSlotsRepository.delete({
      id: slotId,
      status: 'available',
    });

    if (result.affected === 0) {
      throw new BadRequestException('Only available slots can be deleted');
    }
  }
}
