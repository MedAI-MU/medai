import { BadRequestException, Injectable } from '@nestjs/common';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { InjectRepository } from '@nestjs/typeorm';
import { Doctor } from '../doctors/entities/doctor.entity';
import { Repository } from 'typeorm';
import { areValidTimeRanges } from './utils/are-valid-time-ranges.utils';
import { User } from '../users/entities/user.entity';
import { DocScheduleTemplate } from './entities/doc-schedule-template.entity';
import { DocScheduleTemplateSlot } from './entities/doc-schedule-template-slot.entity';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';
import { DataSource } from 'typeorm';

@Injectable()
export class DocScheduleTemplatesService {
  constructor(
    @InjectRepository(Doctor) private doctorRepository: Repository<Doctor>,
    @InjectRepository(User) private userRepository: Repository<User>,
    @InjectRepository(DocScheduleTemplate)
    private scheduleTemplateRepository: Repository<DocScheduleTemplate>,
    private readonly dataSource: DataSource,
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
      throw new BadRequestException(
        'Invalid or overlapping time ranges in slots',
      );

    const scheduleTemplate = new DocScheduleTemplate({
      name: dto.name,
      doctor,
      secretary,
      slots: dto.slots.map(
        (slot) =>
          new DocScheduleTemplateSlot({
            ...slot,
            weekDay: slot.day,
          }),
      ),
    });

    return this.scheduleTemplateRepository.save(scheduleTemplate);
  }

  async update(dto: UpdateDocScheduleTemplateDto, templateId: number) {
    return this.dataSource.transaction(async (manager) => {
      const templateRepo = manager.getRepository(DocScheduleTemplate);
      const slotRepo = manager.getRepository(DocScheduleTemplateSlot);
      const doctorRepo = manager.getRepository(Doctor);

      const scheduleTemplate = await templateRepo.findOne({
        where: { id: templateId },
      });

      if (!scheduleTemplate)
        throw new BadRequestException('Schedule template not found');

      if (dto.slots && !areValidTimeRanges(dto.slots))
        throw new BadRequestException(
          'Invalid or overlapping time ranges in slots',
        );

      scheduleTemplate.name = dto.name ?? scheduleTemplate.name;

      if (dto.doctorId) {
        const doctor = await doctorRepo.findOneBy({
          userId: dto.doctorId,
        });

        if (!doctor) throw new BadRequestException('Doctor not found');

        scheduleTemplate.doctor = doctor;
      }

      if (dto.slots) {
        await slotRepo.delete({
          template: { id: templateId },
        });

        const newSlots = dto.slots.map((slot) =>
          slotRepo.create({
            ...slot,
            template: scheduleTemplate,
            weekDay: slot.day,
          }),
        );

        await slotRepo.save(newSlots);
      }

      return templateRepo.save(scheduleTemplate);
    });
  }

  async delete(templateId: number) {
    const result = await this.scheduleTemplateRepository.delete({
      id: templateId,
    });

    if (result.affected === 0)
      throw new BadRequestException('Schedule template not found');
  }
}
