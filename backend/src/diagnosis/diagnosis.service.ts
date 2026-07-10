import {
  BadRequestException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { In, Repository } from 'typeorm';
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

  private async getDoctorPatientIds(doctorUserId: number): Promise<number[]> {
    const appointments = await this.appointmentsRepository.find({
      where: { doctorUserId },
      select: { patientUserId: true },
    });
    return [...new Set(appointments.map((a) => a.patientUserId))];
  }

  async create(
    dto: CreateDiagnosisDto,
    doctorUserId: number,
    patientUserId: number,
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
    if (appointment.patientUserId !== patientUserId) {
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
      patientUserId,
      doctorUserId,
      appointmentId: dto.appointmentId,
      symptoms: dto.symptoms,
      summary: dto.summary,
    });
    return this.diagnosesRepository.save(diagnosis);
  }

  async findAll(currentUser: TokenUser): Promise<Diagnosis[]> {
    if (currentUser.role === 'doctor') {
      const patientIds = await this.getDoctorPatientIds(currentUser.id);
      if (patientIds.length === 0) return [];
      return this.diagnosesRepository.find({
        where: { patientUserId: In(patientIds) },
        order: { createdAt: 'DESC' },
      });
    }

    return this.diagnosesRepository.find({
      order: { createdAt: 'DESC' },
    });
  }

  async findByPatient(patientUserId: number): Promise<Diagnosis[]> {
    return this.diagnosesRepository.find({
      where: { patientUserId },
      order: { createdAt: 'DESC' },
    });
  }

  async findOne(id: number): Promise<Diagnosis> {
    const diagnosis = await this.diagnosesRepository.findOne({
      where: { id },
    });
    if (!diagnosis) throw new NotFoundException('Diagnosis not found');
    return diagnosis;
  }

  async update(id: number, dto: UpdateDiagnosisDto): Promise<Diagnosis> {
    const diagnosis = await this.diagnosesRepository.findOne({
      where: { id },
    });
    if (!diagnosis) throw new NotFoundException('Diagnosis not found');
    diagnosis.symptoms = dto.symptoms;
    diagnosis.summary = dto.summary;
    return this.diagnosesRepository.save(diagnosis);
  }

  async updateSymptoms(id: number, dto: UpdateSymptomsDto): Promise<Diagnosis> {
    const diagnosis = await this.diagnosesRepository.findOne({
      where: { id },
    });
    if (!diagnosis) throw new NotFoundException('Diagnosis not found');
    diagnosis.symptoms = dto.symptoms;
    return this.diagnosesRepository.save(diagnosis);
  }

  async delete(id: number): Promise<void> {
    const diagnosis = await this.diagnosesRepository.findOne({
      where: { id },
    });
    if (!diagnosis) throw new NotFoundException('Diagnosis not found');
    await this.diagnosesRepository.remove(diagnosis);
  }
}
