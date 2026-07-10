import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { InjectDataSource } from '@nestjs/typeorm';
import { DataSource } from 'typeorm';
import { PATIENT_RESOURCE_KEY } from '../decorators/patient-resource.decorator';
import type { PatientResourceOptions } from '../interfaces/patient-resource-options.interface';
import { getNested } from '../utils/patient-resource.utils';

@Injectable()
export class PatientResourceAccessGuard implements CanActivate {
  constructor(
    private readonly reflector: Reflector,
    @InjectDataSource()
    private readonly dataSource: DataSource,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const options = this.reflector.get<PatientResourceOptions>(
      PATIENT_RESOURCE_KEY,
      context.getHandler(),
    );
    if (!options) return true;

    const request = context.switchToHttp().getRequest<{
      user?: { id: number; role: string };
      params: Record<string, string>;
    }>();
    const user = request.user;
    if (!user) return false;

    const resourceId = parseInt(request.params[options.resourceIdParam], 10);
    const routePatientId = parseInt(request.params.id, 10);

    const repository = this.dataSource.getRepository(options.entity);

    const findOptions: Record<string, unknown> = { where: { id: resourceId } };
    if (options.relations) {
      findOptions.relations = options.relations;
    }

    const resource = await repository.findOne(findOptions);
    if (!resource) {
      throw new NotFoundException(`${options.entity.name} not found`);
    }

    const resourcePatientUserId = getNested(
      resource,
      options.patientUserId,
    ) as number;

    if (user.role === 'patient' && resourcePatientUserId !== user.id) {
      throw new ForbiddenException('Access denied');
    }

    if (user.role === 'doctor' && resourcePatientUserId !== routePatientId) {
      throw new ForbiddenException(
        'This resource does not belong to this patient',
      );
    }

    return true;
  }
}
