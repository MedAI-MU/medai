import { IsEnum, IsInt, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateAppointmentDto {
  @IsInt()
  @IsNotEmpty()
  doctorId: number;

  @IsInt()
  @IsNotEmpty()
  slotId: number;

  @IsOptional()
  @IsString()
  bookedForName?: string;

  @IsOptional()
  @IsString()
  bookedForAge?: string;

  @IsOptional()
  @IsString()
  bookedForGender?: string;

  @IsOptional()
  @IsString()
  problemDescription?: string;
}
