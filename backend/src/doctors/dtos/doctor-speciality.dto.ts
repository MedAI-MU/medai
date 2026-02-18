import { IsBoolean, IsNumber, IsOptional } from 'class-validator';

export class DoctorSpecialityDto {
  @IsOptional()
  @IsNumber()
  specialityId: number;

  @IsOptional()
  @IsBoolean()
  isPrimary?: boolean;

  @IsOptional()
  @IsNumber()
  yearsOfExperience?: number;
}
