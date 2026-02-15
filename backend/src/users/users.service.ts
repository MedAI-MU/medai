import { ConflictException, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Repository } from 'typeorm';
import { RegisterDto } from './dtos/register.dto';
import * as argon2 from 'argon2';

import { Doctor } from '../doctors/entities/doctor.entity';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
    @InjectRepository(Doctor) private readonly doctorsRepository: Repository<Doctor>,
  ) { }

  async registerUser(registerDto: RegisterDto) {
    // check if email or phone already exists
    if (
      await this.usersRepository.findOne({
        where: [{ email: registerDto.email }, { phone: registerDto.phone }],
      })
    ) {
      throw new ConflictException('Phone number or email already in use');
    }
    const user = new User(registerDto);
    user.password = await argon2.hash(registerDto.password);
    const savedUser = await this.usersRepository.save(user);

    if (savedUser.role === 'doctor') {
      const doctor = new Doctor({
        userId: savedUser.id,
        specialty: 'General Practitioner', // Default specialty
      });
      await this.doctorsRepository.save(doctor);
    }

    return savedUser;
  }
}
