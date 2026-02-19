import { ApiProperty } from '@nestjs/swagger';
import { IsOptional, IsString } from 'class-validator';

export class PatientCommonInfoDto {
  @ApiProperty({
    description:
      'The name of a patient field (e.g. allergy, chronic disease, etc.)',
    example: 'Heart surgery',
  })
  @IsString()
  name: string;

  @ApiProperty({
    description:
      'The description of a patient field (e.g. allergy, chronic disease, etc.)',
    example: 'Patient had heart surgery in 2019',
    required: false,
  })
  @IsOptional()
  @IsString()
  description: string;
}
