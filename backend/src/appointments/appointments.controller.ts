import {
  Controller,
  Get,
  Post,
  Body,
  Patch,
  Param,
  ParseIntPipe,
} from '@nestjs/common';
import { AppointmentsService } from './appointments.service';
import { CreateAppointmentDto } from './dtos/create-appointment.dto';
import { UpdateAppointmentStatusDto } from './dtos/update-appointment-status.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller('appointments')
export class AppointmentsController {
  constructor(private readonly appointmentsService: AppointmentsService) {}

  @Post()
  create(
    @Body() createAppointmentDto: CreateAppointmentDto,
    @CurrentUser() user: TokenUser,
  ) {
    return this.appointmentsService.create(createAppointmentDto, user);
  }

  @Get('my-appointments')
  getMyPatientAppointments(@CurrentUser() user: TokenUser) {
    // Assuming patientId == userId, which is true based on Patient entity (userId is PrimaryColumn)
    return this.appointmentsService.getForPatient(user.id);
  }

  @Get('doctor-appointments')
  getMyDoctorAppointments(@CurrentUser() user: TokenUser) {
    // Assuming doctorId == userId, which is true based on Doctor entity (userId is PrimaryColumn)
    return this.appointmentsService.getForDoctor(user.id);
  }

  @Get(':id')
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.appointmentsService.getById(id);
  }

  @Patch(':id/status')
  updateStatus(
    @Param('id', ParseIntPipe) id: number,
    @Body() updateAppointmentStatusDto: UpdateAppointmentStatusDto,
  ) {
    return this.appointmentsService.updateStatus(id, updateAppointmentStatusDto);
  }
}
