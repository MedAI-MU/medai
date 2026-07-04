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
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller('diagnoses')
@UseGuards(ApprovedGuard)
export class DiagnosisController {
  constructor(private readonly diagnosisService: DiagnosisService) {}

  @Roles('doctor')
  @UseGuards(RolesGuard)
  @Post()
  @HttpCode(HttpStatus.CREATED)
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
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.create(dto, currentUser.id);
    return new DiagnosisResponseDto(diagnosis);
  }

  @Roles('patient', 'doctor')
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

  @Roles('patient', 'doctor')
  @UseGuards(RolesGuard)
  @Get(':id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({
    description: 'Diagnosis details',
    type: DiagnosisResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findOne(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.findOne(id, currentUser);
    return new DiagnosisResponseDto(diagnosis);
  }

  @Roles('doctor')
  @UseGuards(RolesGuard)
  @Patch(':id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiBody({ type: UpdateDiagnosisDto })
  @ApiOkResponse({
    description: 'Diagnosis updated',
    type: DiagnosisResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async update(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: UpdateDiagnosisDto,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.update(id, dto, currentUser);
    return new DiagnosisResponseDto(diagnosis);
  }

  @Roles('patient', 'doctor')
  @UseGuards(RolesGuard)
  @Patch(':id/symptoms')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiBody({ type: UpdateSymptomsDto })
  @ApiOkResponse({
    description: 'Symptoms updated',
    type: DiagnosisResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async updateSymptoms(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: UpdateSymptomsDto,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<DiagnosisResponseDto> {
    const diagnosis = await this.diagnosisService.updateSymptoms(
      id,
      dto,
      currentUser,
    );
    return new DiagnosisResponseDto(diagnosis);
  }

  @Roles('doctor')
  @UseGuards(RolesGuard)
  @Delete(':id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', type: Number })
  @ApiNoContentResponse({ description: 'Diagnosis deleted' })
  @ApiNotFoundResponse({ description: 'Diagnosis not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async delete(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<void> {
    await this.diagnosisService.delete(id, currentUser);
  }
}
