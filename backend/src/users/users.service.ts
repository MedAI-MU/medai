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
import { FileStorageService } from '../shared/services/file-storage.service';
import type { UserRoles, UserStatus } from './types/role.types';
import * as argon2 from 'argon2';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
    private readonly dataSource: DataSource,
    private readonly fileStorage: FileStorageService,
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

  async removeUser(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) {
      throw new NotFoundException('User not found');
    }

    await this.usersRepository.remove(user);
  }

  async updateAvatar(
    userId: number,
    file: Express.Multer.File,
  ): Promise<string> {
    const user = await this.findById(userId);
    if (!user) throw new NotFoundException('User not found');

    const oldAvatar = user.avatar;

    const url = await this.fileStorage.saveFile(
      file.buffer,
      file.originalname,
      'avatars',
      file.mimetype,
    );
    user.avatar = url;
    await this.usersRepository.save(user);
    if (oldAvatar) {
      await this.fileStorage.deleteFile(oldAvatar);
    }
    return url;
  }

  async deleteAvatar(userId: number): Promise<void> {
    const user = await this.findById(userId);
    if (!user) throw new NotFoundException('User not found');
    if (!user.avatar) return;

    const oldAvatar = user.avatar;
    user.avatar = null;
    await this.usersRepository.save(user);
    await this.fileStorage.deleteFile(oldAvatar);
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
