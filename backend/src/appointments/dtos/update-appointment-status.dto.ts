import { ApiProperty } from '@nestjs/swagger';
import { IsEnum } from 'class-validator';
import { AppointmentStatusEnum } from '../enums/appointment-status.enum';

export class UpdateAppointmentStatusDto {
  @ApiProperty({
    description: 'New appointment status',
    enum: AppointmentStatusEnum,
    example: AppointmentStatusEnum.CONFIRMED,
  })
  @IsEnum(AppointmentStatusEnum)
  status: AppointmentStatusEnum;
}
