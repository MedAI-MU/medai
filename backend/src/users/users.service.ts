import {
  ConflictException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Doctor } from 'src/doctors/entities/doctor.entity';
import { Patient } from 'src/patients/entities/patient.entity';
import { DataSource, Repository } from 'typeorm';
import { RegisterDto } from './dtos/register.dto';
import * as argon2 from 'argon2';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
    private readonly dataSource: DataSource,
  ) {}

  async findById(id: number): Promise<User | null> {
    return this.usersRepository.findOneBy({ id });
  }

  async addDoctorRole(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.dataSource.transaction(async (manager) => {
      await manager.getRepository(Patient).delete(userId);
      await manager.getRepository(Doctor).save({ userId });
      await manager.getRepository(User).update(userId, { role: 'doctor' });
    });
  }

  async addSecretaryRole(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.dataSource.transaction(async (manager) => {
      await manager.getRepository(Patient).delete(userId);
      await manager.getRepository(User).update(userId, { role: 'secretary' });
    });
  }

  async registerUser(registerDto: RegisterDto) {
    // check if email or phone already exists
    if (
      await this.usersRepository.findOne({
        where: [{ email: registerDto.email }, { phone: registerDto.phone }],
      })
    ) {
      throw new ConflictException('Phone number or email already in use');
    }
    const user = new User({ ...registerDto, role: 'patient' });
    user.password = await argon2.hash(registerDto.password);
    return this.usersRepository.save(user);
  }
}
