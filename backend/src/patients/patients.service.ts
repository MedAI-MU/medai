import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Patient } from './entities/patient.entity';

@Injectable()
export class PatientsService {
  constructor(
    @InjectRepository(Patient)
    private patientsRepository: Repository<Patient>,
  ) {}

  async findAll(): Promise<Patient[]> {
    // TODO: Later, we will filter patients that are supervised by the requesting doctor.
    return this.patientsRepository.find();
  }

  async findOne(id: number): Promise<Patient | null> {
    // TODO: Later, we will ensure the requesting doctor has access to this patient.
    return this.patientsRepository.findOne({
      where: { id },
      relations: {
        allergies: true,
        chronicDiseases: true,
        surgeries: true,
        familyHistories: true,
        emergencyContacts: true,
      },
    });
  }

  async create(patientData: Partial<Patient>): Promise<Patient> {
    return this.patientsRepository.save(
      this.patientsRepository.create(patientData),
    );
  }
}
