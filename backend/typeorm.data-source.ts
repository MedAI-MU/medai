import 'dotenv/config';
import { ConfigService } from '@nestjs/config';
import { DataSource } from 'typeorm';

const configService = new ConfigService();

const sslEnabled = process.env.DB_SSL === 'true';

export default new DataSource({
  type: 'postgres',
  host: configService.getOrThrow<string>('DB_HOST'),
  port: parseInt(configService.getOrThrow<string>('DB_PORT')),
  username: configService.getOrThrow<string>('DB_USERNAME'),
  password: configService.getOrThrow<string>('DB_PASSWORD'),
  database: configService.getOrThrow<string>('DB_NAME'),
  migrations: [`${__dirname}/src/database/migrations/**/*{.js,.ts}`],
  entities: [`${__dirname}/src/**/entities/*{.js,.ts}`],
  ssl: sslEnabled ? { rejectUnauthorized: false } : false,
  extra: {
    ssl: sslEnabled ? { rejectUnauthorized: false } : false,
  },
});
