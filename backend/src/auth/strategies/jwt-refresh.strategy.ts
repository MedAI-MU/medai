import { Inject, Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import jwtConfig from '../jwt.config';
import { type ConfigType } from '@nestjs/config';
import { Request } from 'express';
import { TokenPayload } from '../interfaces/token-payload.interface';
import { AuthService } from '../auth.service';
import { AuthCookies } from '../interfaces/auth-cookies.interface';

@Injectable()
export class JwtRefreshStrategy extends PassportStrategy(
  Strategy,
  'jwt-refresh',
) {
  constructor(
    @Inject(jwtConfig.KEY)
    private readonly jwtOptions: ConfigType<typeof jwtConfig>,
    private readonly authService: AuthService,
  ) {
    super({
      jwtFromRequest: ExtractJwt.fromExtractors([
        (req: Request) => (req.cookies as AuthCookies).Refresh || null,
      ]),
      ignoreExpiration: false,
      secretOrKey: jwtOptions.refreshTokenSecret,
      passReqToCallback: true,
    });
  }

  async validate(req: Request, payload: TokenPayload) {
    return await this.authService.validateRefreshToken(
      payload.sub,
      (req.cookies as AuthCookies).Refresh as string,
    );
  }
}
