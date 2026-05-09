import { Injectable, InternalServerErrorException, Logger, RequestTimeoutException } from '@nestjs/common';
import { HttpService } from '@nestjs/axios';
import { lastValueFrom, catchError, timeout, TimeoutError } from 'rxjs';
import FormData from 'form-data';

@Injectable()
export class AnalysisService {
  private readonly logger = new Logger(AnalysisService.name);
  private readonly AI_SERVICE_URL = process.env.AI_SERVICE_URL || 'http://localhost:8000/api/v1/analyze-audio';

  constructor(private readonly httpService: HttpService) {}

  async analyzeAudio(file: Express.Multer.File): Promise<any> {
    try {
      this.logger.log(`Forwarding audio file ${file.originalname} to AI service`);

      const formData = new FormData();
      // append buffer as file
      formData.append('file', file.buffer, {
        filename: file.originalname,
        contentType: file.mimetype,
      });

      // The AI service might take 20-40 seconds because of dynamic model loading
      // Setting timeout to 60 seconds (60000 ms)
      const response = await lastValueFrom(
        this.httpService.post(this.AI_SERVICE_URL, formData, {
          headers: {
            ...formData.getHeaders(),
          },
          // Extra high timeout at axios level
          timeout: 120000,
        }).pipe(
          timeout(120000), // RxJS timeout operator
          catchError((error) => {
            if (error instanceof TimeoutError) {
              this.logger.error('AI service request timed out after 120 seconds');
              throw new RequestTimeoutException('The AI analysis took too long. The server is under heavy load or processing a large audio file. Please try again.');
            }

            this.logger.error(`Error from AI service: ${error.message}`, error.response?.data);
            throw new InternalServerErrorException(
              error.response?.data?.detail || 'Failed to process audio through AI service'
            );
          }),
        )
      );

      this.logger.log('Successfully received analysis from AI service');
      return response.data;
    } catch (error) {
      if (error instanceof RequestTimeoutException || error instanceof InternalServerErrorException) {
        throw error;
      }
      this.logger.error(`Unexpected error during AI analysis: ${error.message}`);
      throw new InternalServerErrorException('An unexpected error occurred while communicating with the AI service');
    }
  }
}
