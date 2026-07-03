import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Post,
  UseGuards,
} from '@nestjs/common';
import { UsersService } from './users.service';
import { RegisterDto } from './dtos/register.dto';
import { AddUserRoleDto } from './dtos/add-user-role.dto';
import { AllowAnon } from 'src/auth/decorators/allow-anon.decorator';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { ApprovedGuard } from './guards/approved.guard';
import {
  ApiBadRequestResponse,
  ApiBody,
  ApiConflictResponse,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { PatientsService } from 'src/patients/patients.service';

@Controller('users')
@UseGuards(ApprovedGuard)
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
    if (registerDto.role === 'patient') {
      await this.patientsService.create(user.id);
    }
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

  @Delete(':id')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'User ID', type: Number })
  @ApiOkResponse({ description: 'User removed successfully' })
  @ApiNotFoundResponse({ description: 'User not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async removeUser(@Param('id') id: number) {
    await this.usersService.removeUser(id);
  }

  @Get('secretaries')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'Approved secretaries retrieved successfully' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getSecretaries() {
    return this.usersService.findByRoleAndStatus('secretary', 'approved');
  }

  @Get('pending/doctors')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'Pending doctors retrieved successfully' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getPendingDoctors() {
    return this.usersService.findByRoleAndStatus('doctor', 'pending');
  }

  @Get('pending/secretaries')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'Pending secretaries retrieved successfully' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getPendingSecretaries() {
    return this.usersService.findByRoleAndStatus('secretary', 'pending');
  }

  @Get('managers')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'Managers retrieved successfully' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getManagers() {
    return this.usersService.findByRole('manager');
  }
}
