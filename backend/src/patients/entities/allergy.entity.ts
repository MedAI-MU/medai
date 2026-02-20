import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { Patient } from './patient.entity';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';

@Entity()
export class Allergy extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, (patient) => patient.allergies, {
    onDelete: 'CASCADE',
  })
  patient: Patient;

  @Column({ length: 100 })
  name: string;

  @Column({ type: 'text', nullable: true })
  description: string;
}
