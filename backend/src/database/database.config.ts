import { registerAs } from '@nestjs/config';

export default registerAs('database', () => ({
  username: process.env.DB_USERNAME || 'medai',
  password: process.env.DB_PASSWORD || 'password',
  name: process.env.DB_NAME || 'medai_db',
  host: process.env.DB_HOST || 'medai-db',
  port: parseInt(process.env.DB_PORT || '5432'),
}));
