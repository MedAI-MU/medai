import { Module } from '@nestjs/common';
import { PatientsController } from './patients.controller';
import { PatientsService } from './patients.service';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Patient } from './entities/patient.entity';
import { Allergy } from './entities/allergy.entity';
import { ChronicDisease } from './entities/chronic_disease.entity';
import { FamilyHistory } from './entities/family_history.entity';
import { Surgery } from './entities/surgery.entity';
import { EmergencyContact } from './entities/emergency_contact.entity';

@Module({
  imports: [
    TypeOrmModule.forFeature([
      Patient,
      Allergy,
      ChronicDisease,
      FamilyHistory,
      Surgery,
      EmergencyContact,
    ]),
  ],
  controllers: [PatientsController],
  providers: [PatientsService],
  exports: [PatientsService],
})
export class PatientsModule {}
