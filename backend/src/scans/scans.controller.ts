import {
  Body,
  Controller,
  Delete,
  FileTypeValidator,
  Get,
  HttpCode,
  HttpStatus,
  MaxFileSizeValidator,
  Param,
  ParseFilePipe,
  ParseIntPipe,
  Post,
  Redirect,
  Res,
  UploadedFile,
  UploadedFiles,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FilesInterceptor, FileInterceptor } from '@nestjs/platform-express';
import type { Response } from 'express';
import {
  ApiBody,
  ApiConsumes,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNoContentResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { ScansService } from './scans.service';
import { ScanResponseDto } from './dtos/scan-response.dto';
import { ReportResponseDto } from './dtos/report-response.dto';
import { DoctorAssignedGuard } from '../shared/guards/doctor-assigned.guard';
import { SameIdGuard } from '../shared/guards/same-id.guard';
import { PatientResourceAccessGuard } from '../shared/guards/patient-resource-access.guard';
import { PatientResource } from '../shared/decorators/patient-resource.decorator';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { Scan } from './entities/scan.entity';
import { ScanImage } from './entities/scan-image.entity';
import { Report } from './entities/report.entity';

@Controller()
@UseGuards(ApprovedGuard)
export class ScansController {
  constructor(private readonly scansService: ScansService) {}

  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @Post('scans/:id')
  @HttpCode(HttpStatus.CREATED)
  @UseInterceptors(FilesInterceptor('images'))
  @ApiConsumes('multipart/form-data')
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        images: { type: 'array', items: { type: 'string', format: 'binary' } },
        appointmentId: { type: 'number' },
      },
    },
  })
  @ApiCreatedResponse({ description: 'Scan created', type: ScanResponseDto })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async create(
    @UploadedFiles(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: 10 * 1024 * 1024 }),
          new FileTypeValidator({ fileType: /image\/(jpeg|png|gif|webp)/ }),
        ],
        fileIsRequired: false,
      }),
    )
    images: Express.Multer.File[],
    @Param('id', ParseIntPipe) patientUserId: number,
    @Body('appointmentId') appointmentId?: string,
    @CurrentUser() currentUser?: TokenUser,
  ): Promise<ScanResponseDto> {
    const aId = appointmentId ? Number(appointmentId) : null;
    const scan = await this.scansService.create(
      patientUserId,
      aId,
      images || [],
      currentUser,
    );
    return new ScanResponseDto(scan);
  }

  @Roles('secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('scans')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'List of scans', type: [ScanResponseDto] })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findAll(
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ScanResponseDto[]> {
    const scans = await this.scansService.findAll(currentUser);
    return scans.map((s) => new ScanResponseDto(s));
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard)
  @Roles('secretary', 'doctor')
  @Get('scans/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiOkResponse({ description: "Patient's scans", type: [ScanResponseDto] })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findPatientScans(
    @Param('id', ParseIntPipe) patientUserId: number,
  ): Promise<ScanResponseDto[]> {
    const scans = await this.scansService.findByPatient(patientUserId);
    return scans.map((s) => new ScanResponseDto(s));
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Scan,
    resourceIdParam: 'scanId',
    patientUserId: 'patientUserId',
    relations: ['images', 'reports'],
  })
  @Roles('secretary', 'doctor')
  @Get('scans/:id/:scanId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'scanId', type: Number })
  @ApiOkResponse({ description: 'Scan details', type: ScanResponseDto })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findOne(
    @Param('scanId', ParseIntPipe) scanId: number,
  ): Promise<ScanResponseDto> {
    const scan = await this.scansService.findOne(scanId);
    return new ScanResponseDto(scan);
  }

  @UseGuards(SameIdGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Scan,
    resourceIdParam: 'scanId',
    patientUserId: 'patientUserId',
    relations: ['images'],
  })
  @Roles('secretary')
  @Delete('scans/:id/:scanId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'scanId', type: Number })
  @ApiNoContentResponse({ description: 'Scan deleted' })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async delete(@Param('scanId', ParseIntPipe) scanId: number): Promise<void> {
    await this.scansService.delete(scanId);
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Scan,
    resourceIdParam: 'scanId',
    patientUserId: 'patientUserId',
  })
  @Roles('secretary', 'doctor')
  @Get('scans/:id/:scanId/reports')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'scanId', type: Number })
  @ApiOkResponse({
    description: 'Reports for scan',
    type: [ReportResponseDto],
  })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  async findReports(
    @Param('scanId', ParseIntPipe) scanId: number,
  ): Promise<ReportResponseDto[]> {
    const reports = await this.scansService.findReports(scanId);
    return reports.map((r) => new ReportResponseDto(r));
  }

  @UseGuards(SameIdGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Scan,
    resourceIdParam: 'scanId',
    patientUserId: 'patientUserId',
  })
  @Roles('secretary')
  @Post('scans/:id/:scanId/reports')
  @HttpCode(HttpStatus.CREATED)
  @UseInterceptors(FileInterceptor('file'))
  @ApiConsumes('multipart/form-data')
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'scanId', type: Number })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        file: { type: 'string', format: 'binary' },
      },
    },
  })
  @ApiCreatedResponse({
    description: 'Report created',
    type: ReportResponseDto,
  })
  async createReport(
    @Param('scanId', ParseIntPipe) scanId: number,
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: 10 * 1024 * 1024 }),
          new FileTypeValidator({
            fileType: /(image\/(jpeg|png|gif|webp)|application\/pdf)/,
          }),
        ],
      }),
    )
    file: Express.Multer.File,
    @Param('id', ParseIntPipe) patientUserId: number,
  ): Promise<ReportResponseDto> {
    const report = await this.scansService.createReport(
      scanId,
      patientUserId,
      file,
    );
    return new ReportResponseDto(report);
  }

  @UseGuards(SameIdGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Report,
    resourceIdParam: 'reportId',
    patientUserId: 'patientUserId',
  })
  @Roles('secretary')
  @Delete('reports/:id/:reportId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiNoContentResponse({ description: 'Report deleted' })
  @ApiNotFoundResponse({ description: 'Report not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async deleteReport(
    @Param('reportId', ParseIntPipe) reportId: number,
  ): Promise<void> {
    await this.scansService.deleteReport(reportId);
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Report,
    resourceIdParam: 'reportId',
    patientUserId: 'patientUserId',
  })
  @Roles('secretary', 'doctor')
  @Get('reports/:id/:reportId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiOkResponse({ description: 'Report details', type: ReportResponseDto })
  @ApiNotFoundResponse({ description: 'Report not found' })
  async findReport(
    @Param('reportId', ParseIntPipe) reportId: number,
  ): Promise<ReportResponseDto> {
    const report = await this.scansService.findReportById(reportId);
    return new ReportResponseDto(report);
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard)
  @Roles('secretary', 'doctor')
  @Get('reports/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiOkResponse({
    description: "Patient's reports",
    type: [ReportResponseDto],
  })
  async findReportsByPatient(
    @Param('id', ParseIntPipe) patientUserId: number,
  ): Promise<ReportResponseDto[]> {
    const reports = await this.scansService.findReportsByPatient(patientUserId);
    return reports.map((r) => new ReportResponseDto(r));
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: ScanImage,
    resourceIdParam: 'imageId',
    patientUserId: 'scan.patientUserId',
    relations: ['scan'],
  })
  @Roles('secretary', 'doctor')
  @Get('scans/images/:id/:imageId/file')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'imageId', type: Number })
  @ApiOkResponse({ description: 'Image file' })
  @ApiNotFoundResponse({ description: 'Image not found' })
  @Redirect()
  async getImageFile(
    @Param('imageId', ParseIntPipe) imageId: number,
    @Res({ passthrough: true }) res: Response,
  ): Promise<{ url: string; statusCode: number }> {
    const { path: blobUrl } = await this.scansService.getImageFile(imageId);
    res.setHeader('Cache-Control', 'private, max-age=3600');
    return { url: blobUrl, statusCode: 302 };
  }

  @UseGuards(SameIdGuard, DoctorAssignedGuard, PatientResourceAccessGuard)
  @PatientResource({
    entity: Report,
    resourceIdParam: 'reportId',
    patientUserId: 'patientUserId',
  })
  @Roles('secretary', 'doctor')
  @Get('reports/:id/:reportId/file')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiOkResponse({ description: 'Report file' })
  @ApiNotFoundResponse({ description: 'Report not found' })
  @Redirect()
  async getReportFile(
    @Param('reportId', ParseIntPipe) reportId: number,
    @Res({ passthrough: true }) res: Response,
  ): Promise<{ url: string; statusCode: number }> {
    const { path: blobUrl } = await this.scansService.getReportFile(reportId);
    res.setHeader('Cache-Control', 'private, max-age=3600');
    return { url: blobUrl, statusCode: 302 };
  }
}
