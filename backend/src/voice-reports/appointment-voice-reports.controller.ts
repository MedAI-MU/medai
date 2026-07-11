import {
  Body,
  Controller,
  Delete,
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
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNoContentResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { VoiceReportsService } from './voice-reports.service';
import { VoiceReportResponseDto } from './dtos/voice-report-response.dto';
import { VoiceReportStatusDto } from './dtos/voice-report-status.dto';
import { AppointmentAccessGuard } from '../shared/guards/appointment-access.guard';
import { Roles } from '../auth/decorators/roles.decorator';
import { RolesGuard } from '../auth/guards/roles.guard';
import { ApprovedGuard } from '../users/guards/approved.guard';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller('appointments')
@UseGuards(ApprovedGuard)
export class AppointmentVoiceReportsController {
  constructor(private readonly voiceReportsService: VoiceReportsService) {}

  @UseGuards(AppointmentAccessGuard, RolesGuard)
  @Roles('doctor', 'secretary')
  @Post(':appointmentId/voice-reports')
  @HttpCode(HttpStatus.CREATED)
  @UseInterceptors(
    FileInterceptor('audio', { limits: { fileSize: 50 * 1024 * 1024 } }),
  )
  @ApiConsumes('multipart/form-data')
  @ApiParam({
    name: 'appointmentId',
    description: 'Appointment ID',
    type: Number,
  })
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        audio: { type: 'string', format: 'binary' },
      },
    },
  })
  @ApiCreatedResponse({
    description: 'Voice report job created for the appointment',
    type: VoiceReportResponseDto,
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiNotFoundResponse({ description: 'Appointment not found' })
  async create(
    @UploadedFile(
      new ParseFilePipe({
        validators: [new MaxFileSizeValidator({ maxSize: 50 * 1024 * 1024 })],
      }),
    )
    file: Express.Multer.File,
    @Param('appointmentId', ParseIntPipe) appointmentId: number,
    @CurrentUser() currentUser: TokenUser,
  ): Promise<VoiceReportResponseDto> {
    const report = await this.voiceReportsService.createForAppointment(
      appointmentId,
      file,
      currentUser,
    );
    return new VoiceReportResponseDto(report);
  }

  @UseGuards(AppointmentAccessGuard, RolesGuard)
  @Roles('doctor', 'secretary')
  @Get(':appointmentId/voice-reports')
  @HttpCode(HttpStatus.OK)
  @ApiParam({
    name: 'appointmentId',
    description: 'Appointment ID',
    type: Number,
  })
  @ApiOkResponse({
    description: "Appointment's voice reports",
    type: [VoiceReportResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiNotFoundResponse({ description: 'Appointment not found' })
  async findByAppointment(
    @Param('appointmentId', ParseIntPipe) appointmentId: number,
  ): Promise<VoiceReportResponseDto[]> {
    const reports =
      await this.voiceReportsService.findByAppointment(appointmentId);
    return reports.map((r) => new VoiceReportResponseDto(r));
  }

  @UseGuards(AppointmentAccessGuard, RolesGuard)
  @Roles('doctor', 'secretary')
  @Get(':appointmentId/voice-reports/:reportId')
  @HttpCode(HttpStatus.OK)
  @ApiParam({
    name: 'appointmentId',
    description: 'Appointment ID',
    type: Number,
  })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiOkResponse({
    description: 'Voice report status (poll this endpoint)',
    type: VoiceReportStatusDto,
  })
  @ApiNotFoundResponse({ description: 'Voice report or appointment not found' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  async getStatus(
    @Param('appointmentId', ParseIntPipe) appointmentId: number,
    @Param('reportId', ParseIntPipe) reportId: number,
  ): Promise<VoiceReportStatusDto> {
    const report = await this.voiceReportsService.findOneByAppointment(
      appointmentId,
      reportId,
    );
    return new VoiceReportStatusDto({
      id: report.id,
      status: report.status,
      patientUserId: report.patientUserId,
      doctorUserId: report.doctorUserId,
      appointmentId: report.appointmentId,
      clinicalReport: report.clinicalReport,
      transcription: report.transcription,
      audioUrl: report.audioUrl,
      errorMessage: report.errorMessage,
      createdAt: report.createdAt,
      updatedAt: report.updatedAt,
    });
  }

  @UseGuards(AppointmentAccessGuard, RolesGuard)
  @Roles('doctor', 'secretary')
  @Delete(':appointmentId/voice-reports/:reportId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({
    name: 'appointmentId',
    description: 'Appointment ID',
    type: Number,
  })
  @ApiParam({ name: 'reportId', type: Number })
  @ApiNoContentResponse({ description: 'Voice report deleted successfully' })
  @ApiNotFoundResponse({ description: 'Voice report or appointment not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async remove(
    @Param('appointmentId', ParseIntPipe) appointmentId: number,
    @Param('reportId', ParseIntPipe) reportId: number,
  ): Promise<void> {
    const report = await this.voiceReportsService.findOneByAppointment(
      appointmentId,
      reportId,
    );
    await this.voiceReportsService.remove(report.id);
  }
}
