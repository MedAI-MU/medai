import { DataSource } from 'typeorm';

export interface ISeed {
  name: string;
  run(dataSource: DataSource): Promise<void>;
}
