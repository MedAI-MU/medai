import { IsEmail, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class SendVerificationDto {
  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @ApiProperty({
    description: 'Email address to send verification link to',
    example: 'mohamed.ahmed@gmail.com',
  })
  email: string;
}
