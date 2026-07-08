import { IsNotEmpty, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ConfirmEmailChangeDto {
  @IsNotEmpty()
  @IsString()
  @ApiProperty({
    example: 'abc123...',
    description: 'Verification token sent to the current email',
  })
  token: string;
}
