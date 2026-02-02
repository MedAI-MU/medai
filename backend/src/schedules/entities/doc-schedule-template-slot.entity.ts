import {
  Column,
  Entity,
  Index,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { DocScheduleTemplate } from './doc-schedule-template.entity';

@Entity()
@Index('IDX_doc_schedule_template_slot_template', ['template'])
export class DocScheduleTemplateSlot {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => DocScheduleTemplate, (template) => template.slots, {
    onDelete: 'CASCADE',
  })
  template: DocScheduleTemplate;

  @Column('int')
  weekDay: number;

  @Column('time')
  startTime: string;

  @Column('time')
  endTime: string;

  constructor(partial: Partial<DocScheduleTemplateSlot>) {
    Object.assign(this, partial);
  }
}
