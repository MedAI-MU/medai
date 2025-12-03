import { createParamDecorator } from '@nestjs/common';
import { Request } from 'express';
import { User } from 'src/users/entities/user.entity';
import { TokenUser } from '../interfaces/token-user.interface';

export const CurrentUser = createParamDecorator(
  (data: unknown, ctx): Partial<User> | TokenUser | null => {
    const request: Request = ctx.switchToHttp().getRequest();
    if (!request.user) {
      return null;
    }
    return request.user;
  },
);
