import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { AppointmentStatusEnum } from '../enums/appointment-status.enum';
import type { AppointmentStatus } from '../types/appointment-status.type';
import { Appointment } from '../entities/appointment.entity';
import { DoctorResponseDto } from 'src/doctors/dtos/doctor-response.dto';
import { DocScheduleSlotDto } from 'src/schedules/dtos/doc-schedule-slot.dot';
import { DocScheduleSlot } from 'src/schedules/entities/doc-schedule-slot.entity';

export class AppointmentDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  patientUserId: number;

  @ApiProperty()
  doctorUserId: number;

  @ApiProperty()
  scheduleSlotId: number;

  @ApiProperty({ enum: AppointmentStatusEnum })
  status: AppointmentStatus;

  @ApiPropertyOptional()
  confirmedByUserId: number | null;

  @ApiPropertyOptional()
  rating: number | null;

  @ApiPropertyOptional()
  review: string | null;

  @ApiProperty()
  createdAt: Date;

  @ApiProperty()
  updatedAt: Date;

  @ApiProperty()
  doctor: DoctorResponseDto;

  scheduleSlot: DocScheduleSlotDto;

  constructor(appointment: Appointment) {
    this.patientUserId = appointment.patientUserId;
    this.doctorUserId = appointment.doctorUserId;
    this.confirmedByUserId = appointment.confirmedByUserId;
    this.status = appointment.status;
    this.scheduleSlotId = appointment.scheduleSlotId;
    this.rating = appointment.rating;
    this.review = appointment.review;
    this.createdAt = appointment.createdAt;
    this.updatedAt = appointment.updatedAt;
    this.doctor = new DoctorResponseDto(appointment.doctor);
    this.scheduleSlot = appointment.scheduleSlot;
  }
}
