import { ApiProperty } from '@nestjs/swagger';
import { IsString, IsNotEmpty } from 'class-validator';

export class UpdateSymptomsDto {
  @ApiProperty()
  @IsString()
  @IsNotEmpty()
  symptoms: string;
}
