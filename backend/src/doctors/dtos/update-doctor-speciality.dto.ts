import { PartialType } from '@nestjs/swagger';
import { CreateDoctorSpecialityDto } from './create-doctor-speciality.dto';

export class UpdateDoctorSpecialityDto extends PartialType(
  CreateDoctorSpecialityDto,
) {}
