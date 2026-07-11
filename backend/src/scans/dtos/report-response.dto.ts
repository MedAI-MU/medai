import { ApiProperty } from '@nestjs/swagger';
import type { ReportAnalysisStatus } from '../../report-analysis/types/report-analysis-status.type';

export class ReportResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  patientUserId: number;

  @ApiProperty({ nullable: true })
  scanId: number | null;

  @ApiProperty({ nullable: true })
  appointmentId: number | null;

  @ApiProperty()
  path: string;

  @ApiProperty({
    enum: ['queued', 'processing', 'completed', 'failed'],
    required: false,
    nullable: true,
  })
  analysisStatus: ReportAnalysisStatus | null;

  @ApiProperty({ type: Object, required: false, nullable: true })
  analysisResult: Record<string, unknown> | null;

  @ApiProperty({ required: false, nullable: true })
  analysisError: string | null;

  @ApiProperty()
  createdAt: Date;

  constructor(report: {
    id: number;
    patientUserId: number;
    scanId: number | null;
    appointmentId: number | null;
    path: string;
    analysisStatus: ReportAnalysisStatus | null;
    analysisResult: Record<string, unknown> | null;
    analysisError: string | null;
    createdAt: Date;
  }) {
    this.id = report.id;
    this.patientUserId = report.patientUserId;
    this.scanId = report.scanId;
    this.appointmentId = report.appointmentId;
    this.path = report.path;
    this.analysisStatus = report.analysisStatus;
    this.analysisResult = report.analysisResult;
    this.analysisError = report.analysisError;
    this.createdAt = report.createdAt;
  }
}
