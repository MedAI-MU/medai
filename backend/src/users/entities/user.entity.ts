import {
  Column,
  Entity,
  OneToMany,
  PrimaryGeneratedColumn,
  Index,
  OneToOne,
} from 'typeorm';
import { RefreshToken } from './refresh-token.entity';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import type { UserRoles, UserStatus } from '../types/role.types';

import { DocScheduleTemplate } from '../../schedules/entities/doc-schedule-template.entity';
import { DocSchedule } from '../../schedules/entities/doc-schedule.entity';
import { GenderEnum } from '../../shared/enums/gender.enum';
import type { Gender } from '../../shared/types/gender.type';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Exclude } from 'class-transformer';

@Index('idx_user_name_trgm', { synchronize: false })
@Entity()
export class User extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ length: 100 })
  name: string;

  @Exclude()
  @Column({ unique: true, length: 256 })
  email: string;

  @Exclude()
  @Column()
  password: string;

  @Exclude()
  @Column({ unique: true, length: 20 })
  phone: string;

  @Exclude()
  @Column({ type: 'date', nullable: true })
  birthDate?: Date;

  @OneToMany(() => RefreshToken, (refreshToken) => refreshToken.user)
  refreshTokens: RefreshToken[];

  @Column({ type: 'enum', enum: GenderEnum, nullable: true })
  gender?: Gender;

  @Column({ type: 'varchar' })
  role: UserRoles;

  @Column({ type: 'varchar', default: 'pending' })
  status: UserStatus;

  @Exclude()
  @Column({ default: false })
  emailVerified: boolean;

  @Exclude()
  @Column({ type: 'varchar', nullable: true })
  verificationToken?: string;

  @Exclude()
  @Column({ type: 'varchar', nullable: true })
  resetPasswordToken?: string;

  @Exclude()
  @Column({ type: 'timestamp', nullable: true })
  resetPasswordExpiresAt?: Date;

  @OneToOne(() => Doctor, (doctor) => doctor.user)
  doctor?: Doctor;

  @OneToOne(() => Patient, (patient) => patient.user)
  patient?: Patient;

  @OneToMany(() => DocScheduleTemplate, (template) => template.createdBy)
  doctorScheduleTemplates?: DocScheduleTemplate[];

  @OneToMany(() => DocSchedule, (schedule) => schedule.createdBy)
  doctorSchedules?: DocSchedule[];

  constructor(partial: Partial<User>) {
    super();
    Object.assign(this, partial);
  }
}
