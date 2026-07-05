import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  NotFoundException,
  Param,
  ParseIntPipe,
  Post,
  Res,
  UploadedFile,
  UploadedFiles,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FilesInterceptor, FileInterceptor } from '@nestjs/platform-express';
import type { Response } from 'express';
import * as fs from 'fs';
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
import { SameIdGuard } from '../shared/guards/same-id.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

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
    @CurrentUser() currentUser: TokenUser,
    @UploadedFiles() images: Express.Multer.File[],
    @Param('id', ParseIntPipe) patientUserId: number,
    @Body('appointmentId') appointmentId?: string,
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

  @Roles('patient', 'secretary', 'doctor')
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

  @UseGuards(SameIdGuard)
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
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ScanResponseDto> {
    const scan = await this.scansService.findOne(scanId, currentUser);
    return new ScanResponseDto(scan);
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @Delete('scans/:id/:scanId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'scanId', type: Number })
  @ApiNoContentResponse({ description: 'Scan deleted' })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async delete(
    @Param('scanId', ParseIntPipe) scanId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<void> {
    await this.scansService.delete(scanId, currentUser);
  }

  @UseGuards(SameIdGuard)
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
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto[]> {
    const reports = await this.scansService.findReports(scanId, currentUser);
    return reports.map((r) => new ReportResponseDto(r));
  }

  @UseGuards(SameIdGuard)
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
    @CurrentUser() currentUser: TokenUser,
    @UploadedFile() file: Express.Multer.File,
    @Param('id', ParseIntPipe) patientUserId: number,
  ): Promise<ReportResponseDto> {
    const report = await this.scansService.createReport(
      scanId,
      patientUserId,
      file,
      currentUser,
    );
    return new ReportResponseDto(report);
  }

  @UseGuards(SameIdGuard)
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
    @CurrentUser() currentUser: TokenUser,
  ): Promise<void> {
    await this.scansService.deleteReport(reportId, currentUser);
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary', 'doctor')
  @Get('reports/:id/:reportId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiOkResponse({ description: 'Report details', type: ReportResponseDto })
  @ApiNotFoundResponse({ description: 'Report not found' })
  async findReport(
    @Param('reportId', ParseIntPipe) reportId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto> {
    const report = await this.scansService.findReportById(
      reportId,
      currentUser,
    );
    return new ReportResponseDto(report);
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary', 'doctor')
  @Get('reports/patient/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiOkResponse({
    description: "Patient's reports",
    type: [ReportResponseDto],
  })
  async findReportsByPatient(
    @Param('id', ParseIntPipe) patientUserId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto[]> {
    const reports = await this.scansService.findReportsByPatient(
      patientUserId,
      currentUser,
    );
    return reports.map((r) => new ReportResponseDto(r));
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary', 'doctor')
  @Get('scans/images/:id/:imageId/file')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'imageId', type: Number })
  @ApiOkResponse({ description: 'Image file' })
  @ApiNotFoundResponse({ description: 'Image not found' })
  async getImageFile(
    @Param('imageId', ParseIntPipe) imageId: number,
    @CurrentUser() currentUser: TokenUser,
    @Res() res: Response,
  ): Promise<void> {
    const { path, originalName } = await this.scansService.getImageFile(
      imageId,
      currentUser,
    );
    if (!fs.existsSync(path)) {
      throw new NotFoundException('File not found on disk');
    }
    res.setHeader('Content-Disposition', `inline; filename="${originalName}"`);
    const stream = fs.createReadStream(path);
    stream.pipe(res);
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary', 'doctor')
  @Get('reports/:id/:reportId/file')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiOkResponse({ description: 'Report file' })
  @ApiNotFoundResponse({ description: 'Report not found' })
  async getReportFile(
    @Param('reportId', ParseIntPipe) reportId: number,
    @CurrentUser() currentUser: TokenUser,
    @Res() res: Response,
  ): Promise<void> {
    const { path, originalName } = await this.scansService.getReportFile(
      reportId,
      currentUser,
    );
    if (!fs.existsSync(path)) {
      throw new NotFoundException('File not found on disk');
    }
    res.setHeader('Content-Disposition', `inline; filename="${originalName}"`);
    const stream = fs.createReadStream(path);
    stream.pipe(res);
  }
}
