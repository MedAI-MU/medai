import { ISeed } from './seed.interface';
import { DoctorsSeed } from './static-data/doctor-TEST.seed';
import { UsersSeed } from './static-data/user-TEST.seed';

export class SeedLoader {
  static load(): ISeed[] {
    return [new UsersSeed(), new DoctorsSeed()];
  }
}
