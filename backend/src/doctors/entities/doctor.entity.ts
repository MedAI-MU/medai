import {
  Column,
  Entity,
  PrimaryColumn,
  OneToOne,
  JoinColumn,
  OneToMany,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';
import { DocScheduleTemplate } from '../../schedules/entities/doc-schedule-template.entity';
import { DocSchedule } from '../../schedules/entities/doc-schedule.entity';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { DoctorSpeciality } from './doctor-speciality.entity';
import { Diagnosis } from '../../diagnosis/entities/diagnosis.entity';

@Entity()
export class Doctor extends TimestampEntity {
  @PrimaryColumn()
  userId: number;

  @OneToOne(() => User, (user) => user.doctor, { eager: true })
  @JoinColumn({ name: 'userId' })
  user: User;

  @OneToMany(() => DocScheduleTemplate, (template) => template.doctor)
  scheduleTemplates: DocScheduleTemplate[];

  @OneToMany(() => DocSchedule, (schedule) => schedule.doctor)
  schedules: DocSchedule[];

  @OneToMany(() => DoctorSpeciality, (speciality) => speciality.doctor, {
    cascade: true,
  })
  specialities: DoctorSpeciality[];

  @Column({ type: 'text', nullable: true })
  about?: string;

  @OneToMany(() => Diagnosis, (diagnosis) => diagnosis.doctor)
  diagnoses: Diagnosis[];

  constructor(partial: Partial<Doctor>) {
    super();
    Object.assign(this, partial);
  }
}
