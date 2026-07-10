import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
} from '@nestjs/common';
import { InjectDataSource } from '@nestjs/typeorm';
import { DataSource } from 'typeorm';
import { Appointment } from '../../appointments/entities/appointment.entity';

@Injectable()
export class DoctorAssignedGuard implements CanActivate {
  constructor(
    @InjectDataSource()
    private readonly dataSource: DataSource,
  ) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest<{
      user?: { id: number; role: string };
      params: Record<string, string>;
    }>();
    const user = request.user;
    if (!user) return false;

    if (user.role !== 'doctor') return true;

    const patientId = parseInt(request.params.id as string, 10);
    if (!patientId) {
      throw new ForbiddenException('Patient ID is required');
    }

    const appointmentsRepository = this.dataSource.getRepository(Appointment);
    const appointments = await appointmentsRepository.find({
      where: { doctorUserId: user.id },
      select: { patientUserId: true },
    });
    const patientIds = [...new Set(appointments.map((a) => a.patientUserId))];

    if (!patientIds.includes(patientId)) {
      throw new ForbiddenException('You are not assigned to this patient');
    }

    return true;
  }
}
