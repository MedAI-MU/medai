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
import { ApiBody, ApiOkResponse, ApiBadRequestResponse } from '@nestjs/swagger';
import { LoginDto } from './dto/login.dto';
import { CredentialsDto } from './dto/credentials.dto';
import { AuthenticatedUserDto } from './dto/authenticated-user.dto';

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
  ): Promise<CredentialsDto> {
    // remove existing refresh token
    await this.authService.removeOldRefreshToken(req, user);
    // issue new tokens
    const credentials = await this.authService.login(user);

    res.cookie('Authentication', credentials.accessToken, {
      httpOnly: true,
      secure: this.configService.get('NODE_ENV') === 'production',
      expires: credentials.accessTokenExpiresAt,
      sameSite: 'strict',
    });

    res.cookie('Refresh', credentials.refreshToken, {
      httpOnly: true,
      secure: this.configService.get('NODE_ENV') === 'production',
      expires: credentials.refreshTokenExpiresAt,
      path: '/',
      sameSite: 'strict',
    });

    return credentials;
  }

  @Post('login')
  @AllowAnon()
  @UseGuards(LocalAuthGuard)
  @HttpCode(HttpStatus.OK)
  @ApiBody({ type: LoginDto })
  @ApiOkResponse({
    description: 'User authenticated and cookies set',
    type: AuthenticatedUserDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid Email or Password' })
  async login(
    @CurrentUser() currentUser: Partial<User>,
    @Res({ passthrough: true }) res: Response,
    @Req() req: Request,
  ): Promise<AuthenticatedUserDto> {
    // logged in user trying to login again
    // remove existing refresh token if refresh cookie exists and then issue new tokens
    const credentials = await this.issueTokensAndSetCookies(
      currentUser as User,
      req,
      res,
    );

    return {
      id: credentials.id,
      name: credentials.name,
      email: credentials.email,
      phone: credentials.phone,
      role: credentials.role,
    };
  }

  @Post('refresh-token')
  @AllowAnon()
  @UseGuards(RefreshJwtAuthGuard)
  @HttpCode(HttpStatus.OK)
  @ApiBody({ type: LoginDto })
  @ApiOkResponse({
    description:
      'Tokens refreshed and cookies updated (No tokens returned in response)',
  })
  @ApiBadRequestResponse({ description: 'Invalid refresh token' })
  async refreshToken(
    @CurrentUser() currentUser: Partial<User>,
    @Req() req: Request,
    @Res({ passthrough: true }) res: Response,
  ) {
    // remove existing refresh token and then issue new tokens
    await this.issueTokensAndSetCookies(currentUser as User, req, res);
  }
}
