import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Doctor } from '../../doctors/entities/doctor.entity';
import type { SlotStatus } from '../types/slot-status.types';
import { User } from '../../users/entities/user.entity';

@Entity()
export class DocScheduleSlot {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Doctor, (doctor) => doctor.scheduleSlots, {
    onDelete: 'RESTRICT',
  })
  @JoinColumn({ name: 'doctorId' })
  doctor: Doctor;

  @Column('date')
  dayDate: string;

  @Column('time')
  startTime: string;

  @Column('time')
  endTime: string;

  @Column({
    type: 'varchar',
    length: 20,
    default: 'available',
  })
  status: SlotStatus;

  @ManyToOne(() => User, (user) => user.doctor, {
    onDelete: 'RESTRICT',
  })
  @JoinColumn({ name: 'secretaryId' })
  secretary: User;

  constructor(partial: Partial<DocScheduleSlot>) {
    Object.assign(this, partial);
  }
}
