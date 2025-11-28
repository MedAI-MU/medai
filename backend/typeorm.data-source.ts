import { DataSource, DataSourceOptions } from 'typeorm';
import { typeOrmConfig } from './typeorm.config';

export const AppDataSource = new DataSource({
  ...typeOrmConfig,
  // Override host for typeorm CLI
  host: 'localhost',
} as DataSourceOptions);
