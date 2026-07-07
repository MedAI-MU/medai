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
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiBody,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNoContentResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { AppointmentsService } from './appointments.service';
import { CreateAppointmentDto } from './dtos/create-appointment.dto';
import { UpdateAppointmentStatusDto } from './dtos/update-appointment-status.dto';
import { ReviewAppointmentDto } from './dtos/review-appointment.dto';
import { AppointmentDto } from './dtos/appointment.dto';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from 'src/users/guards/approved.guard';
import { VerifiedGuard } from 'src/auth/guards/verified.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller('appointments')
@UseGuards(ApprovedGuard, VerifiedGuard)
export class AppointmentsController {
  constructor(private readonly appointmentsService: AppointmentsService) {}

  @Roles('patient')
  @UseGuards(RolesGuard)
  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: CreateAppointmentDto })
  @ApiCreatedResponse({
    description: 'Appointment scheduled successfully',
    type: AppointmentDto,
  })
  @ApiBadRequestResponse({ description: 'Slot unavailable or invalid input' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async scheduleAppointment(
    @CurrentUser() currentUser: TokenUser,
    @Body() dto: CreateAppointmentDto,
  ): Promise<AppointmentDto> {
    const appointment = await this.appointmentsService.create(
      currentUser.id,
      dto,
    );
    if (!appointment) {
      throw new NotFoundException('Schedule slot not found');
    }
    return new AppointmentDto(appointment);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Get()
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'All patient appointments retrieved',
    type: [AppointmentDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getAllAppointments(): Promise<AppointmentDto[]> {
    const appointments = await this.appointmentsService.findAll();
    return appointments.map((a) => new AppointmentDto(a));
  }

  @Roles('patient', 'doctor')
  @UseGuards(RolesGuard)
  @Get('me')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Own appointments retrieved (patient or doctor)',
    type: [AppointmentDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getMyAppointments(
    @CurrentUser() currentUser: TokenUser,
  ): Promise<AppointmentDto[]> {
    if (currentUser.role === 'doctor') {
      const appointments = await this.appointmentsService.findByDoctor(
        currentUser.id,
      );
      if (!appointments) {
        throw new NotFoundException('Doctor not found');
      }
      return appointments.map((a) => new AppointmentDto(a));
    }
    const patientAppointments = await this.appointmentsService.findByPatient(
      currentUser.id,
    );
    return patientAppointments.map((a) => new AppointmentDto(a));
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Get('doctor/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Doctor user ID', type: Number })
  @ApiOkResponse({
    description: "Doctor's appointments retrieved",
    type: [AppointmentDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getDoctorAppointments(
    @Param('id', ParseIntPipe) doctorId: number,
  ): Promise<AppointmentDto[]> {
    const appointments = await this.appointmentsService.findByDoctor(doctorId);
    if (!appointments) {
      throw new NotFoundException('Doctor not found');
    }
    return appointments.map((a) => new AppointmentDto(a));
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Appointment ID', type: Number })
  @ApiNoContentResponse({ description: 'Appointment deleted successfully' })
  @ApiNotFoundResponse({ description: 'Appointment not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async deleteAppointment(
    @Param('id', ParseIntPipe) id: number,
  ): Promise<void> {
    const deleted = await this.appointmentsService.delete(id);
    if (!deleted) {
      throw new NotFoundException('Appointment not found');
    }
  }

  @Roles('patient')
  @UseGuards(RolesGuard)
  @Patch(':id/review')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Appointment ID', type: Number })
  @ApiBody({ type: ReviewAppointmentDto })
  @ApiOkResponse({
    description: 'Review submitted successfully',
    type: AppointmentDto,
  })
  @ApiBadRequestResponse({
    description: 'Appointment is not confirmed or invalid input',
  })
  @ApiNotFoundResponse({ description: 'Appointment not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({
    description: 'You can only review your own appointments',
  })
  async addReview(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: ReviewAppointmentDto,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<AppointmentDto> {
    const appointment = await this.appointmentsService.addReview(
      id,
      dto,
      currentUser.id,
    );
    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }
    return new AppointmentDto(appointment);
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Patch(':id/status')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Appointment ID', type: Number })
  @ApiBody({ type: UpdateAppointmentStatusDto })
  @ApiOkResponse({
    description: 'Appointment status updated',
    type: AppointmentDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid status' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({
    description: 'Patients may only cancel their own appointments',
  })
  async updateStatus(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: UpdateAppointmentStatusDto,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<AppointmentDto> {
    const appointment = await this.appointmentsService.updateStatus(
      id,
      dto,
      currentUser,
    );
    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }
    return new AppointmentDto(appointment);
  }
}
