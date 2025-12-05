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
  private async issueTokensAndSetCookies(
    user: User,
    req: Request,
    res: Response,
  ) {
    // remove existing refresh token
    await this.authService.removeOldRefreshToken(req, user);
    // issue new tokens
    const accessToken = await this.authService.login(user);

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

  @Post('login')
  @AllowAnon()
  @UseGuards(LocalAuthGuard)
  @HttpCode(HttpStatus.OK)
  async login(
    @CurrentUser() currentUser: Partial<User>,
    @Res({ passthrough: true }) res: Response,
    @Req() req: Request,
  ) {
    // logged in user trying to login again
    // remove existing refresh token if refresh cookie exists and then issue new tokens
    await this.issueTokensAndSetCookies(currentUser as User, req, res);
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
    // remove existing refresh token and then issue new tokens
    await this.issueTokensAndSetCookies(currentUser as User, req, res);
  }
}
