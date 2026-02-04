import {
  Controller,
  Get,
  Param,
  Post,
  Body,
  Patch,
  Put,
  UseGuards,
  ParseArrayPipe,
} from '@nestjs/common';
import { PatientsService } from './patients.service';
import { Patient } from './entities/patient.entity';
import { CurrentUser } from 'src/auth/decorators/current-user.decorator';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { User } from 'src/users/entities/user.entity';
import { PatientDto } from './dtos/patient.dto';
import { UpdatePatientDto } from './dtos/update_patient.dto';
import { AllergyDto } from './dtos/allergy.dto';
import { PatientGuard } from './guards/patient.guard';
import { Allergy } from './entities/allergy.entity';
import { ChronicDiseaseDto } from './dtos/chronic_disease.dto';
import { ChronicDisease } from './entities/chronic_disease.entity';
import { FamilyHistoryDto } from './dtos/family_history.dto';
import { FamilyHistory } from './entities/family_history.entity';
import { SurgeryDto } from './dtos/surgery.dto';
import { Surgery } from './entities/surgery.entity';
import { EmergencyContactDto } from './dtos/emergency_contact.dto';
import { EmergencyContact } from './entities/emergency_contact.entity';

@Controller('patients')
export class PatientsController {
  constructor(private readonly patientsService: PatientsService) {}

  @Get()
  @Roles('secretary')
  @UseGuards(RolesGuard)
  async getPatients(): Promise<Patient[]> {
    return this.patientsService.findAll();
  }
  @Get(':id')
  @Roles('secretary')
  @UseGuards(PatientGuard)
  async getPatient(@Param('id') id: number): Promise<Patient | null> {
    return await this.patientsService.findOne(id);
  }

  @Post()
  @UseGuards(RolesGuard)
  @Roles('patient', 'secretary')
  async create(
    @CurrentUser() user: User,
    @Body() patient: PatientDto,
  ): Promise<Patient> {
    return await this.patientsService.create({
      userId: user.id,
      ...patient,
    } as Patient);
  }

  @Patch(':id')
  @UseGuards(PatientGuard)
  async update(
    @Body() data: UpdatePatientDto,
    @Param('id') id: number,
  ): Promise<Patient> {
    return await this.patientsService.update(data, id);
  }

  @Put(':id/allergies')
  @UseGuards(PatientGuard)
  async updateAllergies(
    @Param('id') id: number,
    @Body(new ParseArrayPipe({ items: AllergyDto })) data: AllergyDto[],
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    patient.allergies = data as Allergy[];
    return patient.save();
  }

  @Put(':id/chronic-diseases')
  @UseGuards(PatientGuard)
  async updateChronicDiseases(
    @Param('id') id: number,
    @Body(new ParseArrayPipe({ items: ChronicDiseaseDto }))
    data: ChronicDiseaseDto[],
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    patient.chronicDiseases = data as ChronicDisease[];
    return patient.save();
  }

  @Put(':id/family-histories')
  @UseGuards(PatientGuard)
  async updateFamilyHistories(
    @Param('id') id: number,
    @Body(new ParseArrayPipe({ items: FamilyHistoryDto }))
    data: FamilyHistoryDto[],
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    patient.familyHistories = data as FamilyHistory[];
    return patient.save();
  }

  @Put(':id/surgeries')
  @UseGuards(PatientGuard)
  async updateSurgeries(
    @Param('id') id: number,
    @Body(new ParseArrayPipe({ items: SurgeryDto })) data: SurgeryDto[],
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    patient.surgeries = data as Surgery[];
    return patient.save();
  }

  @Put(':id/emergency-contacts')
  @UseGuards(PatientGuard)
  async updateEmergencyContacts(
    @Param('id') id: number,
    @Body(new ParseArrayPipe({ items: EmergencyContactDto }))
    data: EmergencyContactDto[],
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    patient.emergencyContacts = data as EmergencyContact[];
    return patient.save();
  }
}
