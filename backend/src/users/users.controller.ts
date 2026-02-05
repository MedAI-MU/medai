import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { UsersService } from './users.service';
import { RegisterDto } from './dtos/register.dto';
import { AllowAnon } from 'src/auth/decorators/allow-anon.decorator';
import {
  ApiBody,
  ApiCreatedResponse,
  ApiConflictResponse,
  ApiBadRequestResponse,
} from '@nestjs/swagger';

@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @Post()
  @AllowAnon()
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: RegisterDto })
  @ApiCreatedResponse({ description: 'User successfully registered' })
  @ApiConflictResponse({ description: 'Phone number or email already in use' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  async registerUser(@Body() registerDto: RegisterDto) {
    await this.usersService.registerUser(registerDto);
  }
}
