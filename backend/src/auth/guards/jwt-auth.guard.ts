import { ExecutionContext, Injectable } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { AuthGuard } from '@nestjs/passport';
import { Observable } from 'rxjs';
import { ALLOW_ANON_KEY } from '../decorators/allow-anon.decorator';

@Injectable()
export class JwtAuthGuard extends AuthGuard('jwt') {
  constructor(private readonly reflector: Reflector) {
    super();
  }

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    return (
      this.reflector.getAllAndOverride<boolean>(ALLOW_ANON_KEY, [
        context.getHandler(),
        context.getClass(),
      ]) || super.canActivate(context)
    );
  }
}
