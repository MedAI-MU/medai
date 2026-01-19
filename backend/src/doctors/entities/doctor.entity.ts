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
import { DocScheduleSlot } from '../../schedules/entities/doc-schedule-slot.entity';

@Entity()
export class Doctor {
  @PrimaryColumn()
  userId: number;

  @OneToOne(() => User, (user) => user.doctor)
  @JoinColumn({ name: 'userId' })
  user: User;

  @Column()
  specialty: string;

  @OneToMany(() => DocScheduleTemplate, (template) => template.doctor)
  scheduleTemplates: DocScheduleTemplate[];

  @OneToMany(() => DocScheduleSlot, (slot) => slot.doctor)
  scheduleSlots: DocScheduleSlot[];

  constructor(partial: Partial<Doctor>) {
    Object.assign(this, partial);
  }
}
