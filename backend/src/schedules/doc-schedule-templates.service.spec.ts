import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import {
  BadRequestException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { DocScheduleTemplate } from './entities/doc-schedule-template.entity';
import { DocScheduleTemplateSlot } from './entities/doc-schedule-template-slot.entity';
import { DocSchedule } from './entities/doc-schedule.entity';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';
import { ApplyDocScheduleTemplateDto } from './dtos/apply-doc-schedule-template.dto';
import { DataSource, EntityManager } from 'typeorm';
import { TokenUser } from '../auth/interfaces/token-user.interface';

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
    createQueryBuilder: jest.fn(),
    find: jest.fn(),
  };
  const docSchedulesRepositoryMock = {
    find: jest.fn(),
    save: jest.fn(),
  };
  const docScheduleSlotsRepositoryMock = {
    save: jest.fn(),
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
          provide: getRepositoryToken(DocSchedule),
          useValue: docSchedulesRepositoryMock,
        },
        {
          provide: getRepositoryToken(DocScheduleSlot),
          useValue: docScheduleSlotsRepositoryMock,
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

  describe('getAll', () => {
    const secretaryUser: TokenUser = {
      id: 1,
      email: 'secretary@test.com',
      role: 'secretary',
    };

    const mockTemplates = [
      {
        id: 1,
        name: 'Morning Schedule',
        doctorId: 10,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      {
        id: 2,
        name: 'Evening Schedule',
        doctorId: 10,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    ];

    const mockFullTemplates = [
      {
        id: 1,
        name: 'Morning Schedule',
        doctor: {
          userId: 10,
          user: { name: 'Dr. Smith' },
          specialty: 'Cardiology',
        },
        createdBy: { id: 1, name: 'Secretary' },
        slots: [
          { weekDay: 1, startTime: '09:00', endTime: '12:00' },
          { weekDay: 3, startTime: '09:00', endTime: '12:00' },
        ],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      {
        id: 2,
        name: 'Evening Schedule',
        doctor: {
          userId: 10,
          user: { name: 'Dr. Smith' },
          specialty: 'Cardiology',
        },
        createdBy: { id: 1, name: 'Secretary' },
        slots: [{ weekDay: 2, startTime: '14:00', endTime: '17:00' }],
        createdAt: new Date(),
        updatedAt: new Date(),
      },
    ];

    let queryBuilder: {
      addSelect: jest.Mock;
      where: jest.Mock;
      andWhere: jest.Mock;
      orderBy: jest.Mock;
      addOrderBy: jest.Mock;
      skip: jest.Mock;
      take: jest.Mock;
      getManyAndCount: jest.Mock;
    };

    beforeEach(() => {
      queryBuilder = {
        addSelect: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        orderBy: jest.fn().mockReturnThis(),
        addOrderBy: jest.fn().mockReturnThis(),
        skip: jest.fn().mockReturnThis(),
        take: jest.fn().mockReturnThis(),
        getManyAndCount: jest.fn().mockResolvedValue([mockTemplates, 2]),
      };

      scheduleTemplateRepositoryMock.createQueryBuilder = jest
        .fn()
        .mockReturnValue(queryBuilder);
      scheduleTemplateRepositoryMock.find = jest
        .fn()
        .mockResolvedValue(mockFullTemplates);
    });

    it('should return paginated templates for secretary', async () => {
      const result = await service.getAll(secretaryUser, 1, 10);

      expect(
        scheduleTemplateRepositoryMock.createQueryBuilder,
      ).toHaveBeenCalledWith('t');
      expect(queryBuilder.skip).toHaveBeenCalledWith(0);
      expect(queryBuilder.take).toHaveBeenCalledWith(10);
      expect(result.data).toHaveLength(2);
      expect(result.total).toBe(2);
      expect(result.data[0]).toHaveProperty('id');
      expect(result.data[0]).toHaveProperty('name');
      expect(result.data[0]).toHaveProperty('doctor');
      expect(result.data[0]).toHaveProperty('slots');
    });
  });

  describe('create', () => {
    const doctorId = 10;
    const secretaryId = 1;
    const doctorEntity: Doctor = {
      userId: doctorId,
    } as Doctor;

    const secretaryUser: TokenUser = {
      id: secretaryId,
      email: 'secretary@test.com',
      role: 'secretary',
    };

    const doctorUser: TokenUser = {
      id: doctorId,
      email: 'doctor@test.com',
      role: 'doctor',
    };

    const createDto: CreateDocScheduleTemplateDto = {
      name: 'Morning Schedule',
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

    it('should create schedule template by secretary for a doctor', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      scheduleTemplateRepositoryMock.save.mockResolvedValue({
        id: 1,
        name: createDto.name,
        doctor: doctorEntity,
      });

      const result = await service.create(doctorId, createDto, secretaryUser);

      expect(doctorRepositoryMock.findOneBy).toHaveBeenCalledWith({
        userId: doctorId,
      });
      expect(scheduleTemplateRepositoryMock.save).toHaveBeenCalled();
      expect(result).toBeDefined();
    });

    it('should create schedule template by doctor for themselves', async () => {
      scheduleTemplateRepositoryMock.save.mockResolvedValue({
        id: 1,
        name: createDto.name,
        doctor: new Doctor({ userId: doctorId }),
      });

      const result = await service.create(doctorId, createDto, doctorUser);

      expect(doctorRepositoryMock.findOneBy).not.toHaveBeenCalled();
      expect(scheduleTemplateRepositoryMock.save).toHaveBeenCalled();
      expect(result).toBeDefined();
    });

    it('should throw UnauthorizedException if doctor tries to create for another doctor', async () => {
      const otherDoctorUser: TokenUser = {
        id: 99,
        email: 'other@test.com',
        role: 'doctor',
      };

      await expect(
        service.create(doctorId, createDto, otherDoctorUser),
      ).rejects.toThrow(
        new UnauthorizedException(
          'Doctors can only create schedule templates for themselves',
        ),
      );
    });

    it('should throw BadRequestException if doctor not found', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(
        service.create(doctorId, createDto, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Doctor not found'));
    });

    it('should throw BadRequestException if slots have invalid time ranges', async () => {
      // Create overlapping slots to trigger validation error
      const invalidDto: CreateDocScheduleTemplateDto = {
        name: 'Invalid Schedule',
        slots: [
          { day: 1, startTime: '09:00', endTime: '12:00' },
          { day: 1, startTime: '11:00', endTime: '13:00' }, // Overlaps
        ],
      };

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);

      await expect(
        service.create(doctorId, invalidDto, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges in slots'),
      );
    });
  });

  describe('update', () => {
    const templateId = 1;
    const doctorId = 10;
    const secretaryId = 1;

    const doctorEntity: Doctor = {
      userId: doctorId,
    } as Doctor;

    const secretaryUser: TokenUser = {
      id: secretaryId,
      email: 'secretary@test.com',
      role: 'secretary',
    };

    const doctorUser: TokenUser = {
      id: doctorId,
      email: 'doctor@test.com',
      role: 'doctor',
    };

    const otherDoctorUser: TokenUser = {
      id: 99,
      email: 'other@test.com',
      role: 'doctor',
    };

    const existingTemplate: DocScheduleTemplate = {
      id: templateId,
      name: 'Old Schedule',
      doctor: doctorEntity,
      createdBy: { id: secretaryId } as User,
      slots: [],
      createdAt: new Date(),
      updatedAt: new Date(),
    } as DocScheduleTemplate;

    it('should update schedule template name by secretary', async () => {
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

      const result = await service.update(
        updateDto,
        templateId,
        doctorId,
        secretaryUser,
      );

      expect(dataSourceMock.transaction).toHaveBeenCalled();
      expect(templateRepoMock.findOne).toHaveBeenCalled();
      expect(templateRepoMock.save).toHaveBeenCalled();
      expect(result.name).toBe(updateDto.name);
    });

    it('should update schedule template by doctor who created it', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        name: 'Updated Schedule',
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

      const result = await service.update(
        updateDto,
        templateId,
        doctorId,
        doctorUser,
      );

      expect(templateRepoMock.save).toHaveBeenCalled();
      expect(result.name).toBe(updateDto.name);
    });

    it('should throw error if doctor tries to update another doctor template', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        name: 'Hacked Schedule',
      };

      const templateRepoMock = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
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

      await expect(
        service.update(updateDto, templateId, doctorId, otherDoctorUser),
      ).rejects.toThrow(
        new UnauthorizedException(
          'Doctors can only update their own schedule templates',
        ),
      );
    });

    it('should throw error if template not found', async () => {
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

      await expect(
        service.update(updateDto, templateId, doctorId, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Schedule template not found'));
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

      await service.update(updateDto, templateId, doctorId, secretaryUser);

      expect(mockSlotRepo.delete).toHaveBeenCalledWith({
        template: { id: templateId },
      });
      expect(mockSlotRepo.save).toHaveBeenCalled();
    });

    it('should throw error if updated slots have invalid time ranges', async () => {
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

      await expect(
        service.update(updateDto, templateId, doctorId, secretaryUser),
      ).rejects.toThrow(
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

      await service.update(updateDto, templateId, doctorId, secretaryUser);

      expect(mockTemplateRepo.save).toHaveBeenCalled();
      expect(mockSlotRepo.delete).toHaveBeenCalled();
    });
  });

  describe('delete', () => {
    const templateId = 1;
    const doctorId = 10;
    const secretaryId = 1;

    const secretaryUser: TokenUser = {
      id: secretaryId,
      email: 'secretary@test.com',
      role: 'secretary',
    };

    const doctorUser: TokenUser = {
      id: doctorId,
      email: 'doctor@test.com',
      role: 'doctor',
    };

    const otherDoctorUser: TokenUser = {
      id: 99,
      email: 'other@test.com',
      role: 'doctor',
    };

    const existingTemplate: DocScheduleTemplate = {
      id: templateId,
      doctor: { userId: doctorId } as Doctor,
    } as DocScheduleTemplate;

    it('should delete schedule template by secretary', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId, doctorId, secretaryUser);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
        doctor: { userId: doctorId },
      });
    });

    it('should delete schedule template by doctor who created it', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId, doctorId, doctorUser);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
        doctor: { userId: doctorId },
      });
    });

    it('should throw error if doctor tries to delete another doctor template', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );

      await expect(
        service.delete(templateId, doctorId, otherDoctorUser),
      ).rejects.toThrow(
        new UnauthorizedException(
          'Doctors can only delete their own schedule templates',
        ),
      );
    });

    it('should throw BadRequestException if template not found on fetch', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(null);

      await expect(
        service.delete(templateId, doctorId, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Schedule template not found'));
    });

    it('should throw BadRequestException if delete affects zero rows', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 0 });

      await expect(
        service.delete(templateId, doctorId, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Schedule template not found'));
    });

    it('should only delete single template even if query matches multiple', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId, doctorId, doctorUser);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
        doctor: { userId: doctorId },
      });
    });
  });

  describe('applyTemplate', () => {
    const templateId = 22;
    const doctorId = 10;
    const secretaryUser: TokenUser = {
      id: 1,
      email: 'secretary@test.com',
      role: 'secretary',
    };
    const doctorUser: TokenUser = {
      id: doctorId,
      email: 'doctor@test.com',
      role: 'doctor',
    };

    interface MockTemplateRepo {
      findOne: jest.Mock;
    }
    interface MockScheduleRepo {
      find: jest.Mock;
      save: jest.Mock;
    }
    interface MockSlotRepo {
      save: jest.Mock;
    }

    let templateRepo: MockTemplateRepo;
    let scheduleRepo: MockScheduleRepo;
    let slotRepo: MockSlotRepo;

    beforeEach(() => {
      templateRepo = { findOne: jest.fn() };
      scheduleRepo = { find: jest.fn(), save: jest.fn() };
      slotRepo = { save: jest.fn() };

      dataSourceMock.transaction.mockImplementation(
        async <T>(callback: (manager: EntityManager) => Promise<T>) => {
          const manager: Partial<EntityManager> = {
            getRepository: jest.fn().mockImplementation((entity) => {
              if (entity === DocScheduleTemplate) return templateRepo;
              if (entity === DocSchedule) return scheduleRepo;
              if (entity === DocScheduleSlot) return slotRepo;
              return {};
            }),
          };
          return callback(manager as EntityManager);
        },
      );
    });

    it('should throw UnauthorizedException when doctor applies template for another doctor', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-20',
        endDate: '2026-03-22',
      };

      await expect(
        service.applyTemplate(templateId, 99, dto, doctorUser),
      ).rejects.toThrow(
        new UnauthorizedException(
          'Doctors can only apply templates to their own schedules',
        ),
      );
    });

    it('should allow secretary to apply template for any doctor', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-23',
      };
      const weekDay = new Date(dto.startDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [{ weekDay, startTime: '09:00', endTime: '10:00' }],
      } as DocScheduleTemplate);
      scheduleRepo.find.mockResolvedValue([]);
      scheduleRepo.save.mockResolvedValue([{ dayDate: dto.startDate }]);
      slotRepo.save.mockResolvedValue([{ id: 1 }]);

      const result = await service.applyTemplate(
        templateId,
        doctorId,
        dto,
        secretaryUser,
      );

      expect(result).toHaveLength(1);
    });

    it('should allow doctor to apply template for themselves', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-23',
      };
      const weekDay = new Date(dto.startDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [{ weekDay, startTime: '14:00', endTime: '15:00' }],
      } as DocScheduleTemplate);
      scheduleRepo.find.mockResolvedValue([]);
      scheduleRepo.save.mockResolvedValue([{ dayDate: dto.startDate }]);
      slotRepo.save.mockResolvedValue([{ id: 1 }]);

      const result = await service.applyTemplate(
        templateId,
        doctorId,
        dto,
        doctorUser,
      );

      expect(result).toHaveLength(1);
    });

    it('should throw NotFoundException when template does not exist', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-20',
        endDate: '2026-03-22',
      };

      templateRepo.findOne.mockResolvedValue(null);

      await expect(
        service.applyTemplate(templateId, doctorId, dto, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Schedule template not found'));
    });

    it('should throw BadRequestException when startDate is after endDate', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-25',
        endDate: '2026-03-20',
      };

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        name: 'Test Template',
        doctor: { userId: doctorId } as Doctor,
        createdBy: { id: 1 } as User,
        slots: [],
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      await expect(
        service.applyTemplate(templateId, doctorId, dto, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('startDate must be before or equal to endDate'),
      );
    });

    it('should throw BadRequestException when overlapping slots detected', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-23',
      };
      const weekDay = new Date(dto.startDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [{ weekDay, startTime: '09:00', endTime: '11:00' }],
      } as DocScheduleTemplate);

      scheduleRepo.find.mockResolvedValue([
        {
          dayDate: dto.startDate,
          slots: [{ startTime: '10:00', endTime: '12:00' }],
        },
      ]);

      await expect(
        service.applyTemplate(templateId, doctorId, dto, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException(
          'Invalid or overlapping time ranges detected when applying template',
        ),
      );
    });

    it('should create new schedules for days that do not exist', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-24',
      };
      const weekDay1 = new Date(dto.startDate).getDay();
      const weekDay2 = new Date(dto.endDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [
          { weekDay: weekDay1, startTime: '09:00', endTime: '10:00' },
          { weekDay: weekDay2, startTime: '11:00', endTime: '12:00' },
        ],
      } as DocScheduleTemplate);

      scheduleRepo.find.mockResolvedValue([]);
      scheduleRepo.save.mockImplementation((schedules: DocSchedule[]) =>
        Promise.resolve(schedules),
      );
      slotRepo.save.mockResolvedValue([{ id: 1 }, { id: 2 }]);

      await service.applyTemplate(templateId, doctorId, dto, secretaryUser);

      expect(scheduleRepo.save).toHaveBeenCalledWith(
        expect.arrayContaining([
          expect.objectContaining({ dayDate: dto.startDate }),
          expect.objectContaining({ dayDate: dto.endDate }),
        ]),
      );
    });

    it('should not create schedules for days that already exist', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-23',
      };
      const weekDay = new Date(dto.startDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [{ weekDay, startTime: '09:00', endTime: '10:00' }],
      } as DocScheduleTemplate);

      const existingSchedule = {
        dayDate: dto.startDate,
        slots: [],
      };
      scheduleRepo.find.mockResolvedValue([existingSchedule]);
      slotRepo.save.mockResolvedValue([{ id: 1 }]);

      await service.applyTemplate(templateId, doctorId, dto, secretaryUser);

      expect(scheduleRepo.save).not.toHaveBeenCalled();
    });

    it('should only apply slots for matching weekdays within date range', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-25',
      };
      const weekDay23 = new Date('2026-03-23').getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        name: 'Test Template',
        doctor: { userId: doctorId } as Doctor,
        createdBy: { id: 1 } as User,
        slots: [{ weekDay: weekDay23, startTime: '09:00', endTime: '10:00' }],
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      scheduleRepo.find.mockResolvedValue([]);
      scheduleRepo.save.mockImplementation((schedules: DocSchedule[]) =>
        Promise.resolve(schedules),
      );
      slotRepo.save.mockResolvedValue([{ id: 1 }]);

      await service.applyTemplate(templateId, doctorId, dto, secretaryUser);

      expect(slotRepo.save).toHaveBeenCalledWith(
        expect.arrayContaining([
          expect.objectContaining({ startTime: '09:00', endTime: '10:00' }),
        ]),
      );
      expect(slotRepo.save).toHaveBeenCalledWith(expect.any(Array));
      expect(scheduleRepo.save).toHaveBeenCalledWith(
        expect.arrayContaining([
          expect.objectContaining({ dayDate: dto.startDate }),
        ]),
      );
    });

    it('should return saved slots from the repository', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-23',
      };
      const weekDay = new Date(dto.startDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [{ weekDay, startTime: '09:00', endTime: '10:00' }],
      } as DocScheduleTemplate);

      scheduleRepo.find.mockResolvedValue([]);
      scheduleRepo.save.mockImplementation((schedules: DocSchedule[]) =>
        Promise.resolve(schedules),
      );
      const savedSlots = [{ id: 100, startTime: '09:00', endTime: '10:00' }];
      slotRepo.save.mockResolvedValue(savedSlots);

      const result = await service.applyTemplate(
        templateId,
        doctorId,
        dto,
        secretaryUser,
      );

      expect(result).toEqual(savedSlots);
    });

    it('should handle empty template slots gracefully', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-25',
      };

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        name: 'Empty Template',
        doctor: { userId: doctorId } as Doctor,
        createdBy: { id: 1 } as User,
        slots: [],
        createdAt: new Date(),
        updatedAt: new Date(),
      });

      scheduleRepo.find.mockResolvedValue([]);
      slotRepo.save.mockResolvedValue([]);

      const result = await service.applyTemplate(
        templateId,
        doctorId,
        dto,
        secretaryUser,
      );

      expect(scheduleRepo.save).not.toHaveBeenCalled();
      expect(slotRepo.save).toHaveBeenCalledWith([]);
      expect(result).toEqual([]);
    });

    it('should apply same startDate and endDate correctly', async () => {
      const dto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-03-23',
        endDate: '2026-03-23',
      };
      const weekDay = new Date(dto.startDate).getDay();

      templateRepo.findOne.mockResolvedValue({
        id: templateId,
        doctor: { userId: doctorId } as Doctor,
        slots: [{ weekDay, startTime: '08:00', endTime: '09:00' }],
      } as DocScheduleTemplate);

      scheduleRepo.find.mockResolvedValue([]);
      scheduleRepo.save.mockImplementation((schedules: DocSchedule[]) =>
        Promise.resolve(schedules),
      );
      slotRepo.save.mockResolvedValue([{ id: 1 }]);

      const result = await service.applyTemplate(
        templateId,
        doctorId,
        dto,
        secretaryUser,
      );

      expect(result).toHaveLength(1);
      expect(scheduleRepo.save).toHaveBeenCalledWith([
        expect.objectContaining({ dayDate: dto.startDate }),
      ]);
    });
  });
});
