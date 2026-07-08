import { IsNotEmpty, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class VerifyEmailDto {
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    description: 'Email verification token',
    example: 'a1b2c3d4e5f6...',
  })
  token: string;
}
