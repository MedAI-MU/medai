import { Injectable } from '@nestjs/common';
import { Doctor } from './entities/doctor.entity';
import { ILike, Repository } from 'typeorm';
import { InjectRepository } from '@nestjs/typeorm';
import { Speciality } from './entities/speciality.entity';
import { SpecialityDto } from './dtos/speciality.dto';

@Injectable()
export class DoctorsService {
  constructor(
    @InjectRepository(Doctor)
    private doctorsRepository: Repository<Doctor>,
    @InjectRepository(Speciality)
    private specialitiesRepository: Repository<Speciality>,
  ) {}

  async create(userId: number): Promise<Doctor> {
    const doctor = this.doctorsRepository.create({
      user: { id: userId },
    });
    return this.doctorsRepository.save(doctor);
  }

  async findOne(userId: number): Promise<Doctor | null> {
    return this.doctorsRepository.findOne({
      where: { userId: userId },
      relations: {
        specialities: true,
      },
    });
  }

  async findAllBySpeciality(name: string): Promise<Doctor[]> {
    return this.doctorsRepository.find({
      where: { specialities: { speciality: { name: ILike(`%${name}%`) } } },
      relations: {
        specialities: true,
      },
    });
  }

  async createSpeciality(specialityDto: SpecialityDto): Promise<Speciality> {
    const speciality = this.specialitiesRepository.create({
      name: specialityDto.name,
    });

    await this.specialitiesRepository.save(speciality);
    return speciality;
  }
}
