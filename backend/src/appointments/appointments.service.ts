import { Injectable, NotFoundException, BadRequestException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, DataSource } from 'typeorm';
import { Appointment, AppointmentStatus } from './entities/appointment.entity';
import { CreateAppointmentDto } from './dtos/create-appointment.dto';
import { UpdateAppointmentStatusDto } from './dtos/update-appointment-status.dto';
import { DocScheduleSlot } from '../schedules/entities/doc-schedule-slot.entity';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Injectable()
export class AppointmentsService {
  constructor(
    @InjectRepository(Appointment)
    private readonly appointmentRepository: Repository<Appointment>,
    @InjectRepository(DocScheduleSlot)
    private readonly slotRepository: Repository<DocScheduleSlot>,
    private readonly dataSource: DataSource,
  ) { }

  async create(createAppointmentDto: CreateAppointmentDto, user: TokenUser): Promise<Appointment> {
    const queryRunner = this.dataSource.createQueryRunner();

    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      // 1. Lock the slot so no one else can book it concurrently
      const slot = await queryRunner.manager
        .createQueryBuilder(DocScheduleSlot, 'slot')
        .innerJoinAndSelect('slot.schedule', 'schedule')
        .innerJoinAndSelect('schedule.doctor', 'doctor')
        .where('slot.id = :slotId', { slotId: createAppointmentDto.slotId })
        .andWhere('doctor.userId = :doctorId', { doctorId: createAppointmentDto.doctorId })
        .setLock('pessimistic_write')
        .getOne();

      if (!slot) {
        throw new NotFoundException('Slot not found');
      }

      if (slot.status !== 'available') {
        throw new BadRequestException('Slot is not available');
      }

      // 2. Mark the slot as booked
      slot.status = 'booked';
      await queryRunner.manager.save(slot);

      // 3. Create the appointment
      const appointment = queryRunner.manager.create(Appointment, {
        patientId: user.id,
        doctorId: createAppointmentDto.doctorId,
        slotId: createAppointmentDto.slotId,
        status: AppointmentStatus.PENDING,
        bookedForName: createAppointmentDto.bookedForName,
        bookedForAge: createAppointmentDto.bookedForAge,
        bookedForGender: createAppointmentDto.bookedForGender,
        problemDescription: createAppointmentDto.problemDescription,
      });

      const savedAppointment = await queryRunner.manager.save(appointment);

      await queryRunner.commitTransaction();
      return savedAppointment;
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }

  async getForPatient(patientId: number): Promise<Appointment[]> {
    return this.appointmentRepository.find({
      where: { patientId },
      relations: ['doctor', 'doctor.user', 'slot', 'slot.schedule'],
      order: { createdAt: 'DESC' },
    });
  }

  async getForDoctor(doctorId: number): Promise<Appointment[]> {
    return this.appointmentRepository.find({
      where: { doctorId },
      relations: ['patient', 'patient.user', 'slot', 'slot.schedule'],
      order: { createdAt: 'DESC' },
    });
  }

  async getById(id: number): Promise<Appointment> {
    const appointment = await this.appointmentRepository.findOne({
      where: { id },
      relations: ['doctor', 'doctor.user', 'patient', 'patient.user', 'slot', 'slot.schedule'],
    });

    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }

    return appointment;
  }

  async updateStatus(id: number, updateDto: UpdateAppointmentStatusDto): Promise<Appointment> {
    const queryRunner = this.dataSource.createQueryRunner();

    await queryRunner.connect();
    await queryRunner.startTransaction();

    try {
      // ✅ Fixed: Use INNER JOIN instead of LEFT JOIN with FOR UPDATE
      const appointment = await queryRunner.manager
        .createQueryBuilder(Appointment, 'appointment')
        .innerJoinAndSelect('appointment.slot', 'slot')
        .where('appointment.id = :id', { id })
        .setLock('pessimistic_write')
        .getOne();

      if (!appointment) {
        throw new NotFoundException('Appointment not found');
      }

      appointment.status = updateDto.status;
      if (updateDto.cancellationReason) {
        appointment.cancellationReason = updateDto.cancellationReason;
      }

      if (updateDto.status === AppointmentStatus.CANCELLED) {
        // Free up the slot
        appointment.slot.status = 'available';
        await queryRunner.manager.save(appointment.slot);
      }

      const updatedAppointment = await queryRunner.manager.save(appointment);

      await queryRunner.commitTransaction();
      return updatedAppointment;
    } catch (err) {
      await queryRunner.rollbackTransaction();
      throw err;
    } finally {
      await queryRunner.release();
    }
  }
}
