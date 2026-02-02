import { BloodType, Gender, MaritalStatus } from '../enums/patients.enum';
import { IsNumber, IsEnum, IsOptional, IsDate } from 'class-validator';
import { Type } from 'class-transformer';
export class PatientDto {
  @IsDate()
  @Type(() => Date)
  birthDate: Date;

  @IsNumber()
  height: number;

  @IsNumber()
  weight: number;

  @IsEnum(Gender)
  gender: Gender;

  @IsOptional()
  @IsEnum(BloodType)
  bloodType?: BloodType;

  @IsOptional()
  @IsEnum(MaritalStatus)
  maritalStatus?: MaritalStatus;
}
