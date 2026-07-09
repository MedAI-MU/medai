import { DataSource } from 'typeorm';
import { User } from '../../../users/entities/user.entity';
import { Patient } from '../../../patients/entities/patient.entity';
import { Doctor } from '../../../doctors/entities/doctor.entity';
import { ISeed } from '../seed.interface';
import * as argon2 from 'argon2';

const SEED_PASSWORD = 'Password123123';

export class StagingSeed implements ISeed {
  name = 'StagingSeed';

  async run(dataSource: DataSource): Promise<void> {
    await this.wipeAllTables(dataSource);

    const passwordHash = await argon2.hash(SEED_PASSWORD);

    const patientUser = new User({
      name: 'Patient',
      email: 'patient@example.com',
      password: passwordHash,
      phone: '01000000002',
      role: 'patient',
      status: 'approved',
      emailVerified: true,
    });

    const secretaryUser = new User({
      name: 'Secretary',
      email: 'secretary@example.com',
      password: passwordHash,
      phone: '01000000003',
      role: 'secretary',
      status: 'approved',
      emailVerified: true,
    });

    const managerUser = new User({
      name: 'Manager',
      email: 'manager@example.com',
      password: passwordHash,
      phone: '01000000004',
      role: 'manager',
      status: 'approved',
      emailVerified: true,
    });

    const doctorUser = new User({
      name: 'Doctor',
      email: 'doctor@example.com',
      password: passwordHash,
      phone: '01000000005',
      role: 'doctor',
      status: 'approved',
      emailVerified: true,
    });

    const savedUsers = await dataSource
      .getRepository(User)
      .save([patientUser, secretaryUser, managerUser, doctorUser]);

    const [savedPatientUser, , , savedDoctorUser] = savedUsers;

    const patientRepo = dataSource.getRepository(Patient);
    await patientRepo.save(patientRepo.create({ userId: savedPatientUser.id }));

    await dataSource
      .getRepository(Doctor)
      .save(new Doctor({ userId: savedDoctorUser.id }));
  }

  private async wipeAllTables(dataSource: DataSource): Promise<void> {
    const tableNames = dataSource.entityMetadatas
      .map((metadata) => metadata.tableName)
      .filter((tableName) => tableName && tableName.length > 0);

    if (tableNames.length === 0) return;

    const quotedTables = tableNames.map((t) => `"${t}"`).join(', ');
    await dataSource.query(
      `TRUNCATE TABLE ${quotedTables} RESTART IDENTITY CASCADE`,
    );
  }
}