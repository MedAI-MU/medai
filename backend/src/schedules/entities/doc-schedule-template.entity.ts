import {
  Column,
  CreateDateColumn,
  Entity,
  Index,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';
import { Doctor } from '../../doctors/entities/doctor.entity';
import { User } from '../../users/entities/user.entity';
import { DocScheduleTemplateSlot } from './doc-schedule-template-slot.entity';

@Entity()
@Index('IDX_doc_schedule_template_doctor_created', ['doctor', 'createdAt'])
@Index('idx_template_name_trgm', { synchronize: false })
export class DocScheduleTemplate {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ type: 'varchar', length: 100 })
  name: string;

  @ManyToOne(() => Doctor, (doctor) => doctor.scheduleTemplates, {
    onDelete: 'RESTRICT',
  })
  @JoinColumn({ name: 'doctorId' })
  doctor: Doctor;

  @ManyToOne(() => User, (user) => user.doctorScheduleTemplates, {
    onDelete: 'RESTRICT',
  })
  @JoinColumn({ name: 'createdByUserId' })
  createdBy: User;

  @OneToMany(() => DocScheduleTemplateSlot, (slot) => slot.template, {
    cascade: true,
  })
  slots: DocScheduleTemplateSlot[];

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;

  constructor(partial: Partial<DocScheduleTemplate>) {
    Object.assign(this, partial);
  }
}
