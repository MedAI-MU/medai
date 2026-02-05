import { IsDate } from 'class-validator';
import { PatientCommonInfoDto } from './common.dto';
import { Type } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

export class SurgeryDto extends PatientCommonInfoDto {
  @ApiProperty({
    description: 'The date of the surgery',
    example: '2022-01-01',
  })
  @IsDate()
  @Type(() => Date)
  date: Date;
}
