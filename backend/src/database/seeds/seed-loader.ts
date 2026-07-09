import { ISeed } from './seed.interface';
import { StagingSeed } from './static-data/staging.seed';

export class SeedLoader {
  static load(): ISeed[] {
    return [new StagingSeed()];
  }
}
