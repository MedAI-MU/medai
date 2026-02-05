import {
  Column,
  Entity,
  Index,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { User } from '../../users/entities/user.entity';
import { DocScheduleSlot } from './doc-schedule-slot.entity';

@Entity()
@Index('IDX_doc_schedule_doctor_day', ['doctor', 'dayDate'])
export class DocSchedule {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Doctor, (doctor) => doctor.schedules, {
    onDelete: 'RESTRICT',
  })
  @JoinColumn({ name: 'doctorId' })
  doctor: Doctor;

  @Index('IDX_doc_schedule_day_date')
  @Column('date')
  dayDate: string;

  @ManyToOne(() => User, (user) => user.doctorSchedules, {
    onDelete: 'RESTRICT',
  })
  @JoinColumn({ name: 'createdByUserId' })
  createdBy: User;

  @OneToMany(() => DocScheduleSlot, (slot) => slot.schedule, {
    cascade: true,
  })
  slots: DocScheduleSlot[];

  constructor(partial: Partial<DocSchedule>) {
    Object.assign(this, partial);
  }
}
