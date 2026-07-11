import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

export interface AiPipelineResult {
  transcription: string;
  clinicalReport: Record<string, unknown>;
}

@Injectable()
export class AiServerService {
  private readonly logger = new Logger(AiServerService.name);
  private readonly serverUrl: string;
  private readonly apiKey: string;
  private readonly timeoutMs: number;

  constructor(private readonly configService: ConfigService) {
    this.serverUrl =
      this.configService.get<string>('AI_SERVER_URL') ||
      'http://localhost:80/api/v1/pipeline';
    this.apiKey =
      this.configService.get<string>('MEDAI_API_KEY') || 'medai-secret-key';
    this.timeoutMs = parseInt(
      this.configService.get<string>('AI_SERVER_TIMEOUT_MS') || '300000',
      10,
    );
  }

  async runPipeline(wavBuffer: Buffer): Promise<AiPipelineResult> {
    const formData = new FormData();
    formData.append(
      'audio_file',
      new Blob([new Uint8Array(wavBuffer)], { type: 'audio/wav' }),
      'audio.wav',
    );

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), this.timeoutMs);

    try {
      const response = await fetch(this.serverUrl, {
        method: 'POST',
        headers: {
          'X-MedAI-API-Key': this.apiKey,
        },
        body: formData,
        signal: controller.signal,
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(
          `AI server returned ${response.status}: ${text || response.statusText}`,
        );
      }

      const data = (await response.json()) as {
        status: string;
        transcription?: string;
        clinical_report?: Record<string, unknown>;
        detail?: string;
      };

      if (data.status !== 'success') {
        throw new Error(
          `AI server processing failed: ${data.detail || data.status}`,
        );
      }

      if (!data.transcription || !data.clinical_report) {
        throw new Error('AI server response missing required fields');
      }

      return {
        transcription: data.transcription,
        clinicalReport: data.clinical_report,
      };
    } finally {
      clearTimeout(timeout);
    }
  }
}
