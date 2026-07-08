import { ApiProperty } from '@nestjs/swagger';

export class ReportResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  patientUserId: number;

  @ApiProperty({ nullable: true })
  scanId: number | null;

  @ApiProperty()
  path: string;

  @ApiProperty()
  createdAt: Date;

  constructor(report: {
    id: number;
    patientUserId: number;
    scanId: number | null;
    path: string;
    createdAt: Date;
  }) {
    this.id = report.id;
    this.patientUserId = report.patientUserId;
    this.scanId = report.scanId;
    this.path = report.path;
    this.createdAt = report.createdAt;
  }
}
