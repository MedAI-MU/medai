import { ISeed } from './seed.interface';
import { StagingSeed } from './static-data/staging.seed';
import { ProdSeed } from './static-data/prod.seed';

export class SeedLoader {
  static load(): ISeed[] {
    if (process.env.SEED_PROFILE === 'prod') {
      return [new ProdSeed()];
    }
    return [new StagingSeed()];
  }
}
