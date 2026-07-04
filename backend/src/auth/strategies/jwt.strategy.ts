import { Inject, Injectable } from '@nestjs/common';
import { PassportStrategy } from '@nestjs/passport';
import { ExtractJwt, Strategy } from 'passport-jwt';
import jwtConfig from '../jwt.config';
import { type ConfigType } from '@nestjs/config';
import { Request } from 'express';
import { TokenUser } from '../interfaces/token-user.interface';
import { TokenPayload } from '../interfaces/token-payload.interface';
import { AuthCookies } from '../interfaces/auth-cookies.interface';

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor(
    @Inject(jwtConfig.KEY) private jwtOptions: ConfigType<typeof jwtConfig>,
  ) {
    super({
      jwtFromRequest: ExtractJwt.fromExtractors([
        (req: Request) => (req.cookies as AuthCookies).Authentication || null,
      ]),
      ignoreExpiration: false,
      secretOrKey: jwtOptions.tokenSecret,
    });
  }
  validate(payload: TokenPayload): TokenUser {
    return {
      id: payload.sub,
      email: payload.email,
      role: payload.role,
      status: payload.status,
    };
  }
}
