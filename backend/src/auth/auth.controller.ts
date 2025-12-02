import { Controller, Post, Request, UseGuards } from '@nestjs/common';
import { LocalAuthGuard } from './guards/local-auth.guard';
import { User } from 'src/users/entities/users.entity';
import { CurrentUser } from './decorators/current-user.decorator';
import { AuthService } from './auth.service';
import { AllowAnon } from './decorators/allow-anon.decorator';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Post('login')
  @UseGuards(LocalAuthGuard)
  @AllowAnon()
  login(@CurrentUser() currentUser: Partial<User>) {
    const accessToken = this.authService.login(currentUser as User);
    // we can return directly to the user and he save it in the local storage and send it in the authorization header
    return accessToken;
    // we can instead set a cookie in the response to prevent XSS attacks
  }
}
