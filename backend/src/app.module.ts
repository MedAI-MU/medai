import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { DatabaseModule } from './database/database.module';
import { UsersModule } from './users/users.module';
import { AuthModule } from './auth/auth.module';
import { PatientsModule } from './patients/patients.module';
import { DoctorsModule } from './doctors/doctors.module';
import databaseConfig from './database/database.config';
import jwtConfig from './auth/jwt.config';
import { SchedulesModule } from './schedules/schedules.module';
import { SharedModule } from './shared/shared.module';
import { MedicalReportsModule } from './medical-reports/medical-reports.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true, load: [databaseConfig, jwtConfig] }),
    DatabaseModule,
    UsersModule,
    AuthModule,
    PatientsModule,
    DoctorsModule,
    SchedulesModule,
    SharedModule,
    MedicalReportsModule,
  ],
})
export class AppModule {}
