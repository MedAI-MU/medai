import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class DoctorDto {
  @ApiProperty({
    description: 'The name of the doctor',
    example: 'Dr. Ahmed Mostafa',
  })
  @IsString()
  name: string;
}
