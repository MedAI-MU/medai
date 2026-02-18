import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Speciality } from './entities/speciality.entity';
import { Repository } from 'typeorm';
import { SpecialityDto } from './dtos/speciality.dto';

@Injectable()
export class SpecialityService {
  constructor(
    @InjectRepository(Speciality)
    private readonly specialitiesRepository: Repository<Speciality>,
  ) {}

  async createSpeciality(specialityDto: SpecialityDto): Promise<Speciality> {
    const speciality = this.specialitiesRepository.create({
      name: specialityDto.name,
    });

    await this.specialitiesRepository.save(speciality);
    return speciality;
  }

  async updateSpeciality(
    id: number,
    specialityDto: SpecialityDto,
  ): Promise<Speciality | null> {
    const speciality = await this.specialitiesRepository.findOne({
      where: { id },
    });

    if (!speciality) {
      return null;
    }

    speciality.name = specialityDto.name;

    await this.specialitiesRepository.save(speciality);
    return speciality;
  }

  async deleteSpeciality(id: number): Promise<Speciality | null> {
    const speciality = await this.specialitiesRepository.findOne({
      where: { id },
    });

    if (!speciality) {
      return null;
    }

    await this.specialitiesRepository.remove(speciality);
    return speciality;
  }

  async findOne(id: number): Promise<Speciality | null> {
    return this.specialitiesRepository.findOne({
      where: { id },
    });
  }
}
