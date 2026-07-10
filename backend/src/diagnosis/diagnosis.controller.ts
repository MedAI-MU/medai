import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
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
import { DiagnosisService } from './diagnosis.service';
import { CreateDiagnosisDto } from './dtos/create-diagnosis.dto';
import { UpdateDiagnosisDto } from './dtos/update-diagnosis.dto';
import { UpdateSymptomsDto } from './dtos/update-symptoms.dto';
import { DiagnosisResponseDto } from './dtos/diagnosis-response.dto';
import { DoctorAssignedGuard } from '../shared/guards/doctor-assigned.guard';
import { SameIdGuard } from '../shared/guards/same-id.guard';
import { PatientResourceAccessGuard } from '../shared/guards/patient-resource-access.guard';
import { DoctorResourceOwnerGuard } from '../shared/guards/doctor-resource-owner.guard';
import { PatientResource } from '../shared/decorators/patient-resource.decorator';
import { DoctorResource } from '../shared/decorators/doctor-resource.decorator';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { Diagnosis } from './entities/diagnosis.entity';

@Controller('diagnosis')
@UseGuards(ApprovedGuard)
export class DiagnosisController {
  constructor(private readonly diagnosisService: DiagnosisService) {}

  @UseGuards(RolesGuard, DoctorAssignedGuard)
  @Roles('doctor')
  @Post(':id')
  @HttpCode(HttpStatus.CREATED)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiBody({ type: CreateDiagnosisDto })
  @ApiCreatedResponse({
    description: 'Diagnosis created',
    type: DiagnosisResponseDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid input or duplicate' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async create(
    @CurrentUser() currentUser: TokenUser,
    @Body() dto: CreateDiagnosisDto,
    @Param('id', ParseIntPipe) patientUserId: number,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.create(
      dto,
      currentUser.id,
      patientUserId,
    );
    return new DiagnosisResponseDto(diagnosis);
  }

  @Roles('secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get()
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'List of diagnoses',
    type: [DiagnosisResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findAll(
    @CurrentUser() currentUser: TokenUser,
  ): Promise<DiagnosisResponseDto[]> {
    const diagnoses = await this.diagnosisService.findAll(currentUser);
    return diagnoses.map((d) => new DiagnosisResponseDto(d));
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard)
  @Roles('secretary', 'doctor')
  @Get(':id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiOkResponse({
    description: "Patient's diagnoses",
    type: [DiagnosisResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findPatientDiagnoses(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) patientUserId: number,
  ): Promise<DiagnosisResponseDto[]> {
    const diagnoses = await this.diagnosisService.findByPatient(
      patientUserId,
      currentUser,
    );
    return diagnoses.map((d) => new DiagnosisResponseDto(d));
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    patientUserId: 'patientUserId',
  })
  @Roles('doctor')
  @Get(':id/:diagnosisId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'diagnosisId', type: Number })
  @ApiOkResponse({
    description: 'Diagnosis details',
    type: DiagnosisResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findOne(
    @Param('diagnosisId', ParseIntPipe) diagnosisId: number,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.findOne(diagnosisId);
    return new DiagnosisResponseDto(diagnosis);
  }

  @UseGuards(
    RolesGuard,
    DoctorAssignedGuard,
    DoctorResourceOwnerGuard,
    PatientResourceAccessGuard,
  )
  @PatientResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    patientUserId: 'patientUserId',
  })
  @DoctorResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    doctorUserId: 'doctorUserId',
  })
  @Roles('doctor')
  @Patch(':id/:diagnosisId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'diagnosisId', type: Number })
  @ApiBody({ type: UpdateDiagnosisDto })
  @ApiOkResponse({
    description: 'Diagnosis updated',
    type: DiagnosisResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async update(
    @Param('diagnosisId', ParseIntPipe) diagnosisId: number,
    @Body() dto: UpdateDiagnosisDto,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.update(diagnosisId, dto);
    return new DiagnosisResponseDto(diagnosis);
  }

  @UseGuards(
    RolesGuard,
    DoctorAssignedGuard,
    DoctorResourceOwnerGuard,
    PatientResourceAccessGuard,
  )
  @PatientResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    patientUserId: 'patientUserId',
  })
  @DoctorResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    doctorUserId: 'doctorUserId',
  })
  @Roles('doctor')
  @Patch(':id/:diagnosisId/symptoms')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'diagnosisId', type: Number })
  @ApiBody({ type: UpdateSymptomsDto })
  @ApiOkResponse({
    description: 'Symptoms updated',
    type: DiagnosisResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async updateSymptoms(
    @Param('diagnosisId', ParseIntPipe) diagnosisId: number,
    @Body() dto: UpdateSymptomsDto,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.updateSymptoms(
      diagnosisId,
      dto,
    );
    return new DiagnosisResponseDto(diagnosis);
  }

  @UseGuards(
    RolesGuard,
    DoctorAssignedGuard,
    DoctorResourceOwnerGuard,
    PatientResourceAccessGuard,
  )
  @PatientResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    patientUserId: 'patientUserId',
  })
  @DoctorResource({
    entity: Diagnosis,
    resourceIdParam: 'diagnosisId',
    doctorUserId: 'doctorUserId',
  })
  @Roles('doctor')
  @Delete(':id/:diagnosisId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'diagnosisId', type: Number })
  @ApiNoContentResponse({ description: 'Diagnosis deleted' })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async delete(
    @Param('diagnosisId', ParseIntPipe) diagnosisId: number,
  ): Promise<void> {
    await this.diagnosisService.delete(diagnosisId);
  }
}
