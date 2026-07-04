import { Entity, Column, PrimaryGeneratedColumn, ManyToOne } from 'typeorm';
import { Doctor } from './doctor.entity';
import { Speciality } from './speciality.entity';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';

@Entity()
export class DoctorSpeciality extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Doctor, (doctor) => doctor.specialities, {
    onDelete: 'CASCADE',
  })
  doctor: Doctor;

  @ManyToOne(() => Speciality)
  speciality: Speciality;

  @Column({ default: false })
  isPrimary: boolean;

  @Column({ default: 0 })
  yearsOfExperience: number;
}
