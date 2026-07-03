import { IsInt, IsPositive } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class AddUserRoleDto {
  @ApiProperty({ description: 'User ID' })
  @IsInt()
  @IsPositive()
  userId: number;
}
