import {
  IsEmail,
  IsEnum,
  IsOptional,
  IsString,
  Matches,
} from 'class-validator';
import { FamilyRelation } from '../enums/patients.enum';

export class EmergencyContactDto {
  @IsString()
  name: string;

  @IsEnum(FamilyRelation)
  relation: FamilyRelation;

  @IsString()
  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
    each: true,
  })
  phoneNumber: string[];

  @IsEmail()
  email: string;

  @IsString()
  address: string;

  @IsOptional()
  @IsString()
  notes: string;
}
