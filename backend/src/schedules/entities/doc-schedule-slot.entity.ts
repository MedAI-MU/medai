import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import type { SlotStatus } from '../types/slot-status.types';
import { DocSchedule } from './doc-schedule.entity';

@Entity()
export class DocScheduleSlot {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => DocSchedule, (schedule) => schedule.slots, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'docScheduleId' })
  schedule: DocSchedule;

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

  constructor(partial: Partial<DocScheduleSlot>) {
    Object.assign(this, partial);
  }
}
