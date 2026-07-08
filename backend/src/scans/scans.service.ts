import {
  ForbiddenException,
  Injectable,
  NotFoundException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { In, Repository } from 'typeorm';
import { Scan } from './entities/scan.entity';
import { ScanImage } from './entities/scan-image.entity';
import { Report } from './entities/report.entity';
import { FileStorageService } from '../shared/services/file-storage.service';
import { Appointment } from '../appointments/entities/appointment.entity';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Injectable()
export class ScansService {
  constructor(
    @InjectRepository(Scan)
    private readonly scansRepository: Repository<Scan>,
    @InjectRepository(ScanImage)
    private readonly scanImagesRepository: Repository<ScanImage>,
    @InjectRepository(Report)
    private readonly reportsRepository: Repository<Report>,
    @InjectRepository(Appointment)
    private readonly appointmentsRepository: Repository<Appointment>,
    private readonly fileStorage: FileStorageService,
  ) {}

  private async getDoctorPatientIds(doctorUserId: number): Promise<number[]> {
    const appointments = await this.appointmentsRepository.find({
      where: { doctorUserId },
      select: { patientUserId: true },
    });
    return [...new Set(appointments.map((a) => a.patientUserId))];
  }

  private async assertCanAccessScan(
    scanId: number,
    currentUser: TokenUser,
  ): Promise<Scan> {
    const scan = await this.scansRepository.findOne({
      where: { id: scanId },
      relations: { images: true, reports: true },
    });
    if (!scan) throw new NotFoundException('Scan not found');

    if (currentUser.role === 'doctor') {
      const patientIds = await this.getDoctorPatientIds(currentUser.id);
      if (!patientIds.includes(scan.patientUserId)) {
        throw new ForbiddenException('You are not related to this patient');
      }
    }

    return scan;
  }

  private async assertCanAccessPatient(
    patientUserId: number,
    currentUser: TokenUser,
  ): Promise<void> {
    if (currentUser.role === 'doctor') {
      const patientIds = await this.getDoctorPatientIds(currentUser.id);
      if (!patientIds.includes(patientUserId)) {
        throw new ForbiddenException('You are not related to this patient');
      }
    }
  }

  async create(
    patientUserId: number,
    appointmentId: number | null,
    files: Express.Multer.File[],
  ): Promise<Scan> {
    const scan = this.scansRepository.create({
      patientUserId,
      appointmentId: appointmentId ?? null,
    });
    const savedScan = await this.scansRepository.save(scan);

    const images: ScanImage[] = [];
    for (const file of files) {
      const url = await this.fileStorage.saveFile(
        file.buffer,
        file.originalname,
        'scans',
        file.mimetype,
      );
      const image = this.scanImagesRepository.create({
        scanId: savedScan.id,
        path: url,
      });
      images.push(image);
    }
    savedScan.images = await this.scanImagesRepository.save(images);
    return savedScan;
  }

  async findAll(currentUser: TokenUser): Promise<Scan[]> {
    if (currentUser.role === 'doctor') {
      const patientIds = await this.getDoctorPatientIds(currentUser.id);
      if (patientIds.length === 0) return [];
      return this.scansRepository.find({
        where: { patientUserId: In(patientIds) },
        relations: { images: true },
        order: { createdAt: 'DESC' },
      });
    }

    return this.scansRepository.find({
      relations: { images: true },
      order: { createdAt: 'DESC' },
    });
  }

  async findByPatient(
    patientUserId: number,
    currentUser: TokenUser,
  ): Promise<Scan[]> {
    if (currentUser.role === 'doctor') {
      const patientIds = await this.getDoctorPatientIds(currentUser.id);
      if (!patientIds.includes(patientUserId)) {
        throw new ForbiddenException('You are not related to this patient');
      }
    }

    return this.scansRepository.find({
      where: { patientUserId },
      relations: { images: true },
      order: { createdAt: 'DESC' },
    });
  }

  async findOne(id: number, currentUser: TokenUser): Promise<Scan> {
    return this.assertCanAccessScan(id, currentUser);
  }

  async delete(id: number): Promise<void> {
    const scan = await this.scansRepository.findOne({
      where: { id },
      relations: { images: true },
    });
    if (!scan) throw new NotFoundException('Scan not found');

    for (const image of scan.images) {
      await this.fileStorage.deleteFile(image.path);
    }
    await this.scansRepository.remove(scan);
  }

  async createReport(
    scanId: number,
    patientUserId: number,
    file: Express.Multer.File,
  ): Promise<Report> {
    const scan = await this.scansRepository.findOne({ where: { id: scanId } });
    if (!scan) throw new NotFoundException('Scan not found');

    const url = await this.fileStorage.saveFile(
      file.buffer,
      file.originalname,
      'reports',
      file.mimetype,
    );

    const report = this.reportsRepository.create({
      scanId,
      patientUserId,
      path: url,
    });
    return this.reportsRepository.save(report);
  }

  async findReports(scanId: number, currentUser: TokenUser): Promise<Report[]> {
    await this.assertCanAccessScan(scanId, currentUser);
    return this.reportsRepository.find({
      where: { scanId },
      order: { createdAt: 'DESC' },
    });
  }

  async findReportById(id: number, currentUser: TokenUser): Promise<Report> {
    const report = await this.reportsRepository.findOne({ where: { id } });
    if (!report) throw new NotFoundException('Report not found');

    await this.assertCanAccessPatient(report.patientUserId, currentUser);
    return report;
  }

  async deleteReport(id: number): Promise<void> {
    const report = await this.reportsRepository.findOne({ where: { id } });
    if (!report) throw new NotFoundException('Report not found');

    await this.fileStorage.deleteFile(report.path);
    await this.reportsRepository.remove(report);
  }

  async getImageFile(
    imageId: number,
    currentUser: TokenUser,
  ): Promise<{ path: string; originalName: string }> {
    const image = await this.scanImagesRepository.findOne({
      where: { id: imageId },
      relations: { scan: true },
    });
    if (!image) throw new NotFoundException('Image not found');

    await this.assertCanAccessScan(image.scan.id, currentUser);

    return {
      path: this.fileStorage.getFullPath(image.path),
      originalName: image.path.split('/').pop() || 'image',
    };
  }

  async getReportFile(
    reportId: number,
    currentUser: TokenUser,
  ): Promise<{ path: string; originalName: string }> {
    const report = await this.reportsRepository.findOne({
      where: { id: reportId },
    });
    if (!report) throw new NotFoundException('Report not found');

    await this.assertCanAccessPatient(report.patientUserId, currentUser);

    return {
      path: this.fileStorage.getFullPath(report.path),
      originalName: report.path.split('/').pop() || 'report',
    };
  }

  async findReportsByPatient(
    patientUserId: number,
    currentUser: TokenUser,
  ): Promise<Report[]> {
    await this.assertCanAccessPatient(patientUserId, currentUser);
    return this.reportsRepository.find({
      where: { patientUserId },
      order: { createdAt: 'DESC' },
    });
  }
}
