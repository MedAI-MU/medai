import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Appointment } from '../../appointments/entities/appointment.entity';
import { ScanImage } from './scan-image.entity';
import { Report } from './report.entity';

@Entity()
export class Scan extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'patientUserId' })
  patient: Patient;

  @Column()
  patientUserId: number;

  @ManyToOne(() => Appointment, { nullable: true, onDelete: 'SET NULL' })
  @JoinColumn({ name: 'appointmentId' })
  appointment: Appointment | null;

  @Column({ nullable: true })
  appointmentId: number | null;

  @OneToMany(() => ScanImage, (image) => image.scan, {
    cascade: ['insert', 'update'],
  })
  images: ScanImage[];

  @OneToMany(() => Report, (report) => report.scan, {
    cascade: ['insert', 'update'],
  })
  reports: Report[];
}
