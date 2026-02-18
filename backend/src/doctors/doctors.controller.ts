import {
  Body,
  Controller,
  Delete,
  Get,
  NotFoundException,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import { DoctorsService } from './doctors.service';
import { SameIdGuard } from 'src/shared/guards/same-id.guard';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { Doctor } from './entities/doctor.entity';
import { SpecialityDto } from './dtos/speciality.dto';
import { Speciality } from './entities/speciality.entity';
import { SpecialityService } from './speciality.service';
import { DoctorSpecialityDto } from './dtos/doctor-speciality.dto';

@Controller('doctors')
export class DoctorsController {
  constructor(
    private readonly doctorsService: DoctorsService,
    private readonly specialityService: SpecialityService,
  ) {}

  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @Get(':id')
  // TODO: Retrieve doctor details for assigned patient
  async findOne(@Param('id') id: number): Promise<Doctor> {
    const doctor = await this.doctorsService.findOne(id);
    if (!doctor) {
      throw new NotFoundException(`Doctor not found`);
    }
    return doctor;
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Post(':id/specialities')
  async addDoctorSpeciality(
    @Param('id') id: number,
    @Body() doctorSpeciality: DoctorSpecialityDto,
  ): Promise<Doctor> {
    const doctor = await this.doctorsService.findOne(id);
    if (!doctor) {
      throw new NotFoundException(`Doctor not found`);
    }
    const speciality = await this.specialityService.findOne(
      doctorSpeciality.specialityId,
    );
    if (!speciality) {
      throw new NotFoundException(
        `Speciality with id ${doctorSpeciality.specialityId} not found`,
      );
    }
    return await this.doctorsService.addSpeciality(
      doctor,
      doctorSpeciality,
      speciality,
    );
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Patch(':id/specialities/:specialityId')
  async updateDoctorSpeciality(
    @Param('id') id: number,
    @Param('specialityId') doctorSpecialityId: number,
    @Body() doctorSpeciality: DoctorSpecialityDto,
  ): Promise<Doctor> {
    const doctor = await this.doctorsService.findOne(id);
    if (!doctor) {
      throw new NotFoundException('Doctor not found');
    }
    const updatedDoctor = await this.doctorsService.updateDoctorSpeciality(
      doctor,
      doctorSpeciality,
      doctorSpecialityId,
    );
    if (!updatedDoctor) {
      throw new NotFoundException('Speciality not found');
    }
    return updatedDoctor;
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Delete(':id/specialities/:specialityId')
  async removeDoctorSpeciality(
    @Param('id') id: number,
    @Param('specialityId') doctorSpecialityId: number,
  ): Promise<void> {
    const doctor = await this.doctorsService.findOne(id);
    if (!doctor) {
      throw new NotFoundException('Doctor not found');
    }
    const removedDoctor = await this.doctorsService.removeDoctorSpeciality(
      doctor,
      doctorSpecialityId,
    );
    if (!removedDoctor) {
      throw new NotFoundException('Speciality not found');
    }
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search/speciality')
  async searchBySpeciality(
    @Body() speciality: SpecialityDto,
  ): Promise<Doctor[]> {
    return this.doctorsService.findAllBySpeciality(speciality.name);
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search/name')
  async searchByName(@Body() name: string): Promise<Doctor[]> {
    return this.doctorsService.findAllByName(name);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Post('specialities')
  async createSpeciality(
    @Body() specialityDto: SpecialityDto,
  ): Promise<Speciality> {
    return await this.specialityService.createSpeciality(specialityDto);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Patch('specialities/:id')
  async updateSpeciality(
    @Param('id') id: number,
    @Body() specialityDto: SpecialityDto,
  ): Promise<Speciality> {
    const speciality = await this.specialityService.updateSpeciality(
      id,
      specialityDto,
    );
    if (!speciality) {
      throw new NotFoundException(`Speciality not found`);
    }
    return speciality;
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Delete('specialities/:id')
  async deleteSpeciality(@Param('id') id: number): Promise<void> {
    const speciality = await this.specialityService.deleteSpeciality(id);
    if (!speciality) {
      throw new NotFoundException(`Speciality not found`);
    }
  }
}
