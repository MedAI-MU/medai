import { DataSource } from 'typeorm';
import { User } from '../../../users/entities/user.entity';
import { ISeed } from '../seed.interface';
import * as argon2 from 'argon2';

// for testing purposes only - should be removed in the next PR
export class UsersSeed implements ISeed {
  name = 'UsersSeed';
  async run(dataSource: DataSource): Promise<void> {
    const users: User[] = [
      new User({
        name: 'Dr. Sarah Johnson',
        email: 'sarah.johnson@example.com',
        password: await argon2.hash('password123'),
        phone: '01133445566',
        role: 'doctor',
      }),
      new User({
        name: 'Mostafa Atef',
        email: 'mostafa.atef@example.com',
        password: await argon2.hash('password123'),
        phone: '01122337799',
        role: 'secretary',
      }),
    ];
    await dataSource.getRepository(User).save(users);
  }
}
