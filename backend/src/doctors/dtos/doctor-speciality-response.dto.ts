import { ApiProperty } from '@nestjs/swagger';
import { DoctorSpeciality } from '../entities/doctor-speciality.entity';
import { SpecialityResponseDto } from './speciality-response.dto';

export class DoctorSpecialityResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  isPrimary: boolean;

  @ApiProperty()
  yearsOfExperience: number;

  @ApiProperty({ type: () => SpecialityResponseDto, required: false })
  speciality?: SpecialityResponseDto;

  constructor(partial: Partial<DoctorSpeciality>) {
    this.id = partial.id as number;
    this.isPrimary = partial.isPrimary as boolean;
    this.yearsOfExperience = partial.yearsOfExperience as number;
    if (partial.speciality) {
      this.speciality = new SpecialityResponseDto(partial.speciality);
    }
  }
}
