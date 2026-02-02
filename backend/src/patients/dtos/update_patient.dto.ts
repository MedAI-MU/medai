import { IsEnum, IsNumber, IsOptional } from 'class-validator';
import { BloodType, MaritalStatus } from '../enums/patients.enum';

export class UpdatePatientDto {
  @IsOptional()
  @IsNumber()
  height: number;

  @IsOptional()
  @IsNumber()
  weight: number;

  @IsOptional()
  @IsEnum(BloodType)
  bloodType: BloodType;

  @IsOptional()
  @IsEnum(MaritalStatus)
  maritalStatus: MaritalStatus;
}
