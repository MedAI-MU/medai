import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

export interface LabAnalyzeResult {
  finalPatientJson: Record<string, unknown>;
}

@Injectable()
export class LabAiServerService {
  private readonly logger = new Logger(LabAiServerService.name);
  private readonly baseUrl: string;
  private readonly timeoutMs: number;

  constructor(private readonly configService: ConfigService) {
    this.baseUrl =
      this.configService.get<string>('LAB_AI_SERVER_URL') ||
      'http://lab-ai-server:8000';
    this.timeoutMs = parseInt(
      this.configService.get<string>('LAB_AI_SERVER_TIMEOUT_MS') || '1800000',
      10,
    );
  }

  async analyze(
    fileBuffer: Buffer,
    filename: string,
    mimeType: string,
  ): Promise<LabAnalyzeResult> {
    const formData = new FormData();
    formData.append(
      'file',
      new Blob([new Uint8Array(fileBuffer)], { type: mimeType }),
      filename,
    );

    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), this.timeoutMs);

    try {
      const response = await fetch(`${this.baseUrl}/analyze`, {
        method: 'POST',
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
        final_patient_json?: Record<string, unknown>;
        detail?: string;
      };

      if (data.status !== 'success') {
        throw new Error(
          `AI server processing failed: ${data.detail || data.status}`,
        );
      }

      if (!data.final_patient_json) {
        throw new Error('AI server response missing final_patient_json');
      }

      return {
        finalPatientJson: data.final_patient_json,
      };
    } finally {
      clearTimeout(timeout);
    }
  }
}
