import { IsEmail, IsNotEmpty, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class LoginDto {
  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @ApiProperty({
    description: 'Email address of the user',
    example: 'mohamed.ahmed@gmail.com',
  })
  email: string;
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    description: 'Password of the user',
    example: 'strongPassword123',
  })
  password: string;
}
