import { Module } from '@nestjs/common';
import { SchedulesController } from './schedules.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import { DocSchedule } from './entities/doc-schedule.entity';
import { DocScheduleTemplate } from './entities/doc-schedule-template.entity';
import { DocScheduleTemplateSlot } from './entities/doc-schedule-template-slot.entity';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { DocScheduleSlotsService } from './doc-schedule-slots.service';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      DocScheduleTemplate,
      DocScheduleTemplateSlot,
      DocScheduleSlot,
      DocSchedule,
      Doctor,
      User,
    ]),
  ],
  controllers: [SchedulesController],
  providers: [DocScheduleTemplatesService, DocScheduleSlotsService],
  exports: [DocScheduleSlotsService, TypeOrmModule],
})
export class SchedulesModule {}
