import { PartialType } from '@nestjs/swagger';
import { EmergencyContactDto } from './emergency_contact.dto';

export class UpdateEmergencyContactDto extends PartialType(
  EmergencyContactDto,
) {}
