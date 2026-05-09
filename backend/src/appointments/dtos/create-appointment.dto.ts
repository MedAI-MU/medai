import { ApiProperty } from '@nestjs/swagger';
import { IsInt, IsPositive } from 'class-validator';

export class CreateAppointmentDto {
  @ApiProperty({
    description: 'The ID of the schedule slot to book',
    example: 1,
  })
  @IsInt()
  @IsPositive()
  slotId: number;

  @ApiProperty({
    description: 'The user ID of the doctor to book with',
    example: 2,
  })
  @IsInt()
  @IsPositive()
  doctorId: number;
}
