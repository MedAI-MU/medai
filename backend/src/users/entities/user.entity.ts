import {
  Column,
  CreateDateColumn,
  Entity,
  OneToMany,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
  OneToOne,
} from 'typeorm';
import { RefreshToken } from './refresh-token.entity';
import { Doctor } from '../../doctors/entities/doctor.entity';
import type { UserRoles } from '../types/role.types';
import { DocScheduleTemplate } from '../../schedules/entities/doc-schedule-template.entity';
import { DocScheduleSlot } from '../../schedules/entities/doc-schedule-slot.entity';
@Entity()
export class User {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ length: 100 })
  name: string;

  @Column({ unique: true, length: 256 })
  email: string;

  @Column()
  password: string;

  @Column({ unique: true, length: 20 })
  phone: string;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;

  @OneToMany(() => RefreshToken, (refreshToken) => refreshToken.user)
  refreshTokens: RefreshToken[];

  @OneToOne(() => Doctor, (doctor) => doctor.user)
  doctor?: Doctor;

  @Column({ type: 'varchar' })
  role: UserRoles;

  @OneToMany(() => DocScheduleTemplate, (template) => template.createdBy)
  doctorScheduleTemplates?: DocScheduleTemplate[];

  @OneToMany(() => DocScheduleSlot, (slot) => slot.createdBy)
  doctorScheduleSlots?: DocScheduleSlot[];

  constructor(partial: Partial<User>) {
    Object.assign(this, partial);
  }
}
