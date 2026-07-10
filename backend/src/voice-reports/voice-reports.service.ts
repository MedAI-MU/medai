import {
  BadRequestException,
  Injectable,
  Logger,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { InjectQueue } from '@nestjs/bullmq';
import { Queue } from 'bullmq';
import { randomUUID } from 'crypto';
import { promises as fs } from 'fs';
import { join } from 'path';
import { VoiceReport } from './entities/voice-report.entity';
import { Appointment } from '../appointments/entities/appointment.entity';
import { VoiceReportStatusEnum } from './enums/voice-report-status.enum';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { FileStorageService } from '../shared/services/file-storage.service';

export interface VoiceReportJobData {
  voiceReportId: number;
  tempPath: string;
  originalFileName: string;
  originalMimeType: string;
}

@Injectable()
export class VoiceReportsService {
  private readonly logger = new Logger(VoiceReportsService.name);
  private readonly uploadDir = process.env.UPLOAD_DIR || 'uploads';

  constructor(
    @InjectRepository(VoiceReport)
    private readonly voiceReportsRepository: Repository<VoiceReport>,
    @InjectRepository(Appointment)
    private readonly appointmentsRepository: Repository<Appointment>,
    @InjectQueue('voice-reports')
    private readonly voiceReportsQueue: Queue,
    private readonly fileStorage: FileStorageService,
  ) {}

  async create(
    patientUserId: number,
    appointmentId: number | null,
    file: Express.Multer.File,
    currentUser: TokenUser,
  ): Promise<VoiceReport> {
    if (appointmentId) {
      const appointment = await this.appointmentsRepository.findOne({
        where: { id: appointmentId },
      });
      if (!appointment) {
        throw new NotFoundException('Appointment not found');
      }
      if (appointment.doctorUserId !== currentUser.id) {
        throw new BadRequestException(
          'This appointment does not belong to you',
        );
      }
      if (appointment.patientUserId !== patientUserId) {
        throw new BadRequestException('Patient does not match the appointment');
      }
    }

    const ext = file.originalname.includes('.')
      ? file.originalname.split('.').pop()!
      : 'bin';
    const tempName = `${randomUUID()}.${ext}`;
    const tempPath = join(this.uploadDir, tempName);
    await fs.mkdir(this.uploadDir, { recursive: true });
    await fs.writeFile(tempPath, file.buffer);

    const report = this.voiceReportsRepository.create({
      patientUserId,
      doctorUserId: currentUser.id,
      appointmentId: appointmentId ?? null,
      status: VoiceReportStatusEnum.QUEUED,
      originalFileName: file.originalname,
      originalMimeType: file.mimetype,
    });
    const savedReport = await this.voiceReportsRepository.save(report);

    const jobData: VoiceReportJobData = {
      voiceReportId: savedReport.id,
      tempPath,
      originalFileName: file.originalname,
      originalMimeType: file.mimetype,
    };
    await this.voiceReportsQueue.add('voice', jobData, { attempts: 1 });

    return savedReport;
  }

  async findOne(id: number): Promise<VoiceReport> {
    const report = await this.voiceReportsRepository.findOne({
      where: { id },
    });
    if (!report) throw new NotFoundException('Voice report not found');
    return report;
  }

  async markStatus(id: number, status: VoiceReportStatusEnum): Promise<void> {
    await this.voiceReportsRepository.update(id, { status });
  }

  async complete(
    id: number,
    audioUrl: string,
    transcription: string,
    clinicalReport: Record<string, unknown>,
  ): Promise<void> {
    const report = await this.findOne(id);
    report.status = VoiceReportStatusEnum.COMPLETED;
    report.audioUrl = audioUrl;
    report.transcription = transcription;
    report.clinicalReport = clinicalReport;
    report.errorMessage = null;
    await this.voiceReportsRepository.save(report);
  }

  async fail(id: number, errorMessage: string): Promise<void> {
    const report = await this.findOne(id);
    report.status = VoiceReportStatusEnum.FAILED;
    report.errorMessage = errorMessage;
    await this.voiceReportsRepository.save(report);
  }

  async createForAppointment(
    appointmentId: number,
    file: Express.Multer.File,
    currentUser: TokenUser,
  ): Promise<VoiceReport> {
    const appointment = await this.appointmentsRepository.findOne({
      where: { id: appointmentId },
      select: { id: true, patientUserId: true, doctorUserId: true },
    });
    if (!appointment) {
      throw new NotFoundException('Appointment not found');
    }
    if (appointment.doctorUserId !== currentUser.id) {
      throw new BadRequestException('This appointment does not belong to you');
    }
    return this.create(
      appointment.patientUserId,
      appointmentId,
      file,
      currentUser,
    );
  }

  async findByAppointment(appointmentId: number): Promise<VoiceReport[]> {
    return this.voiceReportsRepository.find({
      where: { appointmentId },
      order: { createdAt: 'DESC' },
    });
  }

  async findOneByAppointment(
    appointmentId: number,
    reportId: number,
  ): Promise<VoiceReport> {
    const report = await this.voiceReportsRepository.findOne({
      where: { id: reportId, appointmentId },
    });
    if (!report) throw new NotFoundException('Voice report not found');
    return report;
  }

  async remove(id: number): Promise<void> {
    const report = await this.findOne(id);
    if (report.audioUrl) {
      await this.fileStorage.deleteFile(report.audioUrl);
    }
    await this.voiceReportsRepository.delete(id);
  }
}
