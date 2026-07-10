import {
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectDataSource } from '@nestjs/typeorm';
import { DataSource } from 'typeorm';
import { Appointment } from '../../appointments/entities/appointment.entity';

@Injectable()
export class AppointmentAccessGuard implements CanActivate {
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

    const appointmentId = parseInt(request.params.appointmentId, 10);
    if (!appointmentId) {
      throw new ForbiddenException('Appointment ID is required');
    }

    const appointmentsRepository = this.dataSource.getRepository(Appointment);
    const appointment = await appointmentsRepository.findOne({
      where: { id: appointmentId },
      select: { id: true, doctorUserId: true },
    });

    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }

    if (user.role === 'doctor' && appointment.doctorUserId !== user.id) {
      throw new ForbiddenException('You are not assigned to this appointment');
    }

    return true;
  }
}
