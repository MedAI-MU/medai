import type { ObjectLiteral } from 'typeorm';

export interface DoctorResourceOptions {
  entity: new (...args: unknown[]) => ObjectLiteral;
  resourceIdParam: string;
  doctorUserId: string;
}
