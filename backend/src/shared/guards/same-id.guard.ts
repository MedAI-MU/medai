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

    let targetId: number | null = null;
    const paramId = request.params.id;
    if (typeof paramId === 'string') {
        targetId = parseInt(paramId, 10);
    } else if (Array.isArray(paramId) && paramId.length > 0) {
        targetId = parseInt(paramId[0], 10);
    }

    return (
      (targetId !== null && user.id === targetId) ||
      (allowedRoles && allowedRoles.includes(user.role))
    );
  }
}
