import { registerAs } from '@nestjs/config';

export default registerAs('jwt', () => ({
  secret: process.env.JWT_SECRET || 'default_jwt_secret_key',
  tokenExpiresInMs: parseInt(process.env.JWT_EXPIRATION || '900000', 10), // 15 minutes in ms
  refreshTokenExpiresInMs: parseInt(
    process.env.REFRESH_TOKEN_EXPIRATION || '604800000',
    10,
  ), // 7 days in ms
}));
