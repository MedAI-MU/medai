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
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller()
@UseGuards(ApprovedGuard)
export class ScansController {
  constructor(private readonly scansService: ScansService) {}

  @Roles('patient', 'secretary')
  @UseGuards(RolesGuard)
  @Post('scans')
  @HttpCode(HttpStatus.CREATED)
  @UseInterceptors(FilesInterceptor('images'))
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        images: { type: 'array', items: { type: 'string', format: 'binary' } },
        patientUserId: { type: 'number' },
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
    @Body('patientUserId') patientUserId?: string,
    @Body('appointmentId') appointmentId?: string,
  ): Promise<ScanResponseDto> {
    const pUserId =
      currentUser.role === 'secretary' && patientUserId
        ? Number(patientUserId)
        : currentUser.id;
    const aId = appointmentId ? Number(appointmentId) : null;
    const scan = await this.scansService.create(
      pUserId,
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

  @Roles('patient', 'secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('scans/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ description: 'Scan details', type: ScanResponseDto })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findOne(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ScanResponseDto> {
    const scan = await this.scansService.findOne(id, currentUser);
    return new ScanResponseDto(scan);
  }

  @Roles('patient', 'secretary')
  @UseGuards(RolesGuard)
  @Delete('scans/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', type: Number })
  @ApiNoContentResponse({ description: 'Scan deleted' })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async delete(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<void> {
    await this.scansService.delete(id, currentUser);
  }

  @Roles('patient', 'secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('scans/:id/reports')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({
    description: 'Reports for scan',
    type: [ReportResponseDto],
  })
  @ApiNotFoundResponse({ description: 'Scan not found' })
  async findReports(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto[]> {
    const reports = await this.scansService.findReports(id, currentUser);
    return reports.map((r) => new ReportResponseDto(r));
  }

  @Roles('patient', 'secretary')
  @UseGuards(RolesGuard)
  @Post('scans/:id/reports')
  @HttpCode(HttpStatus.CREATED)
  @UseInterceptors(FileInterceptor('file'))
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        file: { type: 'string', format: 'binary' },
        patientUserId: { type: 'number' },
      },
    },
  })
  @ApiCreatedResponse({
    description: 'Report created',
    type: ReportResponseDto,
  })
  async createReport(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
    @UploadedFile() file: Express.Multer.File,
    @Body('patientUserId') patientUserId?: string,
  ): Promise<ReportResponseDto> {
    const pUserId =
      currentUser.role === 'secretary' && patientUserId
        ? Number(patientUserId)
        : currentUser.id;
    const report = await this.scansService.createReport(
      id,
      pUserId,
      file,
      currentUser,
    );
    return new ReportResponseDto(report);
  }

  @Roles('patient', 'secretary')
  @UseGuards(RolesGuard)
  @Delete('reports/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', type: Number })
  @ApiNoContentResponse({ description: 'Report deleted' })
  @ApiNotFoundResponse({ description: 'Report not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async deleteReport(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<void> {
    await this.scansService.deleteReport(id, currentUser);
  }

  @Roles('patient', 'secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('reports/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ description: 'Report details', type: ReportResponseDto })
  @ApiNotFoundResponse({ description: 'Report not found' })
  async findReport(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto> {
    const report = await this.scansService.findReportById(id, currentUser);
    return new ReportResponseDto(report);
  }

  @Roles('patient', 'secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('reports/patient/:patientUserId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'patientUserId', type: Number })
  @ApiOkResponse({
    description: "Patient's reports",
    type: [ReportResponseDto],
  })
  async findReportsByPatient(
    @Param('patientUserId', ParseIntPipe) patientUserId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto[]> {
    const reports = await this.scansService.findReportsByPatient(
      patientUserId,
      currentUser,
    );
    return reports.map((r) => new ReportResponseDto(r));
  }

  @Roles('patient', 'secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('scans/images/:imageId/file')
  @HttpCode(HttpStatus.OK)
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

  @Roles('patient', 'secretary', 'doctor')
  @UseGuards(RolesGuard)
  @Get('reports/:id/file')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', type: Number })
  @ApiOkResponse({ description: 'Report file' })
  @ApiNotFoundResponse({ description: 'Report not found' })
  async getReportFile(
    @Param('id', ParseIntPipe) id: number,
    @CurrentUser() currentUser: TokenUser,
    @Res() res: Response,
  ): Promise<void> {
    const { path, originalName } = await this.scansService.getReportFile(
      id,
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
