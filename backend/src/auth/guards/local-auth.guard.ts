import {
  BadRequestException,
  ExecutionContext,
  Injectable,
  ValidationPipe,
} from '@nestjs/common';
import { AuthGuard } from '@nestjs/passport';
import { LoginDto } from '../dto/login.dto';
import { Request } from 'express';

@Injectable()
export class LocalAuthGuard extends AuthGuard('local') {
  private readonly validationPipe = new ValidationPipe({
    whitelist: true,
    transform: true,
    forbidNonWhitelisted: true,
    exceptionFactory: (errors) => {
      const messages = errors.map((err) => ({
        field: err.property,
        errors: Object.values(err.constraints!),
      }));

      return new BadRequestException({
        success: false,
        message: 'Validation failed',
        validationErrors: messages,
      });
    },
  });

  async canActivate(context: ExecutionContext): Promise<boolean> {
    try {
      // Validate the body first and assign a properly typed body
      const req = context
        .switchToHttp()
        .getRequest<Request & { body: LoginDto }>();
      const validated = (await this.validationPipe.transform(req.body, {
        type: 'body',
        metatype: LoginDto,
      })) as LoginDto;
      req.body = validated;
    } catch (err) {
      throw new BadRequestException(err);
    }
    return super.canActivate(context) as boolean | Promise<boolean>;
  }
  handleRequest<TUser = any>(
    err: any,
    user: any,
    info: any,
    context: ExecutionContext,
    status?: any,
  ): TUser {
    if (err || !user) {
      throw new BadRequestException('Invalid email or password');
    }
    return user;
  }
}
