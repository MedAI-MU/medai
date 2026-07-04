import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsNumber, IsString } from 'class-validator';

export class CreateDiagnosisDto {
  @ApiProperty()
  @IsNumber()
  @IsNotEmpty()
  patientUserId: number;

  @ApiProperty()
  @IsNumber()
  @IsNotEmpty()
  appointmentId: number;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  symptoms: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  summary: string;
}
