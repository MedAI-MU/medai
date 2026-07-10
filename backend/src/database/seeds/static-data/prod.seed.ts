import { DataSource } from 'typeorm';
import { User } from '../../../users/entities/user.entity';
import { ISeed } from '../seed.interface';
import * as argon2 from 'argon2';

export class ProdSeed implements ISeed {
  name = 'ProdSeed';

  async run(dataSource: DataSource): Promise<void> {
    const email = process.env.PROD_MANAGER_EMAIL;
    const password = process.env.PROD_MANAGER_PASSWORD;
    const phone = process.env.PROD_MANAGER_PHONE;

    if (!email || !password || !phone) {
      throw new Error(
        'PROD_MANAGER_EMAIL, PROD_MANAGER_PASSWORD, and PROD_MANAGER_PHONE environment variables must be set',
      );
    }

    const userRepo = dataSource.getRepository(User);

    const existing = await userRepo.findOne({ where: { email } });
    if (existing) {
      console.log(`Manager with email ${email} already exists, skipping`);
      return;
    }

    const passwordHash = await argon2.hash(password);

    const managerUser = new User({
      name: 'Manager',
      email,
      password: passwordHash,
      phone,
      role: 'manager',
      status: 'approved',
      emailVerified: true,
    });

    await userRepo.save(managerUser);
  }
}
