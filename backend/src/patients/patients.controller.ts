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
import { PatientResponseDto } from './dtos/patient-response.dto';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { UpdatePatientDto } from './dtos/update_patient.dto';
import { AllergyDto } from './dtos/allergy.dto';
import { UpdateAllergyDto } from './dtos/update-allergy.dto';
import { SameIdGuard } from '../shared/guards/same-id.guard';
import { ApprovedGuard } from 'src/users/guards/approved.guard';
import { VerifiedGuard } from 'src/auth/guards/verified.guard';
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
@UseGuards(ApprovedGuard, VerifiedGuard)
export class PatientsController {
  constructor(private readonly patientsService: PatientsService) {}

  @Get()
  @Roles('secretary', 'manager')
  @UseGuards(RolesGuard)
  @ApiOkResponse({
    description: 'Returns an array of patients',
    type: [PatientResponseDto],
  })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  async getPatients(): Promise<PatientResponseDto[]> {
    const patients = await this.patientsService.findAll();
    return patients.map((p) => new PatientResponseDto(p));
  }

  @Get(':id')
  @Roles('secretary', 'doctor')
  @UseGuards(SameIdGuard)
  @ApiPatientGeneral()
  @ApiOkResponse({ description: 'Returns a patient', type: PatientResponseDto })
  async getPatient(@Param('id') id: number): Promise<PatientResponseDto> {
    const patient = await this.patientsService.findOne(id);
    if (!patient) {
      throw new NotFoundException('Patient not found');
    }
    return new PatientResponseDto(patient);
  }

