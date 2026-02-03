import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { Patient } from './patient.entity';
import { FamilyRelationEnum } from '../enums/patients.enum';
import { TimestampEntity } from '../../common/entities/timestamp.entity';
import type { FamilyRelation } from '../types/patient.types';

@Entity()
export class EmergencyContact extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, (patient) => patient.emergencyContacts, {
    onDelete: 'CASCADE',
  })
  patient: Patient;

  @Column({ length: 100 })
  name: string;

  @Column({ type: 'enum', enum: FamilyRelationEnum })
  relation: FamilyRelation;

  @Column({ length: 15 })
  phoneNumber: string;

  @Column({ length: 100 })
  email: string;

  @Column({ type: 'text' })
  address: string;

  @Column({ type: 'text', nullable: true })
  notes: string;
}
