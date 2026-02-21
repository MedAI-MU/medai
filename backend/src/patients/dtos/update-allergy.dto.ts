import { PartialType } from '@nestjs/swagger';
import { AllergyDto } from './allergy.dto';

export class UpdateAllergyDto extends PartialType(AllergyDto) {}