  @Patch(':id')
  @UseGuards(SameIdGuard)
  @ApiOkResponse({ description: 'Patient updated', type: PatientResponseDto })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdatePatientDto })
  async update(
    @Body() data: UpdatePatientDto,
    @Param('id') id: number,
  ): Promise<PatientResponseDto> {
    const patient = await this.patientsService.update(data, id);
    return new PatientResponseDto(patient);
  }

  @Post(':id/allergies')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({
    description: 'Added allergy',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: AllergyDto })
  async addAllergy(
    @Param('id') id: number,
    @Body() data: AllergyDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient = await this.patientsService.addRelation<Allergy>(
      patient,
      'allergies',
      data as Allergy,
    );
    return new PatientResponseDto(updatedPatient);
  }

  @Patch(':id/allergies/:allergyId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'allergyId', description: 'Allergy ID', type: Number })
  @ApiOkResponse({ description: 'Updated allergy', type: PatientResponseDto })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateAllergyDto })
  async updateAllergy(
    @Param('id') id: number,
    @Param('allergyId') allergyId: number,
    @Body() data: UpdateAllergyDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient = await this.patientsService.updateRelation<Allergy>(
      patient,
      'allergies',
      allergyId,
      data as Allergy,
    );
    return new PatientResponseDto(updatedPatient);
  }

  @Delete(':id/allergies/:allergyId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'allergyId', description: 'Allergy ID', type: Number })
  @ApiOkResponse({ description: 'Removed allergy', type: PatientResponseDto })
  @ApiPatientGeneral()
  async removeAllergy(
    @Param('id') id: number,
    @Param('allergyId') allergyId: number,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient = await this.patientsService.removeRelation<Allergy>(
      patient,
      'allergies',
      allergyId,
    );
    return new PatientResponseDto(updatedPatient);
  }
  @Post(':id/chronic-diseases')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({
    description: 'Added chronic disease',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: ChronicDiseaseDto })
  async addChronicDisease(
    @Param('id') id: number,
    @Body() data: ChronicDiseaseDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.addRelation<ChronicDisease>(
        patient,
        'chronicDiseases',
        data as ChronicDisease,
      );
    return new PatientResponseDto(updatedPatient);
  }

  @Patch(':id/chronic-diseases/:chronicDiseaseId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'chronicDiseaseId',
    description: 'Chronic Disease ID',
    type: Number,
  })
  @ApiOkResponse({
    description: 'Updated chronic disease',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateChronicDiseaseDto })
  async updateChronicDisease(
    @Param('id') id: number,
    @Param('chronicDiseaseId') chronicDiseaseId: number,
    @Body() data: UpdateChronicDiseaseDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.updateRelation<ChronicDisease>(
        patient,
        'chronicDiseases',
        chronicDiseaseId,
        data as ChronicDisease,
      );
    return new PatientResponseDto(updatedPatient);
  }

  @Delete(':id/chronic-diseases/:chronicDiseaseId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'chronicDiseaseId',
    description: 'Chronic Disease ID',
    type: Number,
  })
  @ApiOkResponse({
    description: 'Removed chronic disease',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  async removeChronicDisease(
    @Param('id') id: number,
    @Param('chronicDiseaseId') chronicDiseaseId: number,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.removeRelation<ChronicDisease>(
        patient,
        'chronicDiseases',
        chronicDiseaseId,
      );
    return new PatientResponseDto(updatedPatient);
  }
  @Post(':id/family-histories')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({
    description: 'Added family history',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: FamilyHistoryDto })
  async addFamilyHistory(
    @Param('id') id: number,
    @Body() data: FamilyHistoryDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.addRelation<FamilyHistory>(
        patient,
        'familyHistories',
        data as FamilyHistory,
      );
    return new PatientResponseDto(updatedPatient);
  }

  @Patch(':id/family-histories/:familyHistoryId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'familyHistoryId',
    description: 'Family History ID',
    type: Number,
  })
  @ApiOkResponse({
    description: 'Updated family history',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateFamilyHistoryDto })
  async updateFamilyHistory(
    @Param('id') id: number,
    @Param('familyHistoryId') familyHistoryId: number,
    @Body() data: UpdateFamilyHistoryDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.updateRelation<FamilyHistory>(
        patient,
        'familyHistories',
        familyHistoryId,
        data as FamilyHistory,
      );
    return new PatientResponseDto(updatedPatient);
  }

  @Delete(':id/family-histories/:familyHistoryId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'familyHistoryId',
    description: 'Family History ID',
    type: Number,
  })
  @ApiOkResponse({
    description: 'Removed family history',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  async removeFamilyHistory(
    @Param('id') id: number,
    @Param('familyHistoryId') familyHistoryId: number,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.removeRelation<FamilyHistory>(
        patient,
        'familyHistories',
        familyHistoryId,
      );
    return new PatientResponseDto(updatedPatient);
  }
  @Post(':id/surgeries')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({
    description: 'Added surgery',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: SurgeryDto })
  async addSurgery(
    @Param('id') id: number,
    @Body() data: SurgeryDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient = await this.patientsService.addRelation<Surgery>(
      patient,
      'surgeries',
      data as Surgery,
    );
    return new PatientResponseDto(updatedPatient);
  }

  @Patch(':id/surgeries/:surgeryId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'surgeryId', description: 'Surgery ID', type: Number })
  @ApiOkResponse({ description: 'Updated surgery', type: PatientResponseDto })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateSurgeryDto })
  async updateSurgery(
    @Param('id') id: number,
    @Param('surgeryId') surgeryId: number,
    @Body() data: UpdateSurgeryDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient = await this.patientsService.updateRelation<Surgery>(
      patient,
      'surgeries',
      surgeryId,
      data as Surgery,
    );
    return new PatientResponseDto(updatedPatient);
  }

  @Delete(':id/surgeries/:surgeryId')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'surgeryId', description: 'Surgery ID', type: Number })
  @ApiOkResponse({ description: 'Removed surgery', type: PatientResponseDto })
  @ApiPatientGeneral()
  async removeSurgery(
    @Param('id') id: number,
    @Param('surgeryId') surgeryId: number,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient = await this.patientsService.removeRelation<Surgery>(
      patient,
      'surgeries',
      surgeryId,
    );
    return new PatientResponseDto(updatedPatient);
  }
  @Post(':id/emergency-contacts')
  @UseGuards(SameIdGuard)
  @ApiCreatedResponse({
    description: 'Added Emergency Contact',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: EmergencyContactDto })
  async addEmergencyContact(
    @Param('id') id: number,
    @Body() data: EmergencyContactDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.addRelation<EmergencyContact>(
        patient,
        'emergencyContacts',
        data as EmergencyContact,
      );
    return new PatientResponseDto(updatedPatient);
  }

  @Patch(':id/emergency-contacts/:emergencyContactId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'emergencyContactId',
    description: 'Emergency Contact ID',
    type: Number,
  })
  @ApiOkResponse({
    description: 'Updated emergency contact',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  @ApiBody({ type: UpdateEmergencyContactDto })
  async updateEmergencyContact(
    @Param('id') id: number,
    @Param('emergencyContactId') emergencyContactId: number,
    @Body() data: UpdateEmergencyContactDto,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.updateRelation<EmergencyContact>(
        patient,
        'emergencyContacts',
        emergencyContactId,
        data as EmergencyContact,
      );
    return new PatientResponseDto(updatedPatient);
  }

  @Delete(':id/emergency-contacts/:emergencyContactId')
  @UseGuards(SameIdGuard)
  @ApiParam({
    name: 'emergencyContactId',
    description: 'Emergency Contact ID',
    type: Number,
  })
  @ApiOkResponse({
    description: 'Removed emergency contact',
    type: PatientResponseDto,
  })
  @ApiPatientGeneral()
  async removeEmergencyContact(
    @Param('id') id: number,
    @Param('emergencyContactId') emergencyContactId: number,
  ): Promise<PatientResponseDto> {
    const patient = (await this.patientsService.findOne(id)) as Patient;
    const updatedPatient =
      await this.patientsService.removeRelation<EmergencyContact>(
        patient,
        'emergencyContacts',
        emergencyContactId,
      );
    return new PatientResponseDto(updatedPatient);
  }
}
