import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { IsInt, IsOptional, IsString, Max, Min } from 'class-validator';

export class ReviewAppointmentDto {
  @ApiProperty({
    description: 'Rating for the appointment (1–5)',
    example: 5,
    minimum: 1,
    maximum: 5,
  })
  @IsInt()
  @Min(1)
  @Max(5)
  rating: number;

  @ApiPropertyOptional({
    description: 'Optional written review',
    example: 'Very professional and attentive doctor.',
  })
  @IsOptional()
  @IsString()
  review?: string;
}
