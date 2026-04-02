import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { AppointmentsService } from './appointments.service';
import { AppointmentsController } from './appointments.controller';
import { Appointment } from './entities/appointment.entity';
import { SchedulesModule } from '../schedules/schedules.module';
import { PatientsModule } from '../patients/patients.module';
import { DoctorsModule } from '../doctors/doctors.module';
import { Patient } from '../patients/entities/patient.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { DocScheduleSlot } from '../schedules/entities/doc-schedule-slot.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([Appointment, Patient, Doctor, DocScheduleSlot]),
    SchedulesModule,
    PatientsModule,
    DoctorsModule,
  ],
  providers: [AppointmentsService],
  controllers: [AppointmentsController],
  exports: [AppointmentsService],
})
export class AppointmentsModule {}
