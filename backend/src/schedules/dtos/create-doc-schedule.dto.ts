import { IsArray, IsInt, IsNotEmpty, ValidateNested } from 'class-validator';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';
import { Type } from 'class-transformer';

export class CreateDocScheduleDto {
  @IsNotEmpty()
  @IsInt()
  doctorId: number;

  @IsNotEmpty()
  @IsArray({ message: 'Slots must be an array' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleSlotDto)
  slots: CreateDocScheduleSlotDto[];
}
