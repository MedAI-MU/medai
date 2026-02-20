import { IsIn, IsNumber, IsOptional } from 'class-validator';
import { BloodTypeEnum, MaritalStatusEnum } from '../enums/patients.enum';
import type { BloodType, MaritalStatus } from '../types/patient.types';
import { ApiProperty } from '@nestjs/swagger';

export class UpdatePatientDto {
  @ApiProperty({
    description: 'The height of the patient',
    example: 170,
    required: false,
  })
  @IsOptional()
  @IsNumber()
  height?: number;

  @ApiProperty({
    description: 'The weight of the patient',
    example: 70,
    required: false,
  })
  @IsOptional()
  @IsNumber()
  weight?: number;

  @ApiProperty({
    description: 'The blood type of the patient',
    example: 'A+',
    enum: BloodTypeEnum,
    required: false,
  })
  @IsOptional()
  @IsIn(BloodTypeEnum)
  bloodType?: BloodType;

  @ApiProperty({
    description: 'The marital status of the patient',
    example: 'Married',
    enum: MaritalStatusEnum,
    required: false,
  })
  @IsOptional()
  @IsIn(MaritalStatusEnum)
  maritalStatus?: MaritalStatus;
}
