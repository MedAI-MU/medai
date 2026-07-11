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

  @ApiProperty()
  rating: number = 0;

  @ApiProperty({ required: false })
  bestReview?: string = 'Not rated yet';

  constructor(partial: Partial<Doctor>) {
    this.userId = partial.userId as number;
    this.name = partial.user?.name;
    this.about = partial.about;
    if (partial.specialities) {
      this.specialities = partial.specialities.map(
        (s) => new DoctorSpecialityResponseDto(s),
      );
    }
    if (partial.appointments) {
      const rated = partial.appointments.filter(
        (a): a is typeof a & { rating: number } => a.rating !== null,
      );
      if (rated.length > 0) {
        this.rating =
          rated.reduce((sum, a) => sum + a.rating, 0) / rated.length;
        const best = rated.reduce((best, a) =>
          a.rating > best.rating ? a : best,
        );
        if (best.review) {
          this.bestReview = best.review;
        }
      }
    }
  }
}
