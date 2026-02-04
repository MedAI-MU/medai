import { IsDate } from 'class-validator';
import { PatientCommonInfoDto } from './common.dto';
import { Type } from 'class-transformer';

export class SurgeryDto extends PatientCommonInfoDto {
  @IsDate()
  @Type(() => Date)
  date: Date;
}
