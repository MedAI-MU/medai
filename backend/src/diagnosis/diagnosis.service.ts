import {
  BadRequestException,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Diagnosis } from './entities/diagnosis.entity';
import { Appointment } from '../appointments/entities/appointment.entity';
import { CreateDiagnosisDto } from './dtos/create-diagnosis.dto';
import { UpdateDiagnosisDto } from './dtos/update-diagnosis.dto';
import { UpdateSymptomsDto } from './dtos/update-symptoms.dto';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Injectable()
export class DiagnosisService {
  constructor(
    @InjectRepository(Diagnosis)
    private readonly diagnosesRepository: Repository<Diagnosis>,
    @InjectRepository(Appointment)
    private readonly appointmentsRepository: Repository<Appointment>,
  ) {}

  private async assertOwnsDiagnosis(
    id: number,
    currentUser: TokenUser,
  ): Promise<Diagnosis> {
    const diagnosis = await this.diagnosesRepository.findOne({
      where: { id },
    });
    if (!diagnosis) throw new NotFoundException('Diagnosis not found');

    if (
      currentUser.role === 'patient' &&
      diagnosis.patientUserId !== currentUser.id
    ) {
      throw new ForbiddenException('You can only access your own diagnoses');
    }

    if (
      currentUser.role === 'doctor' &&
      diagnosis.doctorUserId !== currentUser.id
    ) {
      throw new ForbiddenException('You can only access your own diagnoses');
    }

    return diagnosis;
  }

  async create(
    dto: CreateDiagnosisDto,
    doctorUserId: number,
  ): Promise<Diagnosis> {
    const appointment = await this.appointmentsRepository.findOne({
      where: { id: dto.appointmentId },
    });
    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }
    if (appointment.doctorUserId !== doctorUserId) {
      throw new BadRequestException('This appointment does not belong to you');
    }
    if (appointment.patientUserId !== dto.patientUserId) {
      throw new BadRequestException('Patient does not match the appointment');
    }

    const existing = await this.diagnosesRepository.findOne({
      where: { appointmentId: dto.appointmentId },
    });
    if (existing) {
      throw new BadRequestException(
        'A diagnosis already exists for this appointment',
      );
    }

    const diagnosis = this.diagnosesRepository.create({
      patientUserId: dto.patientUserId,
      doctorUserId,
      appointmentId: dto.appointmentId,
      symptoms: dto.symptoms,
      summary: dto.summary,
    });
    return this.diagnosesRepository.save(diagnosis);
  }

  async findAll(currentUser: TokenUser): Promise<Diagnosis[]> {
    if (currentUser.role === 'patient') {
      return this.diagnosesRepository.find({
        where: { patientUserId: currentUser.id },
        order: { createdAt: 'DESC' },
      });
    }

    if (currentUser.role === 'doctor') {
      return this.diagnosesRepository.find({
        where: { doctorUserId: currentUser.id },
        order: { createdAt: 'DESC' },
      });
    }

    throw new ForbiddenException('Access denied');
  }

  async findOne(id: number, currentUser: TokenUser): Promise<Diagnosis> {
    return this.assertOwnsDiagnosis(id, currentUser);
  }

  async update(
    id: number,
    dto: UpdateDiagnosisDto,
    currentUser: TokenUser,
  ): Promise<Diagnosis> {
    if (currentUser.role !== 'doctor') {
      throw new ForbiddenException('Only doctors can update diagnoses');
    }

    const diagnosis = await this.assertOwnsDiagnosis(id, currentUser);
    diagnosis.symptoms = dto.symptoms;
    diagnosis.summary = dto.summary;
    return this.diagnosesRepository.save(diagnosis);
  }

  async updateSymptoms(
    id: number,
    dto: UpdateSymptomsDto,
    currentUser: TokenUser,
  ): Promise<Diagnosis> {
    const diagnosis = await this.assertOwnsDiagnosis(id, currentUser);
    diagnosis.symptoms = dto.symptoms;
    return this.diagnosesRepository.save(diagnosis);
  }

  async delete(id: number, currentUser: TokenUser): Promise<void> {
    if (currentUser.role !== 'doctor') {
      throw new ForbiddenException('Only doctors can delete diagnoses');
    }

    const diagnosis = await this.assertOwnsDiagnosis(id, currentUser);
    await this.diagnosesRepository.remove(diagnosis);
  }
}
