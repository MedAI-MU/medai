import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  OneToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { DocScheduleSlot } from '../../schedules/entities/doc-schedule-slot.entity';
import { User } from '../../users/entities/user.entity';
import { AppointmentStatusEnum } from '../enums/appointment-status.enum';
import type { AppointmentStatus } from '../types/appointment-status.type';

@Entity()
export class Appointment extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'patientUserId' })
  patient: Patient;

  @Column()
  patientUserId: number;

  @ManyToOne(() => Doctor, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'doctorUserId' })
  doctor: Doctor;

  @Column()
  doctorUserId: number;

  @OneToOne(() => DocScheduleSlot, { onDelete: 'RESTRICT' })
  @JoinColumn({ name: 'scheduleSlotId' })
  scheduleSlot: DocScheduleSlot;

  @Column()
  scheduleSlotId: number;

  @Column({
    type: 'enum',
    enum: AppointmentStatusEnum,
    default: AppointmentStatusEnum.PENDING,
  })
  status: AppointmentStatus;

  @ManyToOne(() => User, { nullable: true, onDelete: 'SET NULL' })
  @JoinColumn({ name: 'confirmedByUserId' })
  confirmedBy: User | null;

  @Column({ nullable: true })
  confirmedByUserId: number | null;

  @Column({ type: 'int', nullable: true })
  rating: number | null;

  @Column({ type: 'text', nullable: true })
  review: string | null;
}
