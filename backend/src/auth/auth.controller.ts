import {
  Controller,
  HttpCode,
  HttpStatus,
  Post,
  Req,
  Res,
  UseGuards,
} from '@nestjs/common';
import { LocalAuthGuard } from './guards/local-auth.guard';
import { User } from 'src/users/entities/user.entity';
import { CurrentUser } from './decorators/current-user.decorator';
import { AuthService } from './auth.service';
import { AllowAnon } from './decorators/allow-anon.decorator';
import type { Request, Response } from 'express';
import { ConfigService } from '@nestjs/config';
import { RefreshJwtAuthGuard } from './guards/refresh-jwt-auth.guard';

@Controller('auth')
export class AuthController {
  constructor(
    private readonly authService: AuthService,
    private readonly configService: ConfigService,
  ) {}

  @Post('login')
  @AllowAnon()
  @UseGuards(LocalAuthGuard)
  @HttpCode(HttpStatus.OK)
  async login(
    @CurrentUser() currentUser: Partial<User>,
    @Res({ passthrough: true }) res: Response,
    @Req() req: Request,
  ) {
    const accessToken = await this.authService.login(currentUser as User, req);

    res.cookie('Authentication', accessToken.accessToken, {
      httpOnly: true,
      secure: this.configService.get('NODE_ENV') === 'production',
      expires: accessToken.accessTokenExpiresAt,
    });

    res.cookie('Refresh', accessToken.refreshToken, {
      httpOnly: true,
      secure: this.configService.get('NODE_ENV') === 'production',
      expires: accessToken.refreshTokenExpiresAt,
      path: '/api/auth',
    });
  }

  @Post('refresh-token')
  @AllowAnon()
  @UseGuards(RefreshJwtAuthGuard)
  @HttpCode(HttpStatus.OK)
  async refreshToken(
    @CurrentUser() currentUser: Partial<User>,
    @Req() req: Request,
    @Res({ passthrough: true }) res: Response,
  ) {
    const accessToken = await this.authService.login(currentUser as User, req);

    res.cookie('Authentication', accessToken.accessToken, {
      httpOnly: true,
      secure: this.configService.get('NODE_ENV') === 'production',
      expires: accessToken.accessTokenExpiresAt,
    });

    res.cookie('Refresh', accessToken.refreshToken, {
      httpOnly: true,
      secure: this.configService.get('NODE_ENV') === 'production',
      expires: accessToken.refreshTokenExpiresAt,
      path: '/api/auth',
    });
  }
}
