import { Injectable } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Patient } from './entities/patient.entity';
import { UpdatePatientDto } from './dtos/update_patient.dto';

@Injectable()
export class PatientsService {
  constructor(
    @InjectRepository(Patient)
    private patientsRepository: Repository<Patient>,
  ) {}

  async findAll(): Promise<Patient[]> {
    return this.patientsRepository.find();
  }
  // TODO: Filter patients by the supervised doctor
  async findOne(id: number): Promise<Patient | null> {
    return this.patientsRepository.findOne({
      where: { userId: id },
      relations: {
        allergies: true,
        chronicDiseases: true,
        surgeries: true,
        familyHistories: true,
        emergencyContacts: true,
      },
    });
  }

  async create(patientData: Patient): Promise<Patient> {
    return this.patientsRepository.save(
      this.patientsRepository.create(patientData),
    );
  }

  async update(
    updatePatientDto: UpdatePatientDto,
    id: number,
  ): Promise<Patient> {
    await this.patientsRepository.update(id, updatePatientDto);
    return (await this.patientsRepository.findOne({
      where: {
        userId: id,
      },
    })) as Patient;
  }
}
