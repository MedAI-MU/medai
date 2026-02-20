import { ApiProperty } from '@nestjs/swagger';
import { IsBoolean, IsNumber } from 'class-validator';

export class CreateDoctorSpecialityDto {
  @ApiProperty({
    description: 'The ID of the speciality to assign to the doctor',
    example: 1,
  })
  @IsNumber()
  specialityId: number;

  @ApiProperty({
    description: 'Whether this is the primary speciality for the doctor',
    example: true,
  })
  @IsBoolean()
  isPrimary: boolean;

  @ApiProperty({
    description: 'Years of experience in this speciality',
    example: 5,
  })
  @IsNumber()
  yearsOfExperience: number;
}
