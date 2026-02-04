import { IsIn, IsOptional, IsString } from 'class-validator';
import { FamilyRelationEnum } from '../enums/patients.enum';
import type { FamilyRelation } from '../types/patient.types';

export class FamilyHistoryDto {
  @IsIn(FamilyRelationEnum)
  relation: FamilyRelation;

  @IsString()
  condition: string;

  @IsOptional()
  @IsString()
  notes: string;
}
