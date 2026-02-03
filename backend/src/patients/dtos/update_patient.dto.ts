import { IsIn, IsNumber, IsOptional } from 'class-validator';
import { BloodTypeEnum, MaritalStatusEnum } from '../enums/patients.enum';
import type { BloodType, MaritalStatus } from '../types/patient.types';

export class UpdatePatientDto {
  @IsOptional()
  @IsNumber()
  height: number;

  @IsOptional()
  @IsNumber()
  weight: number;

  @IsOptional()
  @IsIn(BloodTypeEnum)
  bloodType: BloodType;

  @IsOptional()
  @IsIn(MaritalStatusEnum)
  maritalStatus: MaritalStatus;
}
