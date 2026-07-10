import { Injectable, NotFoundException } from '@nestjs/common';
import { Doctor } from './entities/doctor.entity';
import { ILike, Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Speciality } from './entities/speciality.entity';
import { CreateDoctorSpecialityDto } from './dtos/create-doctor-speciality.dto';
import { DoctorSpeciality } from './entities/doctor-speciality.entity';
import { UpdateDoctorSpecialityDto } from './dtos/update-doctor-speciality.dto';
import { UpdateDoctorDto } from './dtos/update-doctor.dto';

@Injectable()
export class DoctorsService {
  constructor(
    @InjectRepository(Doctor)
    private doctorsRepository: Repository<Doctor>,
  ) {}

  async create(userId: number): Promise<Doctor> {
    const doctor = this.doctorsRepository.create({
      user: { id: userId },
    });
    return this.doctorsRepository.save(doctor);
  }

  async update(userId: number, dto: UpdateDoctorDto): Promise<Doctor | null> {
    const doctor = await this.findOne(userId);
    if (!doctor) {
      return null;
    }
    doctor.about = dto.about;
    return this.doctorsRepository.save(doctor);
  }

  async findAll(): Promise<Doctor[]> {
    return this.doctorsRepository.find({
      relations: {
        specialities: { speciality: true },
      },
    });
  }

  async findOne(userId: number): Promise<Doctor | null> {
    return this.doctorsRepository.findOne({
      where: { userId: userId },
      relations: {
        specialities: { speciality: true },
      },
    });
  }

  async findAllByName(name: string): Promise<Doctor[]> {
    return this.doctorsRepository.find({
      where: { user: { name: ILike(`%${name}%`) } },
      relations: {
        specialities: { speciality: true },
      },
    });
  }

  async findAllBySpeciality(name: string): Promise<Doctor[]> {
    return this.doctorsRepository.find({
      where: { specialities: { speciality: { name: ILike(`%${name}%`) } } },
      relations: {
        specialities: { speciality: true },
      },
    });
  }

  async addSpeciality(
    doctor: Doctor,
    doctorSpeciality: CreateDoctorSpecialityDto,
    speciality: Speciality,
  ): Promise<Doctor> {
    doctor.specialities.push({
      doctor,
      speciality,
      isPrimary: doctorSpeciality.isPrimary || false,
      yearsOfExperience: doctorSpeciality.yearsOfExperience || 0,
    } as DoctorSpeciality);
    await this.doctorsRepository.save(doctor);
    return (await this.findOne(doctor.userId)) as Doctor;
  }

  async updateDoctorSpeciality(
    doctor: Doctor,
    doctorSpeciality: UpdateDoctorSpecialityDto,
    doctorSpecialityId: number,
  ): Promise<Doctor | null> {
    const doctorSpecialityToUpdate = doctor.specialities.find(
      (speciality) => speciality.id === doctorSpecialityId,
    );
    if (!doctorSpecialityToUpdate) {
      return null;
    }
    if (doctorSpeciality.isPrimary !== undefined) {
      doctorSpecialityToUpdate.isPrimary = doctorSpeciality.isPrimary;
    }
    if (doctorSpeciality.yearsOfExperience !== undefined) {
      doctorSpecialityToUpdate.yearsOfExperience =
        doctorSpeciality.yearsOfExperience;
    }
    return this.doctorsRepository.save(doctor);
  }

  async removeDoctorSpeciality(
    doctor: Doctor,
    doctorSpecialityId: number,
  ): Promise<Doctor | null> {
    const doctorSpecialityToRemove = doctor.specialities.find(
      (speciality) => speciality.id === doctorSpecialityId,
    );
    if (!doctorSpecialityToRemove) {
      return null;
    }
    doctor.specialities = doctor.specialities.filter(
      (speciality) => speciality.id !== doctorSpecialityId,
    );
    return this.doctorsRepository.save(doctor);
  }
}
