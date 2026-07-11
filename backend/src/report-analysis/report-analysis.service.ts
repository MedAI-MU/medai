import {
  BadRequestException,
  ConflictException,
  ForbiddenException,
  Injectable,
  Logger,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { In, Repository } from 'typeorm';
import { InjectQueue } from '@nestjs/bullmq';
import { Queue } from 'bullmq';
import { Report } from '../scans/entities/report.entity';
import { Appointment } from '../appointments/entities/appointment.entity';
import { ReportAnalysisStatusEnum } from './enums/report-analysis-status.enum';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { FileStorageService } from '../shared/services/file-storage.service';

export interface ReportAnalysisJobData {
  reportId: number;
}

@Injectable()
export class ReportAnalysisService {
  private readonly logger = new Logger(ReportAnalysisService.name);

  constructor(
    @InjectRepository(Report)
    private readonly reportsRepository: Repository<Report>,
    @InjectRepository(Appointment)
    private readonly appointmentsRepository: Repository<Appointment>,
    @InjectQueue('report-analysis')
    private readonly reportAnalysisQueue: Queue,
    private readonly fileStorage: FileStorageService,
  ) {}

  async createReport(
    patientUserId: number,
    appointmentId: number | null,
    file: Express.Multer.File,
  ): Promise<Report> {
    if (appointmentId) {
      const appointment = await this.appointmentsRepository.findOne({
        where: { id: appointmentId },
      });
      if (!appointment) {
        throw new NotFoundException('Appointment not found');
      }
      if (appointment.patientUserId !== patientUserId) {
        throw new BadRequestException('Patient does not match the appointment');
      }
    }

    const url = await this.fileStorage.saveFile(
      file.buffer,
      file.originalname,
      'reports',
      file.mimetype,
    );

    const report = this.reportsRepository.create({
      patientUserId,
      scanId: null,
      appointmentId: appointmentId ?? null,
      path: url,
      analysisStatus: null,
      analysisResult: null,
      analysisError: null,
    });
    return this.reportsRepository.save(report);
  }

  async triggerAnalysis(
    reportId: number,
    currentUser: TokenUser,
  ): Promise<Report> {
    const report = await this.reportsRepository.findOne({
      where: { id: reportId },
    });
    if (!report) {
      throw new NotFoundException('Report not found');
    }

    if (
      report.analysisStatus === ReportAnalysisStatusEnum.QUEUED ||
      report.analysisStatus === ReportAnalysisStatusEnum.PROCESSING
    ) {
      throw new ConflictException('Analysis is already in progress');
    }

    this.assertAccess(report, currentUser);

    report.analysisStatus = ReportAnalysisStatusEnum.QUEUED;
    report.analysisResult = null;
    report.analysisError = null;
    await this.reportsRepository.save(report);

    const jobData: ReportAnalysisJobData = { reportId };
    await this.reportAnalysisQueue.add('analyze', jobData, { attempts: 1 });

    return report;
  }

  async getAnalysisRaw(reportId: number): Promise<Report> {
    const report = await this.reportsRepository.findOne({
      where: { id: reportId },
    });
    if (!report) {
      throw new NotFoundException('Report not found');
    }
    return report;
  }

  async getAnalysis(reportId: number, currentUser: TokenUser): Promise<Report> {
    const report = await this.reportsRepository.findOne({
      where: { id: reportId },
    });
    if (!report) {
      throw new NotFoundException('Report not found');
    }
    this.assertAccess(report, currentUser);
    return report;
  }

  async findAllForUser(currentUser: TokenUser): Promise<Report[]> {
    if (currentUser.role === 'secretary' || currentUser.role === 'manager') {
      return this.reportsRepository.find({
        order: { createdAt: 'DESC' },
      });
    }

    if (currentUser.role === 'patient') {
      return this.reportsRepository.find({
        where: { patientUserId: currentUser.id },
        order: { createdAt: 'DESC' },
      });
    }

    if (currentUser.role === 'doctor') {
      const appointments = await this.appointmentsRepository.find({
        where: { doctorUserId: currentUser.id },
        select: { patientUserId: true },
      });
      const patientIds = [...new Set(appointments.map((a) => a.patientUserId))];
      if (patientIds.length === 0) return [];
      return this.reportsRepository.find({
        where: { patientUserId: In(patientIds) },
        order: { createdAt: 'DESC' },
      });
    }

    return [];
  }

  async markStatus(
    reportId: number,
    status: ReportAnalysisStatusEnum,
  ): Promise<void> {
    await this.reportsRepository.update(reportId, { analysisStatus: status });
  }

  async complete(
    reportId: number,
    analysisResult: Record<string, unknown>,
  ): Promise<void> {
    const report = await this.getAnalysisRaw(reportId);
    report.analysisStatus = ReportAnalysisStatusEnum.COMPLETED;
    report.analysisResult = analysisResult;
    report.analysisError = null;
    await this.reportsRepository.save(report);
  }

  async fail(reportId: number, errorMessage: string): Promise<void> {
    const report = await this.getAnalysisRaw(reportId);
    report.analysisStatus = ReportAnalysisStatusEnum.FAILED;
    report.analysisError = errorMessage;
    await this.reportsRepository.save(report);
  }

  private assertAccess(report: Report, currentUser: TokenUser): void {
    if (currentUser.role === 'secretary' || currentUser.role === 'manager') {
      return;
    }
    if (
      currentUser.role === 'patient' &&
      report.patientUserId === currentUser.id
    ) {
      return;
    }
    throw new ForbiddenException('Access denied to this report');
  }
}
