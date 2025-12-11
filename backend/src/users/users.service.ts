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
    return this.usersRepository.save(user);
  }
}
