import { Controller, Get, Post, Param, Body, ParseIntPipe, UseGuards } from '@nestjs/common';
import { ApiTags, ApiCreatedResponse, ApiOkResponse, ApiParam } from '@nestjs/swagger';
import { MedicalReportsService } from './medical-reports.service';
import { CreateMedicalReportDto } from './dtos/create-medical-report.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';
import { RolesGuard } from '../auth/guards/roles.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { SameIdGuard } from '../shared/guards/same-id.guard';

@ApiTags('Medical Reports')
@Controller('patients/:id/reports')
@UseGuards(JwtAuthGuard, RolesGuard)
export class MedicalReportsController {
  constructor(private readonly medicalReportsService: MedicalReportsService) {}

  @Post()
  @Roles('doctor') // Allow doctors to generate reports for patients
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'id', type: Number })
  @ApiCreatedResponse({ description: 'The generated medical report' })
  async createReport(
    @Param('id', ParseIntPipe) id: number,
    @Body() dto: CreateMedicalReportDto,
  ) {
    return await this.medicalReportsService.create(id, dto);
  }

  @Get()
  @Roles('doctor', 'secretary') // Only secretaries and doctors bypass SameIdGuard
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ description: 'List of medical reports for a patient' })
  async getReports(@Param('id', ParseIntPipe) id: number) {
    return await this.medicalReportsService.findByPatientId(id);
  }

  @Get(':reportId')
  @Roles('doctor', 'secretary')
  @UseGuards(SameIdGuard)
  @ApiParam({ name: 'id', type: Number })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiOkResponse({ description: 'Medical report details' })
  async getReportDetails(
    @Param('reportId', ParseIntPipe) reportId: number,
  ) {
    return await this.medicalReportsService.findOne(reportId);
  }
}
