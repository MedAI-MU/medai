import { ISeed } from './seed.interface';
import { UsersSeed } from './static-data/user-TEST.seed';

export class SeedLoader {
  static load(): ISeed[] {
    return [new UsersSeed()];
  }
}
