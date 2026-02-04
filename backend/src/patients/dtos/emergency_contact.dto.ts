import { IsEmail, IsIn, IsOptional, IsString, Matches } from 'class-validator';
import { FamilyRelationEnum } from '../enums/patients.enum';
import type { FamilyRelation } from '../types/patient.types';

export class EmergencyContactDto {
  @IsString()
  name: string;

  @IsIn(FamilyRelationEnum)
  relation: FamilyRelation;

  @IsString()
  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
    each: true,
  })
  phoneNumber: string;

  @IsEmail()
  email: string;

  @IsString()
  address: string;

  @IsOptional()
  @IsString()
  notes: string;
}
