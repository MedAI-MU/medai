import { SetMetadata } from '@nestjs/common';
import type { DoctorResourceOptions } from '../interfaces/doctor-resource-options.interface';

export const DOCTOR_RESOURCE_KEY = 'doctorResource';

export const DoctorResource = (options: DoctorResourceOptions) =>
  SetMetadata(DOCTOR_RESOURCE_KEY, options);
