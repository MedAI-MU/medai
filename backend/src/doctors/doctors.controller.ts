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
import { SpecialityDto } from './dtos/speciality.dto';
import { Speciality } from './entities/speciality.entity';
import { SpecialityService } from './speciality.service';
import { CreateDoctorSpecialityDto } from './dtos/create-doctor-speciality.dto';
import { DoctorDto } from './dtos/doctor.dto';
import { DoctorResponseDto } from './dtos/doctor-response.dto';
import { SpecialityResponseDto } from './dtos/speciality-response.dto';
import { UpdateDoctorSpecialityDto } from './dtos/update-doctor-speciality.dto';

@Controller('doctors')
export class DoctorsController {
  constructor(
    private readonly doctorsService: DoctorsService,
    private readonly specialityService: SpecialityService,
  ) {}

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search/speciality')
  @HttpCode(HttpStatus.OK)
  @ApiBody({ type: SpecialityDto })
  @ApiOkResponse({
    description: 'Doctors matching the speciality',
    type: [DoctorResponseDto],
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async searchBySpeciality(
    @Body() speciality: SpecialityDto,
  ): Promise<DoctorResponseDto[]> {
    const doctors = await this.doctorsService.findAllBySpeciality(
      speciality.name,
    );
    return doctors.map((d) => new DoctorResponseDto(d));
  }

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Post('search/name')
  @HttpCode(HttpStatus.OK)
  @ApiBody({ type: DoctorDto })
  @ApiOkResponse({
    description: 'Doctors matching the name',
    type: [DoctorResponseDto],
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async searchByName(@Body() doctor: DoctorDto): Promise<DoctorResponseDto[]> {
    const doctors = await this.doctorsService.findAllByName(doctor.name);
    return doctors.map((d) => new DoctorResponseDto(d));
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Get('specialities')
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'Specialities retrieved successfully',
    type: [SpecialityResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async getSpecialities(): Promise<SpecialityResponseDto[]> {
    const specialities = await this.specialityService.findAll();
    return specialities.map((s) => new SpecialityResponseDto(s));
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Post('specialities')
  @HttpCode(HttpStatus.CREATED)
  @ApiBody({ type: SpecialityDto })
  @ApiCreatedResponse({
    description: 'Speciality created successfully',
    type: SpecialityResponseDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async createSpeciality(
    @Body() specialityDto: SpecialityDto,
  ): Promise<SpecialityResponseDto> {
    const speciality =
      await this.specialityService.createSpeciality(specialityDto);
    return new SpecialityResponseDto(speciality);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Patch('specialities/:id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Speciality ID', type: Number })
  @ApiBody({ type: SpecialityDto })
  @ApiOkResponse({
    description: 'Speciality updated successfully',
    type: SpecialityResponseDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiNotFoundResponse({ description: 'Speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async updateSpeciality(
    @Param('id') id: number,
    @Body() specialityDto: SpecialityDto,
  ): Promise<SpecialityResponseDto> {
    const speciality = await this.specialityService.updateSpeciality(
      id,
      specialityDto,
    );
    if (!speciality) {
      throw new NotFoundException(`Speciality not found`);
    }
    return new SpecialityResponseDto(speciality);
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

  @Roles('secretary', 'patient')
  @UseGuards(RolesGuard)
  @Get()
  @HttpCode(HttpStatus.OK)
  @ApiOkResponse({
    description: 'All doctors retrieved successfully',
    type: [DoctorResponseDto],
  })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async findAll(): Promise<DoctorResponseDto[]> {
    const doctors = await this.doctorsService.findAll();
    return doctors.map((d) => new DoctorResponseDto(d));
  }

  @UseGuards(SameIdGuard)
  @Roles('secretary', 'patient')
  @Get(':id')
  @HttpCode(HttpStatus.OK)
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiOkResponse({
    description: 'Doctor details retrieved successfully',
    type: DoctorResponseDto,
  })
  @ApiNotFoundResponse({ description: 'Doctor not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  // TODO: Retrieve doctor details for assigned patient
  async findOne(@Param('id') id: number): Promise<DoctorResponseDto> {
    const doctor = await this.doctorsService.findOne(id);
    if (!doctor) {
      throw new NotFoundException(`Doctor not found`);
    }
    return new DoctorResponseDto(doctor);
  }

  @Roles('secretary')
  @UseGuards(RolesGuard)
  @Post(':id/specialities')
  @HttpCode(HttpStatus.CREATED)
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiBody({ type: CreateDoctorSpecialityDto })
  @ApiCreatedResponse({
    description: 'Doctor speciality added successfully',
    type: DoctorResponseDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiNotFoundResponse({ description: 'Doctor or speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async addDoctorSpeciality(
    @Param('id') id: number,
    @Body() doctorSpeciality: CreateDoctorSpecialityDto,
  ): Promise<DoctorResponseDto> {
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
    const updatedDoctor = await this.doctorsService.addSpeciality(
      doctor,
      doctorSpeciality,
      speciality,
    );
    return new DoctorResponseDto(updatedDoctor);
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
  @ApiOkResponse({
    description: 'Doctor speciality updated successfully',
    type: DoctorResponseDto,
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiNotFoundResponse({ description: 'Doctor or speciality not found' })
  @ApiUnauthorizedResponse({ description: 'Unauthorized' })
  @ApiForbiddenResponse({ description: 'Forbidden' })
  async updateDoctorSpeciality(
    @Param('id') id: number,
    @Param('specialityId') doctorSpecialityId: number,
    @Body() doctorSpeciality: UpdateDoctorSpecialityDto,
  ): Promise<DoctorResponseDto> {
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
    return new DoctorResponseDto(updatedDoctor);
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
