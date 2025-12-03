import { ConflictException, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Repository } from 'typeorm';
import { RegisterDto } from './dtos/register.dto';
import * as argon2 from 'argon2';

@Injectable()
export class UsersService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
  ) {}

  async registerUser(registerDto: RegisterDto) {
    const userExists = await this.usersRepository.findOne({
      where: [{ email: registerDto.email }, { name: registerDto.name }],
    });
    if (userExists) {
      throw new ConflictException('User already exists');
    }
    const user = new User(registerDto);
    user.password = await argon2.hash(registerDto.password);
    return this.usersRepository.save(user);
  }
}
