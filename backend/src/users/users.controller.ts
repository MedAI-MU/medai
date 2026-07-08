import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  NotFoundException,
  Param,
  ParseIntPipe,
  Post,
  UploadedFile,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { UsersService } from './users.service';
import { UserProfileDto } from './dtos/user-profile.dto';
import { UserResponseDto } from './dtos/user-response.dto';
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
  ApiConsumes,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNoContentResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
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
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'User ID', type: Number, example: 1 })
  @ApiNoContentResponse({ description: 'User removed successfully' })
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
  @ApiOkResponse({
    description: 'Approved secretaries retrieved successfully',
    type: [UserResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getSecretaries(): Promise<UserResponseDto[]> {
    const users = await this.usersService.findByRoleAndStatus(
      'secretary',
      'approved',
    );
    return users.map((u) => new UserResponseDto(u));
  }

  @Get('doctors')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Approved doctors retrieved successfully',
    type: [UserResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getApprovedDoctors(): Promise<UserResponseDto[]> {
    const users = await this.usersService.findByRoleAndStatus(
      'doctor',
      'approved',
    );
    return users.map((u) => new UserResponseDto(u));
  }

  @Get('pending/doctors')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Pending doctors retrieved successfully',
    type: [UserResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getPendingDoctors(): Promise<UserResponseDto[]> {
    const users = await this.usersService.findByRoleAndStatus(
      'doctor',
      'pending',
    );
    return users.map((u) => new UserResponseDto(u));
  }

  @Get('pending/secretaries')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Pending secretaries retrieved successfully',
    type: [UserResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getPendingSecretaries(): Promise<UserResponseDto[]> {
    const users = await this.usersService.findByRoleAndStatus(
      'secretary',
      'pending',
    );
    return users.map((u) => new UserResponseDto(u));
  }

  @Get('managers')
  @Roles('manager')
  @UseGuards(RolesGuard)
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Managers retrieved successfully',
    type: [UserResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getManagers(): Promise<UserResponseDto[]> {
    const users = await this.usersService.findByRole('manager');
    return users.map((u) => new UserResponseDto(u));
  }

  @Get('me')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Current user details',
    type: UserProfileDto,
  })
  async getProfile(
    @CurrentUser() currentUser: TokenUser,
  ): Promise<UserProfileDto> {
    const user = await this.usersService.findById(currentUser.id);
    if (!user) throw new NotFoundException('User not found');
    return new UserProfileDto(user);
  }

  @UseInterceptors(FileInterceptor('file'))
  @Post(':id/avatar')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number, example: 1 })
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: { file: { type: 'string', format: 'binary' } },
    },
  })
  @ApiOkResponse({
    description: 'Avatar uploaded',
    schema: {
      example: {
        avatar: 'https://storage.blob.core.windows.net/avatars/uuid.jpg',
      },
    },
  })
  async uploadAvatar(
    @Param('id', ParseIntPipe) id: number,
    @UploadedFile() file: Express.Multer.File,
  ): Promise<{ avatar: string }> {
    const avatar = await this.usersService.updateAvatar(id, file);
    return { avatar };
  }

  @Delete(':id/avatar')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', type: Number, example: 1 })
  @ApiNoContentResponse({ description: 'Avatar removed' })
  async removeAvatar(@Param('id', ParseIntPipe) id: number): Promise<void> {
    await this.usersService.deleteAvatar(id);
  }
}
