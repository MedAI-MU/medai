import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { Appointment } from '../../appointments/entities/appointment.entity';
import { VoiceReportStatusEnum } from '../enums/voice-report-status.enum';
import type { VoiceReportStatus } from '../types/voice-report-status.type';

@Entity()
export class VoiceReport extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'patientUserId' })
  patient: Patient;

  @Column()
  patientUserId: number;

  @ManyToOne(() => Doctor, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'doctorUserId' })
  doctor: Doctor;

  @Column()
  doctorUserId: number;

  @ManyToOne(() => Appointment, { nullable: true, onDelete: 'SET NULL' })
  @JoinColumn({ name: 'appointmentId' })
  appointment: Appointment | null;

  @Column({ type: 'int', nullable: true })
  appointmentId: number | null;

  @Column({
    type: 'enum',
    enum: VoiceReportStatusEnum,
    default: VoiceReportStatusEnum.QUEUED,
  })
  status: VoiceReportStatus;

  @Column()
  originalFileName: string;

  @Column()
  originalMimeType: string;

  @Column({ type: 'varchar', nullable: true })
  audioUrl: string | null;

  @Column({ type: 'text', nullable: true })
  transcription: string | null;

  @Column({ type: 'jsonb', nullable: true })
  clinicalReport: Record<string, unknown> | null;

  @Column({ type: 'text', nullable: true })
  errorMessage: string | null;
}
