import {
  Body,
  Controller,
  FileTypeValidator,
  Get,
  HttpCode,
  HttpStatus,
  MaxFileSizeValidator,
  Param,
  ParseFilePipe,
  ParseIntPipe,
  Post,
  UploadedFile,
  UseGuards,
  UseInterceptors,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import {
  ApiBody,
  ApiConsumes,
  ApiConflictResponse,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { ReportAnalysisService } from './report-analysis.service';
import { ReportResponseDto } from '../scans/dtos/report-response.dto';
import { SameIdGuard } from '../shared/guards/same-id.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller()
@UseGuards(ApprovedGuard)
export class ReportsController {
  constructor(private readonly reportAnalysisService: ReportAnalysisService) {}

  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @Post('reports/:id')
  @HttpCode(HttpStatus.CREATED)
  @UseInterceptors(FileInterceptor('file'))
  @ApiConsumes('multipart/form-data')
  @ApiParam({ name: 'id', description: 'Patient ID', type: Number })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        file: { type: 'string', format: 'binary' },
        appointmentId: { type: 'number' },
      },
    },
  })
  @ApiCreatedResponse({
    description: 'Report created',
    type: ReportResponseDto,
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async createReport(
    @UploadedFile(
      new ParseFilePipe({
        validators: [
          new MaxFileSizeValidator({ maxSize: 10 * 1024 * 1024 }),
          new FileTypeValidator({
            fileType: /(image\/(jpeg|png|webp)|application\/pdf)/,
          }),
        ],
      }),
    )
    file: Express.Multer.File,
    @Param('id', ParseIntPipe) patientUserId: number,
    @Body('appointmentId') appointmentId?: string,
  ): Promise<ReportResponseDto> {
    const aId = appointmentId ? Number(appointmentId) : null;
    const report = await this.reportAnalysisService.createReport(
      patientUserId,
      aId,
      file,
    );
    return new ReportResponseDto(report);
  }

  @UseGuards(RolesGuard)
  @Roles('secretary', 'patient')
  @Post('report/:id/analyze')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Report ID', type: Number })
  @ApiOkResponse({
    description: 'Analysis job queued (poll GET report/:id/analyze for status)',
    type: ReportResponseDto,
  })
  @ApiConflictResponse({ description: 'Analysis already in progress' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  async triggerAnalysis(
    @Param('id', ParseIntPipe) reportId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto> {
    const report = await this.reportAnalysisService.triggerAnalysis(
      reportId,
      currentUser,
    );
    return new ReportResponseDto(report);
  }

  @UseGuards(RolesGuard)
  @Roles('secretary', 'patient')
  @Get('report/:id/analyze')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Report ID', type: Number })
  @ApiOkResponse({
    description: 'Report analysis status (poll this endpoint)',
    type: ReportResponseDto,
  })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  async getAnalysis(
    @Param('id', ParseIntPipe) reportId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto> {
    const report = await this.reportAnalysisService.getAnalysis(
      reportId,
      currentUser,
    );
    return new ReportResponseDto(report);
  }

  @UseGuards(RolesGuard)
  @Roles('secretary', 'doctor', 'patient')
  @Get('reports')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Reports list',
    type: [ReportResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findAll(
    @CurrentUser() currentUser: TokenUser,
  ): Promise<ReportResponseDto[]> {
    const reports =
      await this.reportAnalysisService.findAllForUser(currentUser);
    return reports.map((r) => new ReportResponseDto(r));
  }
}
