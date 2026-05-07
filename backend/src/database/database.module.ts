import { Module } from '@nestjs/common';
import { type ConfigType } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import databaseConfig from './database.config';

@Module({
  imports: [
    TypeOrmModule.forRootAsync({
      useFactory: (dbConfig: ConfigType<typeof databaseConfig>) => ({
        type: 'postgres',
        host: dbConfig.host,
        port: dbConfig.port,
        username: dbConfig.username,
        password: dbConfig.password,
        database: dbConfig.name,
        migrations: ['./src/database/migrations/**/*{.js,.ts}'],
        autoLoadEntities: true,
        synchronize: false,
        logging: true,
        ssl:
          process.env.DB_SSL === 'true' ? { rejectUnauthorized: false } : false,
        extra:
          process.env.DB_SSL === 'true'
            ? {
                ssl: { rejectUnauthorized: false },
              }
            : {},
      }),
      inject: [databaseConfig.KEY],
    }),
  ],
})
export class DatabaseModule {}
