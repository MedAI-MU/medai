import { Injectable } from '@nestjs/common';
import { Doctor } from './entities/doctor.entity';
import { Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';

@Injectable()
export class DoctorsService {
  constructor(
    @InjectRepository(Doctor)
    private doctorsRepository: Repository<Doctor>,
  ) {}

  async createDoctor(specialty: string, userId: number): Promise<Doctor> {
    const doctor = this.doctorsRepository.create({
      specialty,
      user: { id: userId },
    });
    return this.doctorsRepository.save(doctor);
  }

  async findDoctorById(id: number): Promise<Doctor | null> {
    return this.doctorsRepository.findOne({
      where: { id },
      relations: ['user'],
    });
  }

  async findDoctorByUserId(userId: number): Promise<Doctor | null> {
    return this.doctorsRepository.findOne({
      where: { user: { id: userId } },
      relations: ['user'],
    });
  }

  async getAllDoctors(): Promise<Doctor[]> {
    return this.doctorsRepository.find({ relations: ['user'] });
  }
}
