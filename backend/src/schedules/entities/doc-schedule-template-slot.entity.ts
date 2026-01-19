import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { DocScheduleTemplate } from './doc-schedule-template.entity';

@Entity()
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
