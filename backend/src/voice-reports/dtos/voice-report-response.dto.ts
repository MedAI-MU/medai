import { ApiProperty } from '@nestjs/swagger';
import type { VoiceReportStatus } from '../types/voice-report-status.type';

export class VoiceReportResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty({ enum: ['queued', 'processing', 'completed', 'failed'] })
  status: VoiceReportStatus;

  @ApiProperty()
  patientUserId: number;

  @ApiProperty()
  doctorUserId: number;

  @ApiProperty({ nullable: true })
  appointmentId: number | null;

  @ApiProperty()
  createdAt: Date;

  constructor(report: {
    id: number;
    status: VoiceReportStatus;
    patientUserId: number;
    doctorUserId: number;
    appointmentId: number | null;
    createdAt: Date;
  }) {
    this.id = report.id;
    this.status = report.status;
    this.patientUserId = report.patientUserId;
    this.doctorUserId = report.doctorUserId;
    this.appointmentId = report.appointmentId;
    this.createdAt = report.createdAt;
  }
}
