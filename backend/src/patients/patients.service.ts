import { Injectable, NotFoundException } from '@nestjs/common';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Patient } from './entities/patient.entity';
import { UpdatePatientDto } from './dtos/update_patient.dto';
import { RelationType } from './types/patient.types';
import { GenericPatientRelation } from './interfaces/generic-patient-relation.interface';

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

  async create(userId: number): Promise<Patient> {
    return this.patientsRepository.save(
      this.patientsRepository.create({ userId }),
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

  async addRelation<T>(
    patient: Patient,
    relationType: RelationType,
    relationData: T,
  ): Promise<Patient> {
    const items = patient[relationType] as T[];
    items.push(relationData);
    await this.patientsRepository.save(patient);
    return patient;
  }

  async updateRelation<T extends GenericPatientRelation>(
    patient: Patient,
    relationType: RelationType,
    relationId: number,
    relationData: T,
  ): Promise<Patient> {
    const items = patient[relationType] as unknown as T[];
    const index = items.findIndex((item) => item.id === relationId);
    if (index === -1) {
      throw new NotFoundException(`${relationType} not found`);
    }
    Object.assign(items[index], relationData);
    items[index].updatedAt = new Date();
    await this.patientsRepository.save(patient);
    return patient;
  }

  async removeRelation<T extends GenericPatientRelation>(
    patient: Patient,
    relationType: RelationType,
    relationId: number,
  ): Promise<Patient> {
    const items = patient[relationType] as unknown as T[];
    const index = items.findIndex((item) => item.id === relationId);
    if (index === -1) {
      throw new NotFoundException(`${relationType} not found`);
    }
    const itemToRemove = items[index];
    items.splice(index, 1);
    await this.patientsRepository.manager.remove(itemToRemove);
    return patient;
  }
}
