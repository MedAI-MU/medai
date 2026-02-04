import { IsNumber, IsOptional, IsDate, IsIn } from 'class-validator';
import { Type } from 'class-transformer';
import {
  BloodTypeEnum,
  GenderEnum,
  MaritalStatusEnum,
} from '../enums/patients.enum';
import type { BloodType, Gender, MaritalStatus } from '../types/patient.types';
import { ApiProperty } from '@nestjs/swagger';

export class PatientDto {
  @ApiProperty({
    description: 'The date of birth of the patient',
    example: '2022-01-01',
  })
  @IsDate()
  @Type(() => Date)
  birthDate: Date;

  @ApiProperty({
    description: 'The height of the patient',
    example: 170,
  })
  @IsNumber()
  height: number;

  @ApiProperty({
    description: 'The weight of the patient',
    example: 70,
  })
  @IsNumber()
  weight: number;

  @ApiProperty({
    description: 'The gender of the patient',
    example: 'male',
    enum: GenderEnum,
  })
  @IsIn(GenderEnum)
  gender: Gender;

  @ApiProperty({
    description: 'The blood type of the patient',
    example: 'A+',
    enum: BloodTypeEnum,
  })
  @IsOptional()
  @IsIn(BloodTypeEnum)
  bloodType?: BloodType;

  @ApiProperty({
    description: 'The marital status of the patient',
    example: 'single',
    enum: MaritalStatusEnum,
  })
  @IsOptional()
  @IsIn(MaritalStatusEnum)
  maritalStatus?: MaritalStatus;
}
