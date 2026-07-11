import { ApiProperty } from '@nestjs/swagger';
import type { VoiceReportStatus } from '../types/voice-report-status.type';

export class VoiceReportStatusDto {
  @ApiProperty()
  id: number;

  @ApiProperty({ enum: ['queued', 'processing', 'completed', 'failed'] })
  status: VoiceReportStatus;

  @ApiProperty({ required: false, nullable: true })
  patientUserId: number;

  @ApiProperty({ required: false, nullable: true })
  doctorUserId: number;

  @ApiProperty({ required: false, nullable: true })
  appointmentId: number | null;

  @ApiProperty({ type: Object, required: false, nullable: true })
  clinicalReport: Record<string, unknown> | null;

  @ApiProperty({ required: false, nullable: true })
  transcription: string | null;

  @ApiProperty({ required: false, nullable: true })
  audioUrl: string | null;

  @ApiProperty({ required: false, nullable: true })
  errorMessage: string | null;

  @ApiProperty()
  createdAt: Date;

  @ApiProperty()
  updatedAt: Date;

  constructor(report: {
    id: number;
    status: VoiceReportStatus;
    patientUserId: number;
    doctorUserId: number;
    appointmentId: number | null;
    clinicalReport: Record<string, unknown> | null;
    transcription: string | null;
    audioUrl: string | null;
    errorMessage: string | null;
    createdAt: Date;
    updatedAt: Date;
  }) {
    this.id = report.id;
    this.status = report.status;
    this.patientUserId = report.patientUserId;
    this.doctorUserId = report.doctorUserId;
    this.appointmentId = report.appointmentId;
    this.clinicalReport = report.clinicalReport;
    this.transcription = report.transcription;
    this.audioUrl = report.audioUrl;
    this.errorMessage = report.errorMessage;
    this.createdAt = report.createdAt;
    this.updatedAt = report.updatedAt;
  }
}
