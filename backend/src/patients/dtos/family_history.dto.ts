import { IsEnum, IsOptional, IsString } from 'class-validator';
import { FamilyRelation } from '../enums/patients.enum';

export class FamilyHistoryDto {
  @IsEnum(FamilyRelation)
  relation: FamilyRelation;

  @IsString()
  condition: string;

  @IsOptional()
  @IsString()
  notes: string;
}
