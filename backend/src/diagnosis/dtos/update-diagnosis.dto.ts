import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsNotEmpty, IsOptional } from 'class-validator';

export class UpdateDiagnosisDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  @IsOptional()
  symptoms: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  summary: string;
}
