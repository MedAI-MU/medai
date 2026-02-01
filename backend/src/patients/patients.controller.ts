import {
  Controller,
  Get,
  ForbiddenException,
  Param,
  Post,
  Body,
  Patch,
} from '@nestjs/common';
import { PatientsService } from './patients.service';
import { Patient } from './entities/patient.entity';
import { CurrentUser } from 'src/auth/decorators/current-user.decorator';
import { User } from 'src/users/entities/user.entity';
import { PatientDto } from './dtos/patient.dto';

@Controller('patients')
export class PatientsController {
  constructor(private readonly patientsService: PatientsService) {}

  @Get()
  async getPatients(@CurrentUser() user: User): Promise<Patient[]> {
    if (user.role === 'secretary') {
      return this.patientsService.findAll();
    }
    // TODO: For doctors, we will retrieve the patients that are on there schedules.
    throw new ForbiddenException();
  }
  @Get(':id')
  async getPatient(
    @CurrentUser() user: User,
    @Param('id') id: string,
  ): Promise<Patient | null> {
    const allowedPatient = user.role === 'patient' && user.id === parseInt(id);
    if (user.role === 'secretary' || allowedPatient) {
      return await this.patientsService.findOne(parseInt(id));
    }
    // TODO: For doctors, we will retrieve the patient if they are on there schedules.
    throw new ForbiddenException();
  }

  @Post()
  async create(
    @CurrentUser() user: User,
    @Body() patient: PatientDto,
  ): Promise<Patient> {
    if (user.role === 'patient' || user.role === 'secretary') {
      return await this.patientsService.create({
        userId: user.id,
        ...patient,
      } as Patient);
    }
    throw new ForbiddenException();
  }
  // TODO: add endpoints for allergies, chronic diseases, family histories, and surgeries.
}
