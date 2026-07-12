import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Scan } from './scan.entity';
import { Appointment } from '../../appointments/entities/appointment.entity';
import { ReportAnalysisStatusEnum } from '../../report-analysis/enums/report-analysis-status.enum';

@Entity()
export class Report extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'patientUserId' })
  patient: Patient;

  @Column()
  patientUserId: number;

  @ManyToOne(() => Scan, { nullable: true, onDelete: 'SET NULL' })
  scan: Scan | null;

  @Column({ nullable: true })
  scanId: number | null;

  @ManyToOne(() => Appointment, { nullable: true, onDelete: 'SET NULL' })
  @JoinColumn({ name: 'appointmentId' })
  appointment: Appointment | null;

  @Column({ nullable: true })
  appointmentId: number | null;

  @Column()
  path: string;

  @Column({
    type: 'enum',
    enum: ReportAnalysisStatusEnum,
    nullable: true,
  })
  analysisStatus: ReportAnalysisStatusEnum | null;

  @Column({ type: 'jsonb', nullable: true })
  analysisResult: Record<string, unknown> | null;

  @Column({ type: 'text', nullable: true })
  analysisError: string | null;
}
