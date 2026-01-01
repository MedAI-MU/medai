import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { RegisterDto } from './dtos/register.dto';
import { AllowAnon } from 'src/auth/decorators/allow-anon.decorator';
import { ApiBody } from '@nestjs/swagger';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Post()
  @AllowAnon()
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: RegisterDto })
  async registerUser(@Body() registerDto: RegisterDto) {
    await this.usersService.registerUser(registerDto);
  }
}
