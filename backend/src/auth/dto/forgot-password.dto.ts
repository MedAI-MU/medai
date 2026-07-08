import { IsEmail, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ForgotPasswordDto {
  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @ApiProperty({
    description: 'Email address to send password reset link to',
    example: 'mohamed.ahmed@gmail.com',
  })
  email: string;
}
