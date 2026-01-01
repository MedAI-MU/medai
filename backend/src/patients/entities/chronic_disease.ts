import { Entity, PrimaryGeneratedColumn, Column, ManyToOne } from 'typeorm';
import { Patient } from './patient.entity';

@Entity()
export class ChronicDisease {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Patient, (patient) => patient.chronicDiseases, {
    onDelete: 'CASCADE',
  })
  patient: Patient;

  @Column({ length: 100 })
  name: string;

  @Column({ type: 'text', nullable: true })
  description: string;

  @Column({ type: 'date', nullable: true })
  diagnosisDate: Date;
}
