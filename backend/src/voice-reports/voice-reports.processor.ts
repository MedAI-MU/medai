import { Logger, OnModuleInit } from '@nestjs/common';
import { Processor, WorkerHost } from '@nestjs/bullmq';
import { Job } from 'bullmq';
import ffmpeg from 'fluent-ffmpeg';
import { promises as fs } from 'fs';
import { randomUUID } from 'crypto';
import { join } from 'path';
import { VoiceReportsService } from './voice-reports.service';
import type { VoiceReportJobData } from './voice-reports.service';
import { AiServerService } from './ai-server.service';
import { FileStorageService } from '../shared/services/file-storage.service';
import { VoiceReportStatusEnum } from './enums/voice-report-status.enum';

@Processor('voice-reports', { concurrency: 1 })
export class VoiceReportsProcessor extends WorkerHost implements OnModuleInit {
  private readonly logger = new Logger(VoiceReportsProcessor.name);
  private readonly uploadDir = process.env.UPLOAD_DIR || 'uploads';

  constructor(
    private readonly voiceReportsService: VoiceReportsService,
    private readonly aiServerService: AiServerService,
    private readonly fileStorage: FileStorageService,
  ) {
    super();
  }

  async onModuleInit(): Promise<void> {
    await fs.mkdir(this.uploadDir, { recursive: true });
  }

  async process(job: Job<VoiceReportJobData>): Promise<void> {
    const { voiceReportId, tempPath } = job.data;
    let wavPath: string | null = null;

    try {
      await this.voiceReportsService.markStatus(
        voiceReportId,
        VoiceReportStatusEnum.PROCESSING,
      );

      wavPath = await this.convertToWav(tempPath);

      const wavBuffer = await fs.readFile(wavPath);

      const [audioUrl, aiResult] = await Promise.all([
        this.fileStorage.saveFile(
          wavBuffer,
          'audio.wav',
          'voice-reports',
          'audio/wav',
        ),
        this.aiServerService.runPipeline(wavBuffer),
      ]);

      await this.voiceReportsService.complete(
        voiceReportId,
        audioUrl,
        aiResult.transcription,
        aiResult.clinicalReport,
      );

      this.logger.log(`Voice report ${voiceReportId} completed successfully`);
    } catch (error) {
      const message =
        error instanceof Error ? error.message : 'Unknown processing error';
      this.logger.error(
        `Voice report ${voiceReportId} failed: ${message}`,
        error instanceof Error ? error.stack : undefined,
      );
      await this.voiceReportsService.fail(voiceReportId, message);
      throw error;
    } finally {
      await this.cleanupTempFile(tempPath);
      if (wavPath) await this.cleanupTempFile(wavPath);
    }
  }

  private convertToWav(inputPath: string): Promise<string> {
    return new Promise((resolve, reject) => {
      const outputName = `${randomUUID()}.wav`;
      const outputPath = join(this.uploadDir, outputName);

      ffmpeg(inputPath)
        .audioCodec('pcm_s16le')
        .audioFrequency(16000)
        .audioChannels(1)
        .format('wav')
        .on('end', () => resolve(outputPath))
        .on('error', (err) =>
          reject(new Error(`FFmpeg conversion failed: ${err.message}`)),
        )
        .save(outputPath);
    });
  }

  private async cleanupTempFile(filePath: string): Promise<void> {
    try {
      await fs.unlink(filePath);
    } catch {
      // file may not exist; ignore
    }
  }
}
