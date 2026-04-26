import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AppointmentsService } from './appointments.service';
import { AppointmentsController } from './appointments.controller';
import { Appointment } from './entities/appointment.entity';
import { DocScheduleSlot } from '../schedules/entities/doc-schedule-slot.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Appointment, DocScheduleSlot])],
  controllers: [AppointmentsController],
  providers: [AppointmentsService],
  exports: [AppointmentsService],
})
export class AppointmentsModule {}
