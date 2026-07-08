import {
  IsDateString,
  IsIn,
  IsOptional,
  IsString,
  Length,
  Matches,
} from 'class-validator';
import { ApiPropertyOptional } from '@nestjs/swagger';

export class UpdateProfileDto {
  @IsOptional()
  @Length(5, 100)
  @IsString()
  @ApiPropertyOptional({
    description: 'Full name of the user',
    example: 'Mohamed Ahmed',
  })
  name?: string;

  @IsOptional()
  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
  })
  @IsString()
  @ApiPropertyOptional({
    description: 'Phone number of the user',
    example: '01123456789',
  })
  phone?: string;

  @IsOptional()
  @IsDateString()
  @ApiPropertyOptional({
    description: 'Birth date of the user (ISO 8601)',
    example: '1990-01-15',
  })
  birthDate?: string;

  @IsOptional()
  @IsIn(['male', 'female'] as const)
  @ApiPropertyOptional({
    description: 'Gender of the user',
    example: 'male',
  })
  gender?: string;
}
