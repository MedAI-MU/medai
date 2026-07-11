import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { BullModule } from '@nestjs/bullmq';
import { ReportsController } from './reports.controller';
import { ReportAnalysisService } from './report-analysis.service';
import { ReportAnalysisProcessor } from './report-analysis.processor';
import { LabAiServerService } from './lab-ai-server.service';
import { Report } from '../scans/entities/report.entity';
import { Appointment } from '../appointments/entities/appointment.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([Report, Appointment]),
    BullModule.registerQueue({ name: 'report-analysis' }),
  ],
  controllers: [ReportsController],
  providers: [
    ReportAnalysisService,
    ReportAnalysisProcessor,
    LabAiServerService,
  ],
})
export class ReportAnalysisModule {}
