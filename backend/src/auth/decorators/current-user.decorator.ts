import { createParamDecorator } from '@nestjs/common';
import { Request } from 'express';

export const CurrentUser = createParamDecorator((data: any, ctx): any => {
  const request: Request = ctx.switchToHttp().getRequest();
  if (!request.user) {
    return null;
  }
  return typeof data === 'string' ? request.user[data] : request.user;
});
