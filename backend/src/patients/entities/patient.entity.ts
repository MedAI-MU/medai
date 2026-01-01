import {
  Entity,
  Column,
  PrimaryGeneratedColumn,
  OneToOne,
  OneToMany,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';
import { Allergy } from './allergy.entity';
import { ChronicDisease } from './chronic_disease';
import { Gender, MaritalStatus, BloodType } from '../enums/patients.enum';

// TODO: Add surgery, family history, emergency contact, medications

@Entity()
export class Patient {
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
}
