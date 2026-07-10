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
import { DOCTOR_RESOURCE_KEY } from '../decorators/doctor-resource.decorator';
import type { DoctorResourceOptions } from '../interfaces/doctor-resource-options.interface';
import { getNested } from '../utils/patient-resource.utils';

@Injectable()
export class DoctorResourceOwnerGuard implements CanActivate {
  constructor(
    private readonly reflector: Reflector,
    @InjectDataSource()
    private readonly dataSource: DataSource,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const options = this.reflector.get<DoctorResourceOptions>(
      DOCTOR_RESOURCE_KEY,
      context.getHandler(),
    );
    if (!options) return true;

    const request = context.switchToHttp().getRequest<{
      user?: { id: number; role: string };
      params: Record<string, string>;
    }>();
    const user = request.user;
    if (!user) return false;

    if (user.role !== 'doctor') return true;

    const resourceId = parseInt(request.params[options.resourceIdParam], 10);
    if (!resourceId) {
      throw new ForbiddenException('Resource ID is required');
    }

    const repository = this.dataSource.getRepository(options.entity);
    const resource = await repository.findOne({
      where: { id: resourceId },
    });

    if (!resource) {
      throw new NotFoundException(`${options.entity.name} not found`);
    }

    const resourceDoctorUserId = getNested(
      resource,
      options.doctorUserId,
    ) as number;

    if (resourceDoctorUserId !== user.id) {
      throw new ForbiddenException(
        'You are not the doctor who owns this resource',
      );
    }

    return true;
  }
}
