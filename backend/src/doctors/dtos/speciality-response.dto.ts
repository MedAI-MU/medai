import { ApiProperty } from '@nestjs/swagger';
import { Speciality } from '../entities/speciality.entity';

export class SpecialityResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  name: string;

  constructor(partial: Partial<Speciality>) {
    this.id = partial.id as number;
    this.name = partial.name as string;
  }
}
