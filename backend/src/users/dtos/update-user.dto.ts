import {
  IsDateString,
  IsIn,
  IsOptional,
  IsString,
  Length,
  Matches,
} from 'class-validator';
import { ApiPropertyOptional } from '@nestjs/swagger';
import { GenderEnum } from '../../shared/enums/gender.enum';

export class UpdateUserDto {
  @IsOptional()
  @Length(5, 100)
  @IsString()
  @ApiPropertyOptional({ example: 'Mohamed Ahmed', description: 'Full name' })
  name?: string;

  @IsOptional()
  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
  })
  @IsString()
  @ApiPropertyOptional({
    example: '01123456789',
    description: 'Phone number',
  })
  phone?: string;

  @IsOptional()
  @IsDateString()
  @ApiPropertyOptional({ example: '1990-01-15', description: 'Birth date' })
  birthDate?: string;

  @IsOptional()
  @IsIn(GenderEnum)
  @ApiPropertyOptional({
    example: 'male',
    enum: GenderEnum,
    description: 'Gender',
  })
  gender?: 'male' | 'female';

  @IsOptional()
  @IsString()
  @ApiPropertyOptional({
    example: 'Experienced cardiologist with 10+ years...',
    description: 'Short biography',
  })
  bio?: string;
}
