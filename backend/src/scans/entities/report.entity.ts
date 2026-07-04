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

  @Column()
  path: string;
}
