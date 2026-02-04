import {
  Entity,
  PrimaryColumn,
  Column,
  OneToOne,
  JoinColumn,
  OneToMany,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';
import { DocScheduleTemplate } from '../../schedules/entities/doc-schedule-template.entity';
import { DocSchedule } from '../../schedules/entities/doc-schedule.entity';

@Entity()
export class Doctor {
  @PrimaryColumn()
  userId: number;

  @OneToOne(() => User, (user) => user.doctor, { eager: true })
  @JoinColumn({ name: 'userId' })
  user: User;

  @Column()
  specialty: string;

  @OneToMany(() => DocScheduleTemplate, (template) => template.doctor)
  scheduleTemplates: DocScheduleTemplate[];

  @OneToMany(() => DocSchedule, (schedule) => schedule.doctor)
  schedules: DocSchedule[];

  constructor(partial: Partial<Doctor>) {
    Object.assign(this, partial);
  }
}
