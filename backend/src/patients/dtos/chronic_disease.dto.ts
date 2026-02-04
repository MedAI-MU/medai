import { IsDate, IsOptional } from 'class-validator';
import { PatientCommonInfoDto } from './common.dto';
import { Type } from 'class-transformer';

export class ChronicDiseaseDto extends PatientCommonInfoDto {
  @IsOptional()
  @IsDate()
  @Type(() => Date)
  diagnosisDate: Date;
}
