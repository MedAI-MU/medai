import { IsOptional, IsString, IsNumber, IsNotEmpty, MaxLength } from 'class-validator';
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class CreateMedicalReportDto {
  @ApiProperty({ description: 'The text context or scan reference to generate a report from' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(2000)
  scanData: string;

  @ApiPropertyOptional({ description: 'The ID of the doctor submitting the scan' })
  @IsOptional()
  @IsNumber()
  doctorId?: number;
}
