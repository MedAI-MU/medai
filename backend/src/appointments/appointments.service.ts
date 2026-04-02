import { Injectable, NotFoundException, BadRequestException, ForbiddenException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Appointment } from './entities/appointment.entity';
import { CreateAppointmentDto } from './dtos/create-appointment.dto';
import { CancelAppointmentDto } from './dtos/cancel-appointment.dto';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { DocScheduleSlot } from '../schedules/entities/doc-schedule-slot.entity';
import { Patient } from '../patients/entities/patient.entity';
import { Doctor } from '../doctors/entities/doctor.entity';

@Injectable()
export class AppointmentsService {
  constructor(
    @InjectRepository(Appointment)
    private readonly appointmentRepo: Repository<Appointment>,
    @InjectRepository(DocScheduleSlot)
    private readonly slotRepo: Repository<DocScheduleSlot>,
    @InjectRepository(Patient)
    private readonly patientRepo: Repository<Patient>,
    @InjectRepository(Doctor)
    private readonly doctorRepo: Repository<Doctor>,
  ) {}

  async create(createDto: CreateAppointmentDto, user: TokenUser): Promise<Appointment> {
    const { doctorId, slotId, problemDescription } = createDto;

    const patient = await this.patientRepo.findOne({ where: { userId: user.id }, relations: ['user'] });
    if (!patient) {
      throw new NotFoundException('Patient not found');
    }

    const doctor = await this.doctorRepo.findOne({ where: { userId: doctorId }, relations: ['user'] });
    if (!doctor) {
      throw new NotFoundException('Doctor not found');
    }

    const slot = await this.slotRepo.findOne({
      where: { id: slotId },
      relations: ['schedule', 'schedule.doctor'],
    });

    if (!slot) {
      throw new NotFoundException('Schedule slot not found');
    }

    if (slot.schedule.doctor.userId !== doctorId) {
      throw new BadRequestException('Slot does not belong to the given doctor');
    }

    if (slot.status !== 'available') {
      throw new BadRequestException('Slot is not available');
    }

    // Mark slot as booked
    slot.status = 'booked';
    await this.slotRepo.save(slot);

    // Create appointment
    const appointment = new Appointment({
      patient,
      doctor,
      slot,
      problemDescription,
      status: 'upcoming',
    });

    return this.appointmentRepo.save(appointment);
  }

  async findAllForUser(user: TokenUser): Promise<Appointment[]> {
    if (user.role === 'patient') {
      return this.appointmentRepo.find({
        where: { patient: { userId: user.id } },
        relations: ['doctor', 'doctor.user', 'slot', 'slot.schedule'],
        order: { createdAt: 'DESC' },
      });
    } else if (user.role === 'doctor') {
      // Find doctor by userId
      const doctor = await this.doctorRepo.findOne({ where: { user: { id: user.id } } });
      if (!doctor) {
        throw new NotFoundException('Doctor not found for the current user');
      }
      return this.appointmentRepo.find({
        where: { doctor: { userId: doctor.userId } },
        relations: ['patient', 'patient.user', 'slot', 'slot.schedule'],
        order: { createdAt: 'DESC' },
      });
    } else {
      return [];
    }
  }

  async findOne(id: number, user: TokenUser): Promise<Appointment> {
    const appointment = await this.appointmentRepo.findOne({
      where: { id },
      relations: ['patient', 'patient.user', 'doctor', 'doctor.user', 'slot', 'slot.schedule'],
    });

    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }

    if (user.role === 'patient' && appointment.patient.userId !== user.id) {
      throw new ForbiddenException('You do not have access to this appointment');
    }

    if (user.role === 'doctor' && appointment.doctor.user.id !== user.id) {
      throw new ForbiddenException('You do not have access to this appointment');
    }

    return appointment;
  }

  async cancel(id: number, cancelDto: CancelAppointmentDto, user: TokenUser): Promise<Appointment> {
    const appointment = await this.findOne(id, user);

    if (appointment.status === 'cancelled') {
      throw new BadRequestException('Appointment is already cancelled');
    }

    appointment.status = 'cancelled';
    appointment.cancelReason = cancelDto.otherReason || cancelDto.reasonId || 'Cancelled by user';

    // Free the slot
    if (appointment.slot) {
      appointment.slot.status = 'available';
      await this.slotRepo.save(appointment.slot);
    }

    return this.appointmentRepo.save(appointment);
  }
}
