import { IsNotEmpty, IsString, Length } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ResetPasswordDto {
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    description: 'Password reset token',
    example: 'a1b2c3d4e5f6...',
  })
  token: string;

  @Length(6, 100)
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    description: 'New password',
    example: 'newStrongPassword123',
  })
  password: string;
}
