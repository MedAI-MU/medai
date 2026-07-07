import {
  BadRequestException,
  ConflictException,
  Inject,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
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
import { AuthMailerService } from './auth-mailer.service';
import { createHash, randomBytes } from 'node:crypto';
import { UpdateProfileDto } from './dto/update-profile.dto';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private readonly usersRepository: Repository<User>,
    @InjectRepository(RefreshToken)
    private readonly refreshTokensRepository: Repository<RefreshToken>,
    private readonly jwtService: JwtService,
    @Inject(jwtConfig.KEY)
    private readonly jwtOptions: ConfigType<typeof jwtConfig>,
    private readonly authMailerService: AuthMailerService,
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

  async sendVerificationEmail(email: string): Promise<void> {
    const user = await this.usersRepository.findOne({ where: { email } });

    if (!user || user.emailVerified) {
      return;
    }

    const rawToken = randomBytes(32).toString('hex');
    user.verificationToken = createHash('sha256')
      .update(rawToken)
      .digest('hex');
    user.verificationTokenExpiresAt = new Date(Date.now() + 86_400_000);
    await this.usersRepository.save(user);

    await this.authMailerService.sendVerificationEmail(
      user.email,
      user.name,
      rawToken,
    );
  }

  async verifyEmail(token: string): Promise<void> {
    const hashedToken = createHash('sha256').update(token).digest('hex');
    const user = await this.usersRepository.findOne({
      where: { verificationToken: hashedToken },
    });

    if (!user) {
      throw new BadRequestException('Invalid verification token');
    }

    if (
      user.verificationTokenExpiresAt &&
      user.verificationTokenExpiresAt < new Date()
    ) {
      throw new BadRequestException('Verification token has expired');
    }

    if (user.pendingEmail) {
      user.email = user.pendingEmail;
      user.pendingEmail = null;
    }

    user.emailVerified = true;
    user.verificationToken = null;
    user.verificationTokenExpiresAt = null;
    await this.usersRepository.save(user);
  }

  async forgotPassword(email: string): Promise<void> {
    const user = await this.usersRepository.findOne({ where: { email } });

    if (!user) {
      return;
    }

    const rawToken = randomBytes(32).toString('hex');
    user.resetPasswordToken = createHash('sha256')
      .update(rawToken)
      .digest('hex');
    user.resetPasswordExpiresAt = new Date(Date.now() + 3_600_000);
    await this.usersRepository.save(user);

    await this.authMailerService.sendPasswordResetEmail(
      user.email,
      user.name,
      rawToken,
    );
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    const hashedToken = createHash('sha256').update(token).digest('hex');
    const user = await this.usersRepository.findOne({
      where: { resetPasswordToken: hashedToken },
    });

    if (
      !user ||
      !user.resetPasswordExpiresAt ||
      user.resetPasswordExpiresAt < new Date()
    ) {
      throw new BadRequestException('Invalid or expired reset token');
    }

    user.password = await argon2.hash(newPassword);
    user.resetPasswordToken = null;
    user.resetPasswordExpiresAt = null;
    await this.usersRepository.save(user);
  }

  async requestEmailChange(
    userId: number,
    password: string,
    newEmail: string,
  ): Promise<void> {
    const user = await this.usersRepository.findOne({
      where: { id: userId },
    });

    if (!user) {
      throw new NotFoundException('User not found');
    }

    if (!(await argon2.verify(user.password, password))) {
      throw new BadRequestException('Invalid password');
    }

    if (user.email === newEmail) {
      throw new BadRequestException('New email is the same as current email');
    }

    const existingUser = await this.usersRepository.findOne({
      where: { email: newEmail },
    });

    if (existingUser) {
      throw new ConflictException('Email already in use');
    }

    const rawToken = randomBytes(32).toString('hex');
    user.pendingEmail = newEmail;
    user.verificationToken = createHash('sha256')
      .update(rawToken)
      .digest('hex');
    user.verificationTokenExpiresAt = new Date(Date.now() + 86_400_000);
    await this.usersRepository.save(user);

    await this.authMailerService.sendVerificationEmail(
      newEmail,
      user.name,
      rawToken,
    );
  }

  async updateProfile(userId: number, dto: UpdateProfileDto): Promise<void> {
    const updateData: Partial<User> = {};

    if (dto.name !== undefined) {
      updateData.name = dto.name;
    }
    if (dto.phone !== undefined) {
      updateData.phone = dto.phone;
    }
    if (dto.birthDate !== undefined) {
      updateData.birthDate = new Date(dto.birthDate);
    }
    if (dto.gender !== undefined) {
      updateData.gender = dto.gender as 'male' | 'female';
    }

    const result = await this.usersRepository.update(userId, updateData);

    if (result.affected === 0) {
      throw new NotFoundException('User not found');
    }
  }
}
