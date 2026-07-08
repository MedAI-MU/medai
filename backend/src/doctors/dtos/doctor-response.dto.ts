import { ApiProperty } from '@nestjs/swagger';
import { Doctor } from '../entities/doctor.entity';
import { DoctorSpecialityResponseDto } from './doctor-speciality-response.dto';

export class DoctorResponseDto {
  @ApiProperty()
  userId: number;

  @ApiProperty({ required: false })
  name?: string;

  @ApiProperty({ type: [DoctorSpecialityResponseDto], required: false })
  specialities?: DoctorSpecialityResponseDto[];

  @ApiProperty({ required: false })
  about?: string;

  constructor(partial: Partial<Doctor>) {
    this.userId = partial.userId as number;
    this.name = partial.user?.name;
    this.about = partial.about;
    if (partial.specialities) {
      this.specialities = partial.specialities.map(
        (s) => new DoctorSpecialityResponseDto(s),
      );
    }
  }
}
