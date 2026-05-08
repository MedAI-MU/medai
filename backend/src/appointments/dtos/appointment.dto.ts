import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { AppointmentStatusEnum } from '../enums/appointment-status.enum';
import type { AppointmentStatus } from '../types/appointment-status.type';
import { Appointment } from '../entities/appointment.entity';

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

  constructor(partial: Partial<Appointment>) {
    Object.assign(this, partial);
  }
}
