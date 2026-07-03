import { Inject, Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { Repository } from 'typeorm';
import * as argon2 from 'argon2';
import { JwtService } from '@nestjs/jwt';
import jwtConfig from './jwt.config';
import { type ConfigType } from '@nestjs/config';
import { CredentialsDto } from './dto/credentials.dto';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { Request } from 'express';
import { TokenPayload } from './interfaces/token-payload.interface';
import { AuthCookies } from './interfaces/auth-cookies.interface';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
    @InjectRepository(RefreshToken)
    private readonly refreshTokensRepository: Repository<RefreshToken>,
    private readonly jwtService: JwtService,
    @Inject(jwtConfig.KEY)
    private readonly jwtOptions: ConfigType<typeof jwtConfig>,
  ) {}

  async validateUser(email: string, password: string): Promise<User | null> {
    const user = await this.usersRepository.findOne({ where: { email } });
    if (user && (await argon2.verify(user.password, password))) {
      const { password: _, ...result } = user;
      return result as User;
    }
    return null;
  }

  async login(user: User): Promise<CredentialsDto> {
    const payload: TokenPayload = {
      sub: user.id,
      email: user.email,
      role: user.role,
      status: user.status,
    };

    const accessToken = await this.jwtService.signAsync(payload, {
      secret: this.jwtOptions.tokenSecret,
      expiresIn: `${this.jwtOptions.tokenExpiresInMs}ms`,
    });

    const accessTokenExpiresAt = new Date();
    accessTokenExpiresAt.setMilliseconds(
      accessTokenExpiresAt.getMilliseconds() + this.jwtOptions.tokenExpiresInMs,
    );

    const refreshToken = await this.jwtService.signAsync(payload, {
      secret: this.jwtOptions.refreshTokenSecret,
      expiresIn: `${this.jwtOptions.refreshTokenExpiresInMs}ms`,
    });

    const refreshTokenExpiresAt = new Date();
    refreshTokenExpiresAt.setMilliseconds(
      refreshTokenExpiresAt.getMilliseconds() +
        this.jwtOptions.refreshTokenExpiresInMs,
    );

    const hashedRefreshToken = await argon2.hash(refreshToken);
    const refreshTokenEntity = new RefreshToken({
      token: hashedRefreshToken,
      user,
      expiresAt: refreshTokenExpiresAt,
    });

    await this.refreshTokensRepository.save(refreshTokenEntity);

    return {
      accessToken,
      accessTokenExpiresAt,
      refreshToken,
      refreshTokenExpiresAt,
      id: user.id,
      name: user.name,
      email: user.email,
      phone: user.phone,
      role: user.role,
      status: user.status,
    };
  }

  async removeOldRefreshToken(req: Request, user: User) {
    const refreshTokenCookie = (req.cookies as AuthCookies).Refresh;
    if (refreshTokenCookie) {
      const existingTokens = await this.refreshTokensRepository.find({
        where: { user: { id: user.id } },
      });

      for (const rt of existingTokens) {
        const isMatch = await argon2.verify(rt.token, refreshTokenCookie);
        if (isMatch) {
          await this.refreshTokensRepository.remove(rt);
        }
      }
    }
  }

  async validateRefreshToken(userId: number, refreshToken: string) {
    const existingUserRT = await this.usersRepository.findOne({
      where: { id: userId },
      relations: ['refreshTokens'],
    });

    if (!existingUserRT) {
      return null;
    }

    for (const rt of existingUserRT.refreshTokens) {
      const isMatch = await argon2.verify(rt.token, refreshToken);
      if (isMatch && rt.expiresAt > new Date()) {
        const { password: _p, refreshTokens: _rts, ...result } = existingUserRT;
        return result as User;
      }
    }
    return null;
  }
}
