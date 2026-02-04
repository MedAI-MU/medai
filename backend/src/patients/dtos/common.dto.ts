import { IsOptional, IsString } from 'class-validator';

export class PatientCommonInfoDto {
  @IsString()
  name: string;

  @IsOptional()
  @IsString()
  description: string;
}
