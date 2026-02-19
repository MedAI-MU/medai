import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class DoctorDto {
  // For searching. It is not a part of the doctor entity.
  @ApiProperty({
    description: 'The name of the doctor',
    example: 'Dr. Ahmed Mostafa',
  })
  @IsString()
  name: string;
}
