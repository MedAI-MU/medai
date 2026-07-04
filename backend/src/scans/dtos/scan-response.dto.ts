import { ApiProperty } from '@nestjs/swagger';

class ScanImageDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  path: string;

  constructor(image: { id: number; path: string }) {
    this.id = image.id;
    this.path = image.path;
  }
}

export class ScanResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  patientUserId: number;

  @ApiProperty({ nullable: true })
  appointmentId: number | null;

  @ApiProperty({ type: [ScanImageDto] })
  images: ScanImageDto[];

  @ApiProperty()
  createdAt: Date;

  constructor(scan: {
    id: number;
    patientUserId: number;
    appointmentId: number | null;
    images?: { id: number; path: string }[];
    createdAt: Date;
  }) {
    this.id = scan.id;
    this.patientUserId = scan.patientUserId;
    this.appointmentId = scan.appointmentId;
    this.images = (scan.images || []).map((img) => new ScanImageDto(img));
    this.createdAt = scan.createdAt;
  }
}
