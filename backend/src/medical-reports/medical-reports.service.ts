import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { MedicalReport } from './entities/medical-report.entity';
import { CreateMedicalReportDto } from './dtos/create-medical-report.dto';

@Injectable()
export class MedicalReportsService {
  constructor(
    @InjectRepository(MedicalReport)
    private readonly medicalReportRepository: Repository<MedicalReport>,
  ) {}

  async create(patientId: number, dto: CreateMedicalReportDto): Promise<MedicalReport> {
    // Simulate AI generation logic
    const mockGeneratedReport = `Patient's scan analysis results:\nBased on the input "${dto.scanData}", the AI system detected no anomalies. Standard monitoring recommended.`;

    const report = this.medicalReportRepository.create({
      patientId,
      doctorId: dto.doctorId,
      scanImageUrl: dto.scanData.startsWith('http') ? dto.scanData : null,
      generatedReport: mockGeneratedReport,
      status: 'COMPLETED',
    } as any);

    const saved = await this.medicalReportRepository.save(report);
    return saved as any; // Ignore TS error since TypeORM save types can be weird if multiple entity definitions exist
  }

  async findByPatientId(patientId: number): Promise<MedicalReport[]> {
    return await this.medicalReportRepository.find({
      where: { patientId },
      order: { createdAt: 'DESC' },
    });
  }

  async findOne(id: number): Promise<MedicalReport> {
    const report = await this.medicalReportRepository.findOne({ where: { id } });
    if (!report) {
      throw new NotFoundException(`Medical report with ID ${id} not found`);
    }
    return report;
  }
}
