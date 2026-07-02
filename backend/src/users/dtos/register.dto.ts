import {
  IsEmail,
  IsNotEmpty,
  IsString,
  Length,
  Matches,
} from 'class-validator';

import { ApiProperty } from '@nestjs/swagger';

export class RegisterDto {
  @Length(5, 100)
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    example: 'Mohamed Ahmed',
    description: 'Full name of the user',
  })
  name: string;

  @Length(5, 256)
  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    example: 'mohamed.ahmed@gmail.com',
    description: 'Email address of the user',
  })
  email: string;

  @Length(6, 100)
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    example: 'strongPassword123',
    description: 'Password of the user',
  })
  password: string;

  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
  })
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    example: '01123456789',
    description: 'Phone number of the user',
  })
  phone: string;
}
