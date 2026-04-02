import {
  Column,
  Entity,
  Index,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Patient } from '../../patients/entities/patient.entity';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { DocScheduleSlot } from '../../schedules/entities/doc-schedule-slot.entity';
import type { AppointmentStatus } from '../types/appointment-status.type';

@Entity()
@Index('IDX_appointment_patient', ['patient'])
@Index('IDX_appointment_doctor', ['doctor'])
export class Appointment {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient)
  @JoinColumn({ name: 'patientId' })
  patient: Patient;

  @ManyToOne(() => Doctor)
  @JoinColumn({ name: 'doctorId' })
  doctor: Doctor;

  @ManyToOne(() => DocScheduleSlot)
  @JoinColumn({ name: 'slotId' })
  slot: DocScheduleSlot;

  @Column({
    type: 'varchar',
    length: 20,
    default: 'upcoming',
  })
  status: AppointmentStatus;

  @Column({ type: 'text', nullable: true })
  problemDescription: string;

  @Column({ type: 'varchar', length: 255, nullable: true })
  cancelReason: string;

  @Column({ type: 'int', nullable: true })
  rating: number;

  @Column({ type: 'text', nullable: true })
  reviewComment: string;

  @Column({ type: 'timestamp', default: () => 'CURRENT_TIMESTAMP' })
  createdAt: Date;

  constructor(partial: Partial<Appointment>) {
    Object.assign(this, partial);
  }
}
