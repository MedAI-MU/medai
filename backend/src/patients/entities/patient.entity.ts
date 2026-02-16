import {
  Entity,
  Column,
  OneToOne,
  OneToMany,
  PrimaryColumn,
  JoinColumn,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';
import { Allergy } from './allergy.entity';
import { ChronicDisease } from './chronic_disease.entity';
import { Surgery } from './surgery.entity';
import { FamilyHistory } from './family_history.entity';
import { EmergencyContact } from './emergency_contact.entity';
import {
  GenderEnum,
  MaritalStatusEnum,
  BloodTypeEnum,
} from '../enums/patients.enum';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import type { BloodType, Gender, MaritalStatus } from '../types/patient.types';

// TODO: Add surgery, family history, emergency contact, medications

@Entity()
export class Patient extends TimestampEntity {
  @PrimaryColumn()
  userId: number;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'userId' })
  user: User;

  @Column({ type: 'date', nullable: true })
  birthDate: Date;

  @Column({ type: 'float', nullable: true })
  height: number;

  @Column({ type: 'float', nullable: true })
  weight: number;

  @Column({ type: 'enum', enum: GenderEnum })
  gender: Gender;

  @Column({ type: 'enum', enum: BloodTypeEnum, nullable: true })
  bloodType: BloodType;

  @Column({ type: 'enum', enum: MaritalStatusEnum, nullable: true })
  maritalStatus: MaritalStatus;

  @OneToMany(() => Allergy, (allergy) => allergy.patient, {
    cascade: ['insert', 'update'],
  })
  allergies: Allergy[];

  @OneToMany(() => ChronicDisease, (disease) => disease.patient, {
    cascade: ['insert', 'update'],
  })
  chronicDiseases: ChronicDisease[];

  @OneToMany(() => Surgery, (surgery) => surgery.patient, {
    cascade: ['insert', 'update'],
  })
  surgeries: Surgery[];

  @OneToMany(() => FamilyHistory, (history) => history.patient, {
    cascade: ['insert', 'update'],
  })
  familyHistories: FamilyHistory[];

  @OneToMany(() => EmergencyContact, (contact) => contact.patient, {
    cascade: ['insert', 'update'],
  })
  emergencyContacts: EmergencyContact[];
}
