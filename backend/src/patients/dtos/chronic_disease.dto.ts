import { IsDate, IsOptional } from 'class-validator';
import { PatientCommonInfoDto } from './common.dto';
import { Type } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

export class ChronicDiseaseDto extends PatientCommonInfoDto {
  @ApiProperty({
    description: 'The diagnosis date of a chronic disease',
    example: '2022-01-01',
    required: false,
  })
  @IsOptional()
  @IsDate()
  @Type(() => Date)
  diagnosisDate: Date;
}
