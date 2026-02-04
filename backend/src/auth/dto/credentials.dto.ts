import { ApiProperty } from '@nestjs/swagger';

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
}
