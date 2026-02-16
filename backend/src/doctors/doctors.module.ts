import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { DoctorsController } from './doctors.controller';
import { DoctorsService } from './doctors.service';
import { Doctor } from './entities/doctor.entity';
import { DoctorSpeciality } from './entities/doctor-speciality.entity';
import { Speciality } from './entities/speciality.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Doctor, DoctorSpeciality, Speciality])],
  controllers: [DoctorsController],
  providers: [DoctorsService],
  exports: [],
})
export class DoctorsModule {}
