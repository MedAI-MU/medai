import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  NotFoundException,
  Param,
  Patch,
  Post,
  UseGuards,
} from '@nestjs/common';
import {
  ApiBadRequestResponse,
  ApiBody,
  ApiCreatedResponse,
  ApiForbiddenResponse,
  ApiNoContentResponse,
  ApiNotFoundResponse,
  ApiOkResponse,
  ApiParam,
  ApiUnauthorizedResponse,
} from '@nestjs/swagger';
import { DoctorsService } from './doctors.service';
import { SameIdGuard } from 'src/shared/guards/same-id.guard';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { Doctor } from './entities/doctor.entity';
import { SpecialityDto } from './dtos/speciality.dto';
import { Speciality } from './entities/speciality.entity';
import { SpecialityService } from './speciality.service';
import { CreateDoctorSpecialityDto } from './dtos/create-doctor-speciality.dto';
import { DoctorDto } from './dtos/doctor.dto';
import { UpdateDoctorSpecialityDto } from './dtos/update-doctor-speciality.dto';

@Controller('doctors')
export class DoctorsController {
  constructor(
    private readonly doctorsService: DoctorsService,
    private readonly specialityService: SpecialityService,
  ) {}

  @Get()
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'All doctors retrieved successfully' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  async findAll(): Promise<Doctor[]> {
    return this.doctorsService.findAll();
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search/speciality')
  @HttpCode(HttpStatus.OK)
  @ApiBody({ type: SpecialityDto })
  @ApiOkResponse({ description: 'Doctors matching the speciality' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async searchBySpeciality(
    @Body() speciality: SpecialityDto,
  ): Promise<Doctor[]> {
    return this.doctorsService.findAllBySpeciality(speciality.name);
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search/name')
  @HttpCode(HttpStatus.OK)
  @ApiBody({ type: DoctorDto })
  @ApiOkResponse({ description: 'Doctors matching the name' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async searchByName(@Body() doctor: DoctorDto): Promise<Doctor[]> {
    return this.doctorsService.findAllByName(doctor.name);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Get('specialities')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({ description: 'Specialities retrieved successfully' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getSpecialities(): Promise<Speciality[]> {
    return await this.specialityService.findAll();
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Post('specialities')
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: SpecialityDto })
  @ApiCreatedResponse({ description: 'Speciality created successfully' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async createSpeciality(
    @Body() specialityDto: SpecialityDto,
  ): Promise<Speciality> {
    return await this.specialityService.createSpeciality(specialityDto);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Patch('specialities/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Speciality ID', type: Number })
  @ApiBody({ type: SpecialityDto })
  @ApiOkResponse({ description: 'Speciality updated successfully' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiNotFoundResponse({ description: 'Speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
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
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Speciality ID', type: Number })
  @ApiNoContentResponse({ description: 'Speciality deleted successfully' })
  @ApiNotFoundResponse({ description: 'Speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async deleteSpeciality(@Param('id') id: number): Promise<void> {
    const speciality = await this.specialityService.deleteSpeciality(id);
    if (!speciality) {
      throw new NotFoundException(`Speciality not found`);
    }
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @Get(':id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiOkResponse({ description: 'Doctor details retrieved successfully' })
  @ApiNotFoundResponse({ description: 'Doctor not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
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
  @HttpCode(HttpStatus.CREATED)
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiBody({ type: CreateDoctorSpecialityDto })
  @ApiCreatedResponse({ description: 'Doctor speciality added successfully' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiNotFoundResponse({ description: 'Doctor or speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async addDoctorSpeciality(
    @Param('id') id: number,
    @Body() doctorSpeciality: CreateDoctorSpecialityDto,
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
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({
    name: 'specialityId',
    description: 'Doctor speciality ID',
    type: Number,
  })
  @ApiBody({ type: UpdateDoctorSpecialityDto })
  @ApiOkResponse({ description: 'Doctor speciality updated successfully' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiNotFoundResponse({ description: 'Doctor or speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async updateDoctorSpeciality(
    @Param('id') id: number,
    @Param('specialityId') doctorSpecialityId: number,
    @Body() doctorSpeciality: UpdateDoctorSpecialityDto,
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
  @HttpCode(HttpStatus.NO_CONTENT)
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({
    name: 'specialityId',
    description: 'Doctor speciality ID',
    type: Number,
  })
  @ApiNoContentResponse({
    description: 'Doctor speciality removed successfully',
  })
  @ApiNotFoundResponse({ description: 'Doctor or speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
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
}
