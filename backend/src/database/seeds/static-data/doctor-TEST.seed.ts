import { DataSource } from 'typeorm';
import { ISeed } from '../seed.interface';
import { Doctor } from '../../../doctors/entities/doctor.entity';

// for testing purposes only - should be removed in the next PR
export class DoctorsSeed implements ISeed {
  name = 'DoctorsSeed';
  async run(dataSource: DataSource): Promise<void> {
    const doctors: Doctor[] = [
      new Doctor({
        userId: 1,
      }),
    ];
    await dataSource.getRepository(Doctor).save(doctors);
  }
}
