import { ApiProperty } from '@nestjs/swagger';
import type { UserRoles, UserStatus } from 'src/users/types/role.types';

export class AuthenticatedUserDto {
  @ApiProperty({
    description: 'User id',
    example: 1,
  })
  id: number;

  @ApiProperty({
    description: 'User name',
    example: 'John Doe',
  })
  name: string;

  @ApiProperty({
    description: 'User email',
    example: 'john.doe@example.com',
  })
  email: string;

  @ApiProperty({
    description: 'User phone number',
    example: '01123456789',
  })
  phone: string;

  @ApiProperty({
    description: 'User role',
    example: 'doctor',
  })
  role: UserRoles;

  @ApiProperty({
    description: 'User status',
    example: 'approved',
  })
  status: UserStatus;
}
