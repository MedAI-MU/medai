import type { ObjectLiteral } from 'typeorm';

export interface PatientResourceOptions {
  entity: new (...args: unknown[]) => ObjectLiteral;
  resourceIdParam: string;
  patientUserId: string;
  relations?: string[];
}
