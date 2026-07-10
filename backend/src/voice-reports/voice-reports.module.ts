import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { BullModule } from '@nestjs/bullmq';
import { AppointmentVoiceReportsController } from './appointment-voice-reports.controller';
import { VoiceReportsService } from './voice-reports.service';
import { VoiceReportsProcessor } from './voice-reports.processor';
import { AiServerService } from './ai-server.service';
import { VoiceReport } from './entities/voice-report.entity';
import { Appointment } from '../appointments/entities/appointment.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([VoiceReport, Appointment]),
    BullModule.registerQueue({ name: 'voice-reports' }),
  ],
  controllers: [AppointmentVoiceReportsController],
  providers: [VoiceReportsService, VoiceReportsProcessor, AiServerService],
})
export class VoiceReportsModule {}
