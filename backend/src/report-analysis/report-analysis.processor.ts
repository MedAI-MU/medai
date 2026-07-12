import { Logger } from '@nestjs/common';
import { Processor, WorkerHost } from '@nestjs/bullmq';
import { Job } from 'bullmq';
import { ReportAnalysisService } from './report-analysis.service';
import type { ReportAnalysisJobData } from './report-analysis.service';
import { LabAiServerService } from './lab-ai-server.service';
import { FileStorageService } from '../shared/services/file-storage.service';
import { ReportAnalysisStatusEnum } from './enums/report-analysis-status.enum';

const EXT_MIME_MAP: Record<string, string> = {
  pdf: 'application/pdf',
  jpg: 'image/jpeg',
  jpeg: 'image/jpeg',
  png: 'image/png',
  webp: 'image/webp',
};

@Processor('report-analysis', { concurrency: 1 })
export class ReportAnalysisProcessor extends WorkerHost {
  private readonly logger = new Logger(ReportAnalysisProcessor.name);

  constructor(
    private readonly reportAnalysisService: ReportAnalysisService,
    private readonly labAiServerService: LabAiServerService,
    private readonly fileStorage: FileStorageService,
  ) {
    super();
  }

  async process(job: Job<ReportAnalysisJobData>): Promise<void> {
    const { reportId } = job.data;

    try {
      await this.reportAnalysisService.markStatus(
        reportId,
        ReportAnalysisStatusEnum.PROCESSING,
      );

      const report = await this.reportAnalysisService.getAnalysisRaw(reportId);

      const fileBuffer = await this.fileStorage.downloadFile(report.path);
      const { filename, mimeType } = this.inferFileMeta(report.path);

      const result = await this.labAiServerService.analyze(
        fileBuffer,
        filename,
        mimeType,
      );

      await this.reportAnalysisService.complete(
        reportId,
        result.finalPatientJson,
      );

      this.logger.log(`Report ${reportId} analysis completed successfully`);
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Unknown processing error';
      this.logger.error(
        `Report ${reportId} analysis failed: ${message}`,
        error instanceof Error ? error.stack : undefined,
      );
      await this.reportAnalysisService.fail(reportId, message);
      throw error;
    }
  }

  private inferFileMeta(blobUrl: string): {
    filename: string;
    mimeType: string;
  } {
    try {
      const url = new URL(blobUrl);
      const pathParts = url.pathname.split('/').filter(Boolean);
      const blobName = pathParts.slice(1).join('/') || 'report';
      const ext = blobName.includes('.')
        ? blobName.split('.').pop()!.toLowerCase()
        : '';
      const mimeType = EXT_MIME_MAP[ext] || 'application/octet-stream';
      const filename = ext ? `report.${ext}` : 'report';
      return { filename, mimeType };
    } catch {
      return { filename: 'report', mimeType: 'application/octet-stream' };
    }
  }
}
