import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  OneToOne,
  OneToMany,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';
import { Allergy } from './allergy.entity';
import { ChronicDisease } from './chronic_disease.entity';
import { Surgery } from './surgery.entity';
import { FamilyHistory } from './family_history.entity';
import { EmergencyContact } from './emergency_contact.entity';
import { Gender, MaritalStatus, BloodType } from '../enums/patients.enum';
import { TimestampEntity } from '../../common/entities/timestamp.entity';

// TODO: Add surgery, family history, emergency contact, medications

@Entity()
export class Patient extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @OneToOne(() => User, { onDelete: 'CASCADE' })
  user: User;

  @Column({ type: 'date', nullable: true })
  birthDate: Date;

  @Column({ type: 'float', nullable: true })
  height: number;

  @Column({ type: 'float', nullable: true })
  weight: number;

  @Column({ type: 'enum', enum: Gender })
  gender: Gender;

  @Column({ type: 'enum', enum: BloodType, nullable: true })
  bloodType: BloodType;

  @Column({ type: 'enum', enum: MaritalStatus, nullable: true })
  maritalStatus: MaritalStatus;

  @OneToMany(() => Allergy, (allergy) => allergy.patient)
  allergies: Allergy[];

  @OneToMany(() => ChronicDisease, (disease) => disease.patient)
  chronicDiseases: ChronicDisease[];

  @OneToMany(() => Surgery, (surgery) => surgery.patient)
  surgeries: Surgery[];

  @OneToMany(() => FamilyHistory, (history) => history.patient)
  familyHistories: FamilyHistory[];

  @OneToMany(() => EmergencyContact, (contact) => contact.patient)
  emergencyContacts: EmergencyContact[];
}
