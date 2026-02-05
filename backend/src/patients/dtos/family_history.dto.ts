import { IsIn, IsOptional, IsString } from 'class-validator';
import { FamilyRelationEnum } from '../enums/patients.enum';
import type { FamilyRelation } from '../types/patient.types';
import { ApiProperty } from '@nestjs/swagger';

export class FamilyHistoryDto {
  @ApiProperty({
    enum: FamilyRelationEnum,
    description: 'The relation of the family member to the patient',
  })
  @IsIn(FamilyRelationEnum)
  relation: FamilyRelation;

  @ApiProperty({
    description: 'The condition of the family member',
    example: 'Hypertension',
  })
  @IsString()
  condition: string;

  @ApiProperty({
    description: 'Notes about the family member',
    example: 'notes',
  })
  @IsOptional()
  @IsString()
  notes: string;
}
