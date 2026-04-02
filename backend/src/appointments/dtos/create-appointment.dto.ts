import { IsInt, IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class CreateAppointmentDto {
  @ApiProperty({ description: 'ID of the doctor' })
  @IsInt()
  @IsNotEmpty()
  doctorId: number;

  @ApiProperty({ description: 'ID of the schedule slot' })
  @IsInt()
  @IsNotEmpty()
  slotId: number;

  @ApiPropertyOptional({ description: 'Description of the problem' })
  @IsString()
  @IsOptional()
  problemDescription?: string;
}
