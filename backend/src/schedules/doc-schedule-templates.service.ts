import { BadRequestException, Injectable } from '@nestjs/common';
import { CreateDocScheduleTemplateDto } from './dtos/create-doctor-schedule-template.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Doctor } from '../doctors/entities/doctor.entity';
import { Repository } from 'typeorm';
import { areValidTimeRanges } from './utils/are-valid-time-ranges.utils';
import { User } from '../users/entities/user.entity';
import { DocScheduleTemplate } from './entities/doc-schedule-template.entity';
import { DocScheduleTemplateSlot } from './entities/doc-schedule-template-slot.entity';

@Injectable()
export class DocScheduleTemplatesService {
  constructor(
    @InjectRepository(Doctor) private doctorRepository: Repository<Doctor>,
    @InjectRepository(User) private userRepository: Repository<User>,
    @InjectRepository(DocScheduleTemplate)
    private scheduleTemplateRepository: Repository<DocScheduleTemplate>,
  ) {}

  async create(dto: CreateDocScheduleTemplateDto, secretaryId: number) {
    const doctor = await this.doctorRepository.findOneBy({
      userId: dto.doctorId,
    });
    if (!doctor) throw new BadRequestException('Doctor not found');

    const secretary = await this.userRepository.findOneBy({
      id: secretaryId,
    });
    if (!secretary) throw new BadRequestException('Secretary not found');

    if (!areValidTimeRanges(dto.slots))
      throw new BadRequestException('Invalid slots time ranges provided');

    const scheduleTemplate = new DocScheduleTemplate({
      name: dto.name,
      doctor,
      secretary,
      slots: dto.slots.map((slot) => new DocScheduleTemplateSlot(slot)),
    });

    return this.scheduleTemplateRepository.save(scheduleTemplate);
  }
}
