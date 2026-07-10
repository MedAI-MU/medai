import { IsString, MaxLength } from 'class-validator';

export class UpdateDoctorDto {
  @IsString()
  @MaxLength(2000)
  about: string;
}
