import {
  Body,
  Controller,
  HttpCode,
  HttpStatus,
  Post,
  UseGuards,
} from '@nestjs/common';
import { UsersService } from './users.service';
import { RegisterDto } from './dtos/register.dto';
import { AddUserRoleDto } from './dtos/add-user-role.dto';
import { AllowAnon } from 'src/auth/decorators/allow-anon.decorator';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import {
  ApiBadRequestResponse,
  ApiBody,
  ApiConflictResponse,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNotFoundResponse,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { PatientsService } from 'src/patients/patients.service';

@Controller('users')
export class UsersController {
  constructor(
    private readonly usersService: UsersService,
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
    await this.patientsService.create(user.id);
  }

  @Post('add/doctor')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: AddUserRoleDto })
  @ApiCreatedResponse({ description: 'Doctor added successfully' })
  @ApiNotFoundResponse({ description: 'User not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async addDoctor(@Body() addDoctorDto: AddUserRoleDto) {
    await this.usersService.addDoctorRole(addDoctorDto.userId);
  }

  @Post('add/secretary')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: AddUserRoleDto })
  @ApiCreatedResponse({ description: 'Secretary added successfully' })
  @ApiNotFoundResponse({ description: 'User not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async addSecretary(@Body() addSecretaryDto: AddUserRoleDto) {
    await this.usersService.addSecretaryRole(addSecretaryDto.userId);
  }
}
