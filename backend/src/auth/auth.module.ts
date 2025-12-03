import { Module } from '@nestjs/common';
import { AuthController } from './auth.controller';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { AuthService } from './auth.service';
import { PassportModule } from '@nestjs/passport';
import { LocalStrategy } from './strategies/local.strategy';
import { JwtModule } from '@nestjs/jwt';
import { JwtStrategy } from './strategies/jwt.strategy';
import { APP_GUARD } from '@nestjs/core';
import { JwtAuthGuard } from './guards/jwt-auth.guard';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { JwtRefreshStrategy } from './strategies/jwt-refresh.strategy';

@Module({
  imports: [
    TypeOrmModule.forFeature([User, RefreshToken]),
    PassportModule,
    JwtModule,
  ],
  controllers: [AuthController],
  providers: [
    AuthService,
    LocalStrategy,
    JwtStrategy,
    JwtRefreshStrategy,
    {
      provide: APP_GUARD,
      useClass: JwtAuthGuard,
    },
  ],
})
export class AuthModule {}
