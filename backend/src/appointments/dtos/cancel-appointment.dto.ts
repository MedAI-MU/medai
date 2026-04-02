import { IsNotEmpty, IsOptional, IsString } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class CancelAppointmentDto {
  @ApiPropertyOptional({ description: 'ID of the cancel reason' })
  @IsString()
  @IsOptional()
  reasonId?: string;

  @ApiPropertyOptional({ description: 'Other cancel reason' })
  @IsString()
  @IsOptional()
  otherReason?: string;
}
