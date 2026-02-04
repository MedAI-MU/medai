import { IsNumber, IsOptional, IsDate, IsIn } from 'class-validator';
import { Type } from 'class-transformer';
import {
  BloodTypeEnum,
  GenderEnum,
  MaritalStatusEnum,
} from '../enums/patients.enum';
import type { BloodType, Gender, MaritalStatus } from '../types/patient.types';
export class PatientDto {
  @IsDate()
  @Type(() => Date)
  birthDate: Date;

  @IsNumber()
  height: number;

  @IsNumber()
  weight: number;

  @IsIn(GenderEnum)
  gender: Gender;

  @IsOptional()
  @IsIn(BloodTypeEnum)
  bloodType?: BloodType;

  @IsOptional()
  @IsIn(MaritalStatusEnum)
  maritalStatus?: MaritalStatus;
}
