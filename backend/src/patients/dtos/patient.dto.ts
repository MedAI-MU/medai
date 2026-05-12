import { ApiPropertyOptional } from '@nestjs/swagger';
import { BloodTypeEnum, MaritalStatusEnum } from '../enums/patients.enum';
import { GenderEnum } from '../../shared/enums/gender.enum';
import type { BloodType, MaritalStatus } from '../types/patient.types';
import type { Gender } from '../../shared/types/gender.type';

export class PatientDto {
  @ApiPropertyOptional()
  birthDate?: Date;

  @ApiPropertyOptional()
  height?: number;

  @ApiPropertyOptional()
  weight?: number;

  @ApiPropertyOptional({ enum: GenderEnum })
  gender?: Gender;

  @ApiPropertyOptional({ enum: BloodTypeEnum })
  bloodType?: BloodType;

  @ApiPropertyOptional({ enum: MaritalStatusEnum })
  maritalStatus?: MaritalStatus;
}
