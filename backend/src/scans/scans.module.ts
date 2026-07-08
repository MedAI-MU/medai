import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ScansController } from './scans.controller';
import { ScansService } from './scans.service';
import { Scan } from './entities/scan.entity';
import { ScanImage } from './entities/scan-image.entity';
import { Report } from './entities/report.entity';
import { Appointment } from '../appointments/entities/appointment.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Scan, ScanImage, Report, Appointment])],
  controllers: [ScansController],
  providers: [ScansService],
})
export class ScansModule {}
