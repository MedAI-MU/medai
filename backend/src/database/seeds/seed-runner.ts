import { DataSource } from 'typeorm';
import { ISeed } from './seed.interface';

export class SeedRunner {
  constructor(
    private dataSource: DataSource,
    private seeds: ISeed[],
  ) {}

  async runAll(): Promise<void> {
    for (const seed of this.seeds) {
      console.log(`Running seed: ${seed.name}`);
      await seed.run(this.dataSource);
      console.log(`Finished seed: ${seed.name}`);
    }
  }
}
