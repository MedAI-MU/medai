import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  ManyToOne,
  JoinColumn,
  Index,
} from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { DocScheduleSlot } from '../../schedules/entities/doc-schedule-slot.entity';

export enum AppointmentStatus {
  PENDING = 'pending',
  CONFIRMED = 'confirmed',
  COMPLETED = 'completed',
  CANCELLED = 'cancelled',
}

@Entity()
export class Appointment extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient)
  @JoinColumn({ name: 'patientId' })
  @Index()
  patient: Patient;

  @Column({ type: 'int' })
  patientId: number;

  @ManyToOne(() => Doctor)
  @JoinColumn({ name: 'doctorId' })
  @Index()
  doctor: Doctor;

  @Column({ type: 'int' })
  doctorId: number;

  @ManyToOne(() => DocScheduleSlot)
  @JoinColumn({ name: 'slotId' })
  @Index()
  slot: DocScheduleSlot;

  @Column({ type: 'int' })
  slotId: number;

  @Column({
    type: 'enum',
    enum: AppointmentStatus,
    default: AppointmentStatus.PENDING,
  })
  status: AppointmentStatus;

  // Allows booking for someone else (as seen in BookingScreen)
  @Column({ type: 'varchar', length: 100, nullable: true })
  bookedForName: string;

  @Column({ type: 'varchar', length: 20, nullable: true })
  bookedForAge: string;

  @Column({ type: 'varchar', length: 20, nullable: true })
  bookedForGender: string;

  @Column({ type: 'text', nullable: true })
  problemDescription: string;

  // Cancellation Reason
  @Column({ type: 'text', nullable: true })
  cancellationReason: string;

  // Reviews
  @Column({ type: 'int', nullable: true })
  rating: number;

  @Column({ type: 'text', nullable: true })
  reviewComment: string;
}
