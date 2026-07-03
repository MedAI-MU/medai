import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { RequestWithUser } from 'src/auth/interfaces/request-with-user.interface';

@Injectable()
export class ApprovedGuard implements CanActivate {
  canActivate(context: ExecutionContext): boolean {
    const request = context.switchToHttp().getRequest<RequestWithUser>();
    const user = request.user;
    if (!user) return true;

    if (user.status !== 'approved') {
      throw new ForbiddenException('Account is not approved');
    }

    return true;
  }
}
