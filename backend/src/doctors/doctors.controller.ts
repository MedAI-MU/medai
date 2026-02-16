import {
  Body,
  Controller,
  Get,
  NotFoundException,
  Param,
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

@Controller('doctors')
export class DoctorsController {
  constructor(private readonly doctorsService: DoctorsService) {}

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

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search')
  async searchBySpeciality(
    @Body() speciality: SpecialityDto,
  ): Promise<Doctor[]> {
    return this.doctorsService.findAllBySpeciality(speciality.name);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Post('specialities')
  async addSpeciality(
    @Body() specialityDto: SpecialityDto,
  ): Promise<Speciality> {
    return await this.doctorsService.createSpeciality(specialityDto);
  }
}
