import { ApiProperty } from '@nestjs/swagger';
import { IsBoolean, IsNumber, IsOptional } from 'class-validator';

export class DoctorSpecialityDto {
  @ApiProperty({
    description: 'The ID of the speciality to assign to the doctor',
    example: 1,
    required: false,
  })
  @IsOptional()
  @IsNumber()
  specialityId: number;

  @ApiProperty({
    description: 'Whether this is the primary speciality for the doctor',
    example: true,
    required: false,
  })
  @IsOptional()
  @IsBoolean()
  isPrimary?: boolean;

  @ApiProperty({
    description: 'Years of experience in this speciality',
    example: 5,
    required: false,
  })
  @IsOptional()
  @IsNumber()
  yearsOfExperience?: number;
}
