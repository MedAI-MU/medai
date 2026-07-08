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
import { Appointment } from '../../appointments/entities/appointment.entity';

@Entity()
export class Diagnosis extends TimestampEntity {
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

  @OneToOne(() => Appointment, { onDelete: 'CASCADE' })
  @JoinColumn({ name: 'appointmentId' })
  appointment: Appointment;

  @Column()
  appointmentId: number;

  @Column({ type: 'text' })
  symptoms: string;

  @Column({ type: 'text' })
  summary: string;
}
