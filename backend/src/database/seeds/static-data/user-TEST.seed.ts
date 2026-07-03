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
        name: 'Ahmed Gouda',
        email: 'ahmed.gouda1@example.com',
        password: await argon2.hash('password123'),
        phone: '01123431234',
        role: 'manager',
        status: 'approved',
      }),
    ];
    await dataSource.getRepository(User).save(users);
  }
}
