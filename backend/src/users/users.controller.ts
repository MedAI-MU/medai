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
import { DoctorsService } from 'src/doctors/doctors.service';
import { PatientsService } from 'src/patients/patients.service';

@Controller('users')
export class UsersController {
  constructor(
    private readonly usersService: UsersService,
    private readonly doctorsService: DoctorsService,
    private readonly patientsService: PatientsService,
  ) {}

  @Post()
  @AllowAnon()
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: RegisterDto })
  @ApiCreatedResponse({ description: 'User successfully registered' })
  @ApiConflictResponse({ description: 'Phone number or email already in use' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  async registerUser(@Body() registerDto: RegisterDto) {
    const user = await this.usersService.registerUser(registerDto);
    if (registerDto.role === 'doctor') {
      await this.doctorsService.create(user.id);
    } else if (registerDto.role === 'patient') {
      await this.patientsService.create(user.id);
    }
  }
}
