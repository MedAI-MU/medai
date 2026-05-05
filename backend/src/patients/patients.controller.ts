import {
  Controller,
  Get,
  NotFoundException,
  Param,
  Post,
  Body,
  Patch,
  UseGuards,
  Delete,
} from '@nestjs/common';
import { PatientsService } from './patients.service';
import { Patient } from './entities/patient.entity';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { UpdatePatientDto } from './dtos/update_patient.dto';
import { AllergyDto } from './dtos/allergy.dto';
import { UpdateAllergyDto } from './dtos/update-allergy.dto';
import { SameIdGuard } from '../shared/guards/same-id.guard';
import { Allergy } from './entities/allergy.entity';
import { ChronicDiseaseDto } from './dtos/chronic_disease.dto';
import { UpdateChronicDiseaseDto } from './dtos/update-chronic-disease.dto';
import { ChronicDisease } from './entities/chronic_disease.entity';
import { FamilyHistoryDto } from './dtos/family_history.dto';
import { UpdateFamilyHistoryDto } from './dtos/update-family-history.dto';
import { FamilyHistory } from './entities/family_history.entity';
import { SurgeryDto } from './dtos/surgery.dto';
import { UpdateSurgeryDto } from './dtos/update-surgery.dto';
import { Surgery } from './entities/surgery.entity';
import { EmergencyContactDto } from './dtos/emergency_contact.dto';
import { UpdateEmergencyContactDto } from './dtos/update-emergency-contact.dto';
import { EmergencyContact } from './entities/emergency_contact.entity';
import {
  ApiBody,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { ApiPatientGeneral } from './decorators/api-patient-general.decorator';

@Controller('patients')
export class PatientsController {
  constructor(private readonly patientsService: PatientsService) {}

  @Get()
  @Roles('secretary')
  @UseGuards(RolesGuard)
  @ApiOkResponse({ description: 'Returns an array of patients' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  async getPatients(): Promise<Patient[]> {
    return this.patientsService.findAll();
  }

  @Get(':id')
  @Roles('secretary')
  @UseGuards(SameIdGuard)
  @ApiPatientGeneral()
  @ApiOkResponse({ description: 'Returns a patient' })
  async getPatient(@Param('id') id: number): Promise<Patient> {
    const patient = await this.patientsService.findOne(id);
    if (!patient) {
      throw new NotFoundException('Patient not found');
    }
    return patient;
  }

  @Patch(':id')
  @UseGuards(SameIdGuard)
  @ApiOkResponse({ description: 'Patient updated' })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdatePatientDto })
  async update(
    @Body() data: UpdatePatientDto,
    @Param('id') id: number,
  ): Promise<Patient> {
    return await this.patientsService.update(data, id);
  }

  @Post(':id/allergies')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({ description: 'Added allergy' })
  @ApiPatientGeneral()
  @ApiBody({ type: AllergyDto })
  async addAllergy(
    @Param('id') id: number,
    @Body() data: AllergyDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.addRelation<Allergy>(
      patient,
      'allergies',
      data as Allergy,
    );
  }

  @Patch(':id/allergies/:allergyId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'allergyId', description: 'Allergy ID', type: Number })
  @ApiOkResponse({ description: 'Updated allergy' })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateAllergyDto })
  async updateAllergy(
    @Param('id') id: number,
    @Param('allergyId') allergyId: number,
    @Body() data: UpdateAllergyDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.updateRelation<Allergy>(
      patient,
      'allergies',
      allergyId,
      data as Allergy,
    );
  }

  @Delete(':id/allergies/:allergyId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'allergyId', description: 'Allergy ID', type: Number })
  @ApiOkResponse({ description: 'Removed allergy' })
  @ApiPatientGeneral()
  async removeAllergy(
    @Param('id') id: number,
    @Param('allergyId') allergyId: number,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.removeRelation<Allergy>(
      patient,
      'allergies',
      allergyId,
    );
  }
  @Post(':id/chronic-diseases')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({ description: 'Added chronic disease' })
  @ApiPatientGeneral()
  @ApiBody({ type: ChronicDiseaseDto })
  async addChronicDisease(
    @Param('id') id: number,
    @Body() data: ChronicDiseaseDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.addRelation<ChronicDisease>(
      patient,
      'chronicDiseases',
      data as ChronicDisease,
    );
  }

  @Patch(':id/chronic-diseases/:chronicDiseaseId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'chronicDiseaseId',
    description: 'Chronic Disease ID',
    type: Number,
  })
  @ApiOkResponse({ description: 'Updated chronic disease' })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateChronicDiseaseDto })
  async updateChronicDisease(
    @Param('id') id: number,
    @Param('chronicDiseaseId') chronicDiseaseId: number,
    @Body() data: UpdateChronicDiseaseDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.updateRelation<ChronicDisease>(
      patient,
      'chronicDiseases',
      chronicDiseaseId,
      data as ChronicDisease,
    );
  }

  @Delete(':id/chronic-diseases/:chronicDiseaseId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'chronicDiseaseId',
    description: 'Chronic Disease ID',
    type: Number,
  })
  @ApiOkResponse({ description: 'Removed chronic disease' })
  @ApiPatientGeneral()
  async removeChronicDisease(
    @Param('id') id: number,
    @Param('chronicDiseaseId') chronicDiseaseId: number,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.removeRelation<ChronicDisease>(
      patient,
      'chronicDiseases',
      chronicDiseaseId,
    );
  }
  @Post(':id/family-histories')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({ description: 'Added family history' })
  @ApiPatientGeneral()
  @ApiBody({ type: FamilyHistoryDto })
  async addFamilyHistory(
    @Param('id') id: number,
    @Body() data: FamilyHistoryDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.addRelation<FamilyHistory>(
      patient,
      'familyHistories',
      data as FamilyHistory,
    );
  }

  @Patch(':id/family-histories/:familyHistoryId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'familyHistoryId',
    description: 'Family History ID',
    type: Number,
  })
  @ApiOkResponse({ description: 'Updated family history' })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateFamilyHistoryDto })
  async updateFamilyHistory(
    @Param('id') id: number,
    @Param('familyHistoryId') familyHistoryId: number,
    @Body() data: UpdateFamilyHistoryDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.updateRelation<FamilyHistory>(
      patient,
      'familyHistories',
      familyHistoryId,
      data as FamilyHistory,
    );
  }

  @Delete(':id/family-histories/:familyHistoryId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'familyHistoryId',
    description: 'Family History ID',
    type: Number,
  })
  @ApiOkResponse({ description: 'Removed family history' })
  @ApiPatientGeneral()
  async removeFamilyHistory(
    @Param('id') id: number,
    @Param('familyHistoryId') familyHistoryId: number,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.removeRelation<FamilyHistory>(
      patient,
      'familyHistories',
      familyHistoryId,
    );
  }
  @Post(':id/surgeries')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({ description: 'Added surgery' })
  @ApiPatientGeneral()
  @ApiBody({ type: SurgeryDto })
  async addSurgery(
    @Param('id') id: number,
    @Body() data: SurgeryDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.addRelation<Surgery>(
      patient,
      'surgeries',
      data as Surgery,
    );
  }

  @Patch(':id/surgeries/:surgeryId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'surgeryId', description: 'Surgery ID', type: Number })
  @ApiOkResponse({ description: 'Updated surgery' })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateSurgeryDto })
  async updateSurgery(
    @Param('id') id: number,
    @Param('surgeryId') surgeryId: number,
    @Body() data: UpdateSurgeryDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.updateRelation<Surgery>(
      patient,
      'surgeries',
      surgeryId,
      data as Surgery,
    );
  }

  @Delete(':id/surgeries/:surgeryId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'surgeryId', description: 'Surgery ID', type: Number })
  @ApiOkResponse({ description: 'Removed surgery' })
  @ApiPatientGeneral()
  async removeSurgery(
    @Param('id') id: number,
    @Param('surgeryId') surgeryId: number,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.removeRelation<Surgery>(
      patient,
      'surgeries',
      surgeryId,
    );
  }
  @Post(':id/emergency-contacts')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({ description: 'Added Emergency Contact' })
  @ApiPatientGeneral()
  @ApiBody({ type: EmergencyContactDto })
  async addEmergencyContact(
    @Param('id') id: number,
    @Body() data: EmergencyContactDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.addRelation<EmergencyContact>(
      patient,
      'emergencyContacts',
      data as EmergencyContact,
    );
  }

  @Patch(':id/emergency-contacts/:emergencyContactId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'emergencyContactId',
    description: 'Emergency Contact ID',
    type: Number,
  })
  @ApiOkResponse({ description: 'Updated emergency contact' })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateEmergencyContactDto })
  async updateEmergencyContact(
    @Param('id') id: number,
    @Param('emergencyContactId') emergencyContactId: number,
    @Body() data: UpdateEmergencyContactDto,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.updateRelation<EmergencyContact>(
      patient,
      'emergencyContacts',
      emergencyContactId,
      data as EmergencyContact,
    );
  }

  @Delete(':id/emergency-contacts/:emergencyContactId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'emergencyContactId',
    description: 'Emergency Contact ID',
    type: Number,
  })
  @ApiOkResponse({ description: 'Removed emergency contact' })
  @ApiPatientGeneral()
  async removeEmergencyContact(
    @Param('id') id: number,
    @Param('emergencyContactId') emergencyContactId: number,
  ): Promise<Patient> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    return await this.patientsService.removeRelation<EmergencyContact>(
      patient,
      'emergencyContacts',
      emergencyContactId,
    );
  }
}
