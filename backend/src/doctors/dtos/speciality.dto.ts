import { ApiProperty } from '@nestjs/swagger';
import { IsString } from 'class-validator';

export class SpecialityDto {
  @ApiProperty({
    description: 'The name of the medical speciality',
    example: 'Cardiology',
  })
  @IsString()
  name: string;
}
