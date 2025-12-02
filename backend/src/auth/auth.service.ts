import { Inject, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from 'src/users/entities/users.entity';
import { Repository } from 'typeorm';
import * as argon2 from 'argon2';
import { JwtService } from '@nestjs/jwt';
import jwtConfig from './jwt.config';
import { type ConfigType } from '@nestjs/config';
import { AccessTokenDto } from './dto/access-token.dto';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
    private readonly jwtService: JwtService,
    @Inject(jwtConfig.KEY)
    private readonly jwtOptions: ConfigType<typeof jwtConfig>,
  ) {}

  async validateUser(email: string, password: string): Promise<User | null> {
    const user = await this.usersRepository.findOne({ where: { email } });
    if (user && (await argon2.verify(user.password, password))) {
      const { password, ...result } = user;
      return result as User;
    }
    return null;
  }

  async login(user: User): Promise<AccessTokenDto> {
    const payload = { sub: user.id, email: user.email };
    const accessToken = await this.jwtService.signAsync(payload);
    const accessTokenExpiresAt = new Date();
    accessTokenExpiresAt.setMilliseconds(
      accessTokenExpiresAt.getMilliseconds() + this.jwtOptions.tokenExpiresInMs,
    );
    return {
      accessToken,
      accessTokenExpiresAt,
    };
  }
}
