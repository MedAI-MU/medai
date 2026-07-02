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
import type { UserRoles, UserStatus } from './types/role.types';
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

  async findByRole(role: UserRoles): Promise<User[]> {
    return this.usersRepository.find({ where: { role } });
  }

  async findByRoleAndStatus(
    role: UserRoles,
    status: UserStatus,
  ): Promise<User[]> {
    return this.usersRepository.find({ where: { role, status } });
  }

  async addDoctorRole(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.dataSource.transaction(async (manager) => {
      await manager.getRepository(Patient).delete(userId);
      await manager.getRepository(Doctor).save({ userId });
      await manager.getRepository(User).update(userId, {
        role: 'doctor',
        status: 'approved',
      });
    });
  }

  async addSecretaryRole(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.dataSource.transaction(async (manager) => {
      await manager.getRepository(Patient).delete(userId);
      await manager.getRepository(User).update(userId, {
        role: 'secretary',
        status: 'approved',
      });
    });
  }

  async removeDoctorRole(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.dataSource.transaction(async (manager) => {
      await manager.getRepository(Doctor).delete(userId);
      await manager.getRepository(Patient).save({ userId });
      await manager.getRepository(User).update(userId, {
        role: 'patient',
        status: 'approved',
      });
    });
  }

  async removeSecretaryRole(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.dataSource.transaction(async (manager) => {
      await manager.getRepository(Patient).save({ userId });
      await manager.getRepository(User).update(userId, {
        role: 'patient',
        status: 'approved',
      });
    });
  }

  async delete(id: number): Promise<void> {
    const user = await this.findById(id);
    if (!user) {
      throw new NotFoundException('User not found');
    }
    await this.usersRepository.remove(user);
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
    const status = registerDto.role === 'patient' ? 'approved' : 'pending';
    const user = new User({
      ...registerDto,
      role: registerDto.role,
      status,
    });
    user.password = await argon2.hash(registerDto.password);
    return this.usersRepository.save(user);
  }
}
