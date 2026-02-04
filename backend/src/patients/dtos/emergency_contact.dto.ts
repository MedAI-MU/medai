import { IsEmail, IsIn, IsOptional, IsString, Matches } from 'class-validator';
import { FamilyRelationEnum } from '../enums/patients.enum';
import type { FamilyRelation } from '../types/patient.types';
import { ApiProperty } from '@nestjs/swagger';

export class EmergencyContactDto {
  @ApiProperty({
    description: 'The name of an emergency contact',
    example: 'Ahmed Mohamed',
  })
  @IsString()
  name: string;

  @ApiProperty({
    description: 'The relation of an emergency contact',
    example: 'Father',
    enum: FamilyRelationEnum,
  })
  @IsIn(FamilyRelationEnum)
  relation: FamilyRelation;

  @ApiProperty({
    description: 'The phone number of an emergency contact',
    example: '01012345678',
  })
  @IsString()
  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
    each: true,
  })
  phoneNumber: string;

  @ApiProperty({
    description: 'The email of an emergency contact',
    example: 'ahmed@example.com',
  })
  @IsEmail()
  email: string;

  @ApiProperty({
    description: 'The address of an emergency contact',
    example: '123 Main St',
  })
  @IsString()
  address: string;

  @ApiProperty({
    description: 'Notes about an emergency contact',
    example: 'Call at 10:00 AM',
  })
  @IsOptional()
  @IsString()
  notes: string;
}
