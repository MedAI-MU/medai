import {
  Controller,
  Post,
  UseInterceptors,
  UploadedFile,
  BadRequestException,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { AnalysisService } from './analysis.service';
import { ApiTags, ApiConsumes, ApiOperation, ApiResponse, ApiBody } from '@nestjs/swagger';

@ApiTags('analysis')
@Controller('analysis')
export class AnalysisController {
  constructor(private readonly analysisService: AnalysisService) {}

  @Post('audio')
  @ApiOperation({ summary: 'Analyze patient audio for clinical insights using AI' })
  @ApiConsumes('multipart/form-data')
  @ApiBody({
    schema: {
      type: 'object',
      properties: {
        file: {
          type: 'string',
          format: 'binary',
          description: 'Audio file (.wav, .mp3, etc.) containing patient symptoms in Egyptian Arabic'
        },
      },
    },
  })
  @ApiResponse({ status: 200, description: 'Successfully analyzed audio' })
  @ApiResponse({ status: 400, description: 'Invalid audio file' })
  @ApiResponse({ status: 408, description: 'Request timeout (AI processing took too long)' })
  @ApiResponse({ status: 500, description: 'Internal server error from AI service' })
  @UseInterceptors(FileInterceptor('file'))
  async analyzeAudio(@UploadedFile() file: Express.Multer.File) {
    if (!file) {
      throw new BadRequestException('No audio file provided');
    }

    // Basic validation of mimetype
    if (!file.mimetype.startsWith('audio/') && !file.mimetype.includes('octet-stream')) {
      throw new BadRequestException('Invalid file format. Please upload an audio file.');
    }

    return this.analysisService.analyzeAudio(file);
  }
}
