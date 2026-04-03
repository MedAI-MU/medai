import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { ROLES_KEY } from 'src/auth/decorators/roles.decorator';
import { RequestWithUser } from 'src/auth/interfaces/request-with-user.interface';
import { UserRoles } from 'src/users/types/role.types';

@Injectable()
export class SameIdGuard implements CanActivate {
  constructor(private readonly reflector: Reflector) {}

  canActivate(context: ExecutionContext): boolean {
    const allowedRoles = this.reflector.getAllAndOverride<UserRoles[]>(
      ROLES_KEY,
      [context.getHandler(), context.getClass()],
    );
    const request = context.switchToHttp().getRequest<RequestWithUser>();
    const user = request.user;
    if (!user) return false;
    const paramId = request.params.id;
    return (
      (typeof paramId === 'string' && user.id === parseInt(paramId)) ||
      (allowedRoles && allowedRoles.includes(user.role))
    );
  }
}
