import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsNotEmpty } from 'class-validator';

export class UpdateDiagnosisDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  symptoms: string;

  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  summary: string;
}
