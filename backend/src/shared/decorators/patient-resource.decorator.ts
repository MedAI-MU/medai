import { SetMetadata } from '@nestjs/common';
import type { PatientResourceOptions } from '../interfaces/patient-resource-options.interface';

export const PATIENT_RESOURCE_KEY = 'patientResource';

export const PatientResource = (options: PatientResourceOptions) =>
  SetMetadata(PATIENT_RESOURCE_KEY, options);
