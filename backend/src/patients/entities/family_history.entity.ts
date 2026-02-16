import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { Patient } from './patient.entity';
import { FamilyRelationEnum } from '../enums/patients.enum';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import type { FamilyRelation } from '../types/patient.types';

@Entity()
export class FamilyHistory extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, (patient) => patient.familyHistories, {
    onDelete: 'CASCADE',
  })
  patient: Patient;

  @Column({ type: 'enum', enum: FamilyRelationEnum })
  relation: FamilyRelation;

  @Column({ length: 100 })
  condition: string;

  @Column({ type: 'text', nullable: true })
  notes: string;
}
