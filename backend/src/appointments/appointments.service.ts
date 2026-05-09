import {
  BadRequestException,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Appointment } from './entities/appointment.entity';
import { CreateAppointmentDto } from './dtos/create-appointment.dto';
import { UpdateAppointmentStatusDto } from './dtos/update-appointment-status.dto';
import { ReviewAppointmentDto } from './dtos/review-appointment.dto';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { DocScheduleSlot } from '../schedules/entities/doc-schedule-slot.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { AppointmentStatusEnum } from './enums/appointment-status.enum';

@Injectable()
export class AppointmentsService {
  constructor(
    @InjectRepository(Appointment)
    private readonly appointmentsRepository: Repository<Appointment>,
    @InjectRepository(DocScheduleSlot)
    private readonly slotsRepository: Repository<DocScheduleSlot>,
    @InjectRepository(Doctor)
    private readonly doctorsRepository: Repository<Doctor>,
  ) {}

  async create(
    patientUserId: number,
    dto: CreateAppointmentDto,
  ): Promise<Appointment | null> {
    const slot = await this.slotsRepository.findOne({
      where: { id: dto.slotId },
      relations: { schedule: { doctor: true } },
    });

    if (!slot) {
      return null;
    }
    if (slot.status !== 'available') {
      throw new BadRequestException('Schedule slot is not available');
    }
    if (slot.schedule.doctor.userId !== dto.doctorId) {
      throw new BadRequestException(
        'Schedule slot does not belong to the specified doctor',
      );
    }

    slot.status = 'booked';
    await this.slotsRepository.save(slot);

    const appointment = this.appointmentsRepository.create({
      patientUserId,
      doctorUserId: dto.doctorId,
      scheduleSlotId: dto.slotId,
    });
    return await this.appointmentsRepository.save(appointment);
  }

  async findByPatient(patientUserId: number): Promise<Appointment[]> {
    return this.appointmentsRepository.find({
      where: { patientUserId },
      relations: {
        doctor: { user: true, specialities: { speciality: true } },
        scheduleSlot: { schedule: true },
        confirmedBy: true,
      },
    });
  }

  async findByDoctor(doctorUserId: number): Promise<Appointment[] | null> {
    const doctor = await this.doctorsRepository.findOne({
      where: { userId: doctorUserId },
    });
    if (!doctor) {
      return null;
    }
    return this.appointmentsRepository.find({
      where: { doctorUserId },
      relations: {
        patient: { user: true },
        scheduleSlot: { schedule: true },
      },
    });
  }

  async findAll(): Promise<Appointment[]> {
    return this.appointmentsRepository.find({
      relations: {
        patient: { user: true },
        doctor: { user: true },
        scheduleSlot: { schedule: true },
        confirmedBy: true,
      },
    });
  }

  async addReview(
    id: number,
    dto: ReviewAppointmentDto,
    patientUserId: number,
  ): Promise<Appointment | null> {
    const appointment = await this.appointmentsRepository.findOne({
      where: { id },
    });

    if (!appointment) {
      return null;
    }

    if (appointment.patientUserId !== patientUserId) {
      throw new ForbiddenException('You can only review your own appointments');
    }

    if (appointment.status !== 'finished') {
      throw new BadRequestException(
        'Only finished appointments can be reviewed',
      );
    }

    appointment.rating = dto.rating;
    appointment.review = dto.review ?? null;
    return this.appointmentsRepository.save(appointment);
  }

  async delete(id: number): Promise<boolean> {
    const result = await this.appointmentsRepository.delete(id);
    return (result.affected ?? 0) > 0;
  }

  async updateStatus(
    id: number,
    dto: UpdateAppointmentStatusDto,
    currentUser: TokenUser,
  ): Promise<Appointment | null> {
    const appointment = await this.appointmentsRepository.findOne({
      where: { id },
      relations: { scheduleSlot: true },
    });

    if (!appointment) {
      return null;
    }

    if (currentUser.role === 'patient') {
      if (appointment.patientUserId !== currentUser.id) {
        throw new ForbiddenException(
          'You can only update your own appointments',
        );
      }
      if (dto.status !== AppointmentStatusEnum.CANCELLED) {
        throw new ForbiddenException('Patients can only cancel appointments');
      }
    }

    if (
      dto.status === AppointmentStatusEnum.CANCELLED &&
      appointment.status !== 'cancelled'
    ) {
      appointment.scheduleSlot.status = 'available';
      await this.slotsRepository.save(appointment.scheduleSlot);
    }

    if (
      currentUser.role === 'secretary' &&
      dto.status === AppointmentStatusEnum.CONFIRMED
    ) {
      appointment.confirmedByUserId = currentUser.id;
    }

    appointment.status = dto.status;
    return this.appointmentsRepository.save(appointment);
  }
}
