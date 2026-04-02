import { ApiProperty } from '@nestjs/swagger';
import type { UserRoles } from 'src/users/types/role.types';

export class CredentialsDto {
  @ApiProperty({
    description: 'Access token for authentication',
    example: 'dGhpc2lzYW5h',
  })
  accessToken: string;

  @ApiProperty({
    description: 'Expiration date and time of the access token',
    example: '2024-12-31T23:59:59.999Z',
  })
  accessTokenExpiresAt: Date;

  @ApiProperty({
    description: 'Refresh token for obtaining new access tokens',
    example: 'dGhpc2lzYXJlZnJlc2h0b2tlbg==',
  })
  refreshToken: string;

  @ApiProperty({
    description: 'Expiration date and time of the refresh token',
    example: '2025-12-31T23:59:59.999Z',
  })
  refreshTokenExpiresAt: Date;

  @ApiProperty({
    description: 'Authenticated user id',
    example: 1,
  })
  id: number;

  @ApiProperty({
    description: 'Authenticated user name',
    example: 'John Doe',
  })
  name: string;

  @ApiProperty({
    description: 'Authenticated user email',
    example: 'john.doe@example.com',
  })
  email: string;

  @ApiProperty({
    description: 'Authenticated user phone number',
    example: '01123456789',
  })
  phone: string;

  @ApiProperty({
    description: 'Authenticated user role',
    example: 'doctor',
  })
  role: UserRoles;
}
