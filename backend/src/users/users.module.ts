import { Module } from '@nestjs/common';
import { UsersController } from './users.controller';
import { User } from './entities/user.entity';
import { TypeOrmModule } from '@nestjs/typeorm';
import { UsersService } from './users.service';
import { RefreshToken } from './entities/refresh-token.entity';
import { DoctorsModule } from 'src/doctors/doctors.module';
import { PatientsModule } from 'src/patients/patients.module';

@Module({
  imports: [
    TypeOrmModule.forFeature([User, RefreshToken]),
    DoctorsModule,
    PatientsModule,
  ],
  controllers: [UsersController],
  providers: [UsersService],
})
export class UsersModule { }
