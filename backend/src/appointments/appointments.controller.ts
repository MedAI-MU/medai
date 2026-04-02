import { Body, Controller, Get, Param, ParseIntPipe, Patch, Post, UseGuards } from '@nestjs/common';
import { AppointmentsService } from './appointments.service';
import { CreateAppointmentDto } from './dtos/create-appointment.dto';
import { CancelAppointmentDto } from './dtos/cancel-appointment.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { ApiTags, ApiOperation, ApiBearerAuth } from '@nestjs/swagger';

@ApiTags('Appointments')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('appointments')
export class AppointmentsController {
  constructor(private readonly appointmentsService: AppointmentsService) {}

  @Post()
  @ApiOperation({ summary: 'Create a new appointment' })
  create(@Body() createDto: CreateAppointmentDto, @CurrentUser() user: TokenUser) {
    return this.appointmentsService.create(createDto, user);
  }

  @Get()
  @ApiOperation({ summary: 'Get all appointments for the current user' })
  findAll(@CurrentUser() user: TokenUser) {
    return this.appointmentsService.findAllForUser(user);
  }

  @Get(':id')
  @ApiOperation({ summary: 'Get a specific appointment by ID' })
  findOne(@Param('id', ParseIntPipe) id: number, @CurrentUser() user: TokenUser) {
    return this.appointmentsService.findOne(id, user);
  }

  @Patch(':id/cancel')
  @ApiOperation({ summary: 'Cancel an appointment' })
  cancel(
    @Param('id', ParseIntPipe) id: number,
    @Body() cancelDto: CancelAppointmentDto,
    @CurrentUser() user: TokenUser,
  ) {
    return this.appointmentsService.cancel(id, cancelDto, user);
  }
}
