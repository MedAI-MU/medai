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
import { Appointment } from './entities/appointment.entity';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller('appointments')
export class AppointmentsController {
  constructor(private readonly appointmentsService: AppointmentsService) {}

  @Roles('patient')
  @UseGuards(RolesGuard)
  @Post()
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: CreateAppointmentDto })
  @ApiCreatedResponse({ description: 'Appointment scheduled successfully' })
  @ApiBadRequestResponse({ description: 'Slot unavailable or invalid input' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async scheduleAppointment(
    @CurrentUser() currentUser: TokenUser,
    @Body() dto: CreateAppointmentDto,
  ): Promise<Appointment> {
    const appointment = await this.appointmentsService.create(
      currentUser.id,
      dto,
    );
    if (!appointment) {
      throw new NotFoundException('Schedule slot not found');
    }
    return appointment;
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Get()
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'All patient appointments retrieved' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getAllAppointments(): Promise<Appointment[]> {
    return this.appointmentsService.findAll();
  }

  @Roles('patient', 'doctor')
  @UseGuards(RolesGuard)
  @Get('me')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Own appointments retrieved (patient or doctor)',
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getMyAppointments(
    @CurrentUser() currentUser: TokenUser,
  ): Promise<Appointment[]> {
    if (currentUser.role === 'doctor') {
      const appointments = await this.appointmentsService.findByDoctor(
        currentUser.id,
      );
      if (!appointments) {
        throw new NotFoundException('Doctor not found');
      }
      return appointments;
    }
    return this.appointmentsService.findByPatient(currentUser.id);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Get('doctor/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Doctor user ID', type: Number })
  @ApiOkResponse({ description: "Doctor's appointments retrieved" })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getDoctorAppointments(
    @Param('id', ParseIntPipe) doctorId: number,
  ): Promise<Appointment[]> {
    const appointments = await this.appointmentsService.findByDoctor(doctorId);
    if (!appointments) {
      throw new NotFoundException('Doctor not found');
    }
    return appointments;
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
  @ApiOkResponse({ description: 'Review submitted successfully' })
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
  ): Promise<Appointment> {
    const appointment = await this.appointmentsService.addReview(
      id,
      dto,
      currentUser.id,
    );
    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }
    return appointment;
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Patch(':id/status')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Appointment ID', type: Number })
  @ApiBody({ type: UpdateAppointmentStatusDto })
  @ApiOkResponse({ description: 'Appointment status updated' })
  @ApiBadRequestResponse({ description: 'Invalid status' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({
    description: 'Patients may only cancel their own appointments',
  })
  async updateStatus(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: UpdateAppointmentStatusDto,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<Appointment> {
    const appointment = await this.appointmentsService.updateStatus(
      id,
      dto,
      currentUser,
    );
    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }
    return appointment;
  }
}
