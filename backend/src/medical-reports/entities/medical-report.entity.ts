import { Entity, Column, PrimaryGeneratedColumn, ManyToOne, JoinColumn } from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Patient } from '../../patients/entities/patient.entity';
import { Doctor } from '../../doctors/entities/doctor.entity';

@Entity()
export class MedicalReport extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  patientId: number;

  @ManyToOne(() => Patient, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'patientId' })
  patient: Patient;

  @Column({ nullable: true })
  doctorId: number;

  @ManyToOne(() => Doctor, { onDelete: 'SET NULL' })
  @JoinColumn({ name: 'doctorId' })
  doctor: Doctor;

  @Column({ type: 'text', nullable: true })
  scanImageUrl: string | null;

  @Column({ type: 'text' })
  generatedReport: string;

  @Column({ default: 'GENERATED' })
  status: string;
}
