import { DataSource } from 'typeorm';
import { User } from '../../../users/entities/user.entity';
import { ISeed } from '../seed.interface';

// for testing purposes only - should be removed in the next PR
export class UsersSeed implements ISeed {
  name = 'UsersSeed';
  private readonly users: User[] = [
    new User({
      name: 'User 1',
      email: 'user@example.com',
      password: 'password123',
      phone: '01122337799',
      role: 'secretary',
    }),
  ];
  async run(dataSource: DataSource): Promise<void> {
    await dataSource.getRepository(User).save(this.users);
  }
}
