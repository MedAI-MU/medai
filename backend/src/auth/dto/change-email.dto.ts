import { IsEmail, IsNotEmpty } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ChangeEmailDto {
  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @ApiProperty({
    description: 'New email address',
    example: 'new.email@example.com',
  })
  newEmail: string;
}
