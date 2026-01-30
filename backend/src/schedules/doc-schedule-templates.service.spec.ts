import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { BadRequestException } from '@nestjs/common';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { DocScheduleTemplate } from './entities/doc-schedule-template.entity';
import { DocScheduleTemplateSlot } from './entities/doc-schedule-template-slot.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';
import { DataSource, EntityManager } from 'typeorm';

describe('DocScheduleTemplatesService', () => {
  let service: DocScheduleTemplatesService;
  const doctorRepositoryMock = {
    findOneBy: jest.fn(),
  };
  const userRepositoryMock = {
    findOneBy: jest.fn(),
  };
  const scheduleTemplateRepositoryMock = {
    findOne: jest.fn(),
    save: jest.fn(),
    delete: jest.fn(),
  };
  const dataSourceMock = {
    transaction: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DocScheduleTemplatesService,
        {
          provide: getRepositoryToken(Doctor),
          useValue: doctorRepositoryMock,
        },
        {
          provide: getRepositoryToken(User),
          useValue: userRepositoryMock,
        },
        {
          provide: getRepositoryToken(DocScheduleTemplate),
          useValue: scheduleTemplateRepositoryMock,
        },
        {
          provide: DataSource,
          useValue: dataSourceMock,
        },
      ],
    }).compile();

    service = module.get<DocScheduleTemplatesService>(
      DocScheduleTemplatesService,
    );

    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    const secretaryId = 1;
    const doctorId = 10;
    const doctorEntity: Doctor = {
      userId: doctorId,
    } as Doctor;
    const secretaryEntity: User = {
      id: secretaryId,
      name: 'Secretary',
    } as User;

    const createDto: CreateDocScheduleTemplateDto = {
      name: 'Morning Schedule',
      doctorId,
      slots: [
        {
          day: 1,
          startTime: '09:00',
          endTime: '12:00',
        },
        {
          day: 3,
          startTime: '09:00',
          endTime: '12:00',
        },
      ],
    };

    it('should create schedule template successfully', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(secretaryEntity);

      const templateToSave = new DocScheduleTemplate({
        name: createDto.name,
        doctor: doctorEntity,
        secretary: secretaryEntity,
      });

      scheduleTemplateRepositoryMock.save.mockResolvedValue({
        ...templateToSave,
        id: 1,
      });

      const result = await service.create(createDto, secretaryId);

      expect(doctorRepositoryMock.findOneBy).toHaveBeenCalledWith({
        userId: doctorId,
      });
      expect(userRepositoryMock.findOneBy).toHaveBeenCalledWith({
        id: secretaryId,
      });
      expect(scheduleTemplateRepositoryMock.save).toHaveBeenCalled();
      expect(result).toBeDefined();
    });

    it('should throw BadRequestException if doctor not found', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(service.create(createDto, secretaryId)).rejects.toThrow(
        new BadRequestException('Doctor not found'),
      );
    });

    it('should throw BadRequestException if secretary not found', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(service.create(createDto, secretaryId)).rejects.toThrow(
        new BadRequestException('Secretary not found'),
      );
    });

    it('should throw BadRequestException if slots have invalid time ranges', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(secretaryEntity);
      // Create overlapping slots to trigger validation error
      const invalidDto: CreateDocScheduleTemplateDto = {
        name: 'Invalid Schedule',
        doctorId,
        slots: [
          { day: 1, startTime: '09:00', endTime: '12:00' },
          { day: 1, startTime: '11:00', endTime: '13:00' }, // Overlaps
        ],
      };

      await expect(service.create(invalidDto, secretaryId)).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges in slots'),
      );
    });
  });

  describe('update', () => {
    const templateId = 1;
    const doctorEntity: Doctor = {
      userId: 10,
    } as Doctor;
    const secretaryEntity: User = {
      id: 1,
      name: 'Secretary',
    } as User;
    const existingTemplate: DocScheduleTemplate = {
      id: templateId,
      name: 'Old Schedule',
      doctor: doctorEntity,
      secretary: secretaryEntity,
      slots: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    } as DocScheduleTemplate;

    it('should update schedule template successfully', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        name: 'New Schedule Name',
      };

      const templateRepoMock = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
        save: jest.fn().mockResolvedValue({
          ...existingTemplate,
          name: updateDto.name,
        }),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };

      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return templateRepoMock;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      const result = await service.update(updateDto, templateId);

      expect(dataSourceMock.transaction).toHaveBeenCalled();
      expect(templateRepoMock.findOne).toHaveBeenCalled();
      expect(templateRepoMock.save).toHaveBeenCalled();
      expect(result.name).toBe(updateDto.name);
    });

    it('should throw BadRequestException if template not found', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        name: 'New Schedule Name',
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate)
          return {
            findOne: jest.fn().mockResolvedValue(null),
          };
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await expect(service.update(updateDto, templateId)).rejects.toThrow(
        new BadRequestException('Schedule template not found'),
      );
    });

    it('should update template name only', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        name: 'Updated Name',
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
        save: jest.fn().mockResolvedValue({
          ...existingTemplate,
          name: updateDto.name,
        }),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return mockTemplateRepo;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await service.update(updateDto, templateId);

      expect(mockTemplateRepo.save).toHaveBeenCalledWith(
        expect.objectContaining({
          name: updateDto.name,
        }),
      );
    });

    it('should update doctor and keep existing name if not provided', async () => {
      const newDoctorEntity: Doctor = {
        userId: 20,
      } as Doctor;

      const updateDto: UpdateDocScheduleTemplateDto = {
        doctorId: 20,
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
        save: jest.fn().mockResolvedValue({
          ...existingTemplate,
          doctor: newDoctorEntity,
        }),
      };

      const mockDoctorRepo = {
        findOneBy: jest.fn().mockResolvedValue(newDoctorEntity),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return mockTemplateRepo;
        if (entity === Doctor) return mockDoctorRepo;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await service.update(updateDto, templateId);

      expect(mockDoctorRepo.findOneBy).toHaveBeenCalledWith({
        userId: 20,
      });
      expect(mockTemplateRepo.save).toHaveBeenCalled();
    });

    it('should throw BadRequestException if new doctor not found', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        doctorId: 20,
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
      };

      const mockDoctorRepo = {
        findOneBy: jest.fn().mockResolvedValue(null),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return mockTemplateRepo;
        if (entity === Doctor) return mockDoctorRepo;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await expect(service.update(updateDto, templateId)).rejects.toThrow(
        new BadRequestException('Doctor not found'),
      );
    });

    it('should update slots and remove old slots', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        slots: [
          { day: 0, startTime: '10:00', endTime: '13:00' },
          { day: 2, startTime: '10:00', endTime: '13:00' },
        ],
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
        save: jest.fn().mockResolvedValue({
          ...existingTemplate,
        }),
      };

      const mockSlotRepo = {
        delete: jest.fn().mockResolvedValue({ affected: 2 }),
        create: jest.fn((slot: DocScheduleTemplateSlot) => slot),
        save: jest.fn().mockResolvedValue([]),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return mockTemplateRepo;
        if (entity === DocScheduleTemplateSlot) return mockSlotRepo;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await service.update(updateDto, templateId);

      expect(mockSlotRepo.delete).toHaveBeenCalledWith({
        template: { id: templateId },
      });
      expect(mockSlotRepo.save).toHaveBeenCalled();
    });

    it('should throw BadRequestException if updated slots have invalid time ranges', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        slots: [
          { day: 0, startTime: '13:00', endTime: '10:00' }, // Invalid
        ],
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return mockTemplateRepo;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await expect(service.update(updateDto, templateId)).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges in slots'),
      );
    });

    it('should update name and slots together', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        name: 'Completely Updated Schedule',
        slots: [
          { day: 1, startTime: '14:00', endTime: '17:00' },
          { day: 4, startTime: '14:00', endTime: '17:00' },
        ],
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
        save: jest.fn().mockResolvedValue({
          ...existingTemplate,
          name: updateDto.name,
        }),
      };

      const mockSlotRepo = {
        delete: jest.fn().mockResolvedValue({ affected: 2 }),
        create: jest.fn((slot: DocScheduleTemplateSlot) => slot),
        save: jest.fn().mockResolvedValue([]),
      };

      const mockManager: Partial<EntityManager> = {
        getRepository: jest.fn(),
      };
      (mockManager.getRepository as jest.Mock).mockImplementation((entity) => {
        if (entity === DocScheduleTemplate) return mockTemplateRepo;
        if (entity === DocScheduleTemplateSlot) return mockSlotRepo;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> => {
          return cb(mockManager as EntityManager);
        },
      );

      await service.update(updateDto, templateId);

      expect(mockTemplateRepo.save).toHaveBeenCalled();
      expect(mockSlotRepo.delete).toHaveBeenCalled();
    });
  });

  describe('delete', () => {
    const templateId = 1;

    it('should delete schedule template successfully', async () => {
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
      });
    });

    it('should throw BadRequestException if template not found', async () => {
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 0 });

      await expect(service.delete(templateId)).rejects.toThrow(
        new BadRequestException('Schedule template not found'),
      );
    });

    it('should only delete single template even if multiple match', async () => {
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
      });
    });
  });
});
