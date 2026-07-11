import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { BullModule } from '@nestjs/bullmq';
import { DatabaseModule } from './database/database.module';
import { UsersModule } from './users/users.module';
import { AuthModule } from './auth/auth.module';
import { PatientsModule } from './patients/patients.module';
import { DoctorsModule } from './doctors/doctors.module';
import databaseConfig from './database/database.config';
import jwtConfig from './auth/jwt.config';
import { SchedulesModule } from './schedules/schedules.module';
import { SharedModule } from './shared/shared.module';
import { AppointmentsModule } from './appointments/appointments.module';
import { ScansModule } from './scans/scans.module';
import { DiagnosisModule } from './diagnosis/diagnosis.module';
import { MailModule } from './mail/mail.module';
import { VoiceReportsModule } from './voice-reports/voice-reports.module';
import { ReportAnalysisModule } from './report-analysis/report-analysis.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true, load: [databaseConfig, jwtConfig] }),
    BullModule.forRootAsync({
      useFactory: (configService: ConfigService) => ({
        connection: {
          host: configService.get<string>('REDIS_HOST', 'localhost'),
          port: parseInt(configService.get<string>('REDIS_PORT', '6379'), 10),
          password: configService.get<string>('REDIS_PASSWORD') || undefined,
        },
      }),
      inject: [ConfigService],
    }),
    DatabaseModule,
    MailModule,
    UsersModule,
    AuthModule,
    PatientsModule,
    SchedulesModule,
    DoctorsModule,
    SharedModule,
    AppointmentsModule,
    ScansModule,
    DiagnosisModule,
    VoiceReportsModule,
    ReportAnalysisModule,
  ],
})
export class AppModule {}
