import { IsEmail, IsNotEmpty, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ChangeEmailDto {
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    description: 'Current password for confirmation',
    example: 'strongPassword123',
  })
  password: string;

  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @ApiProperty({
    description: 'New email address',
    example: 'new.email@example.com',
  })
  newEmail: string;
}
