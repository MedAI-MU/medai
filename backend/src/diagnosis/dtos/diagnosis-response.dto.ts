import { ApiProperty } from '@nestjs/swagger';

export class DiagnosisResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  patientUserId: number;

  @ApiProperty()
  doctorUserId: number;

  @ApiProperty()
  appointmentId: number;

  @ApiProperty()
  symptoms: string;

  @ApiProperty()
  summary: string;

  @ApiProperty()
  createdAt: Date;

  @ApiProperty()
  updatedAt: Date;

  constructor(diagnosis: {
    id: number;
    patientUserId: number;
    doctorUserId: number;
    appointmentId: number;
    symptoms: string;
    summary: string;
    createdAt: Date;
    updatedAt: Date;
  }) {
    this.id = diagnosis.id;
    this.patientUserId = diagnosis.patientUserId;
    this.doctorUserId = diagnosis.doctorUserId;
    this.appointmentId = diagnosis.appointmentId;
    this.symptoms = diagnosis.symptoms;
    this.summary = diagnosis.summary;
    this.createdAt = diagnosis.createdAt;
    this.updatedAt = diagnosis.updatedAt;
  }
}
