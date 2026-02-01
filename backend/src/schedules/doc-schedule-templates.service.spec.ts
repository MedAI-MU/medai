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

  describe('getAll', () => {
    const secretaryUser: TokenUser = {
      id: 1,
      email: 'secretary@test.com',
      role: 'secretary',
    };

    const doctorUser: TokenUser = {
      id: 10,
      email: 'doctor@test.com',
      role: 'doctor',
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

    it('should filter by doctorId when provided for secretary', async () => {
      await service.getAll(secretaryUser, 1, 10, undefined, 10);

      expect(queryBuilder.andWhere).toHaveBeenCalledWith(
        't.doctorId = :doctorId',
        { doctorId: 10 },
      );
    });

    it('should automatically set doctorId for doctor user', async () => {
      await service.getAll(doctorUser, 1, 10);

      expect(queryBuilder.andWhere).toHaveBeenCalledWith(
        't.doctorId = :doctorId',
        { doctorId: 10 },
      );
    });

    it('should filter by name when provided', async () => {
      await service.getAll(secretaryUser, 1, 10, 'Morning');

      expect(queryBuilder.addSelect).toHaveBeenCalledWith(
        'similarity(t.name, :name)',
        'similarity',
      );
      expect(queryBuilder.where).toHaveBeenCalledWith('t.name ILIKE :name', {
        name: '%Morning%',
      });
      expect(queryBuilder.orderBy).toHaveBeenCalledWith('similarity', 'DESC');
    });

    it('should apply pagination correctly for page 2', async () => {
      await service.getAll(secretaryUser, 2, 5);

      expect(queryBuilder.skip).toHaveBeenCalledWith(5);
      expect(queryBuilder.take).toHaveBeenCalledWith(5);
    });

    it('should format response correctly with nested objects', async () => {
      const result = await service.getAll(secretaryUser, 1, 10);

      expect(result.data[0].doctor).toEqual({
        id: 10,
        name: 'Dr. Smith',
        specialty: 'Cardiology',
      });
      expect(result.data[0].createdBy).toEqual({
        id: 1,
        name: 'Secretary',
      });
      expect(result.data[0].slots).toEqual([
        { weekDay: 1, startTime: '09:00', endTime: '12:00' },
        { weekDay: 3, startTime: '09:00', endTime: '12:00' },
      ]);
    });

    it('should load templates with all relations', async () => {
      await service.getAll(secretaryUser, 1, 10);

      expect(scheduleTemplateRepositoryMock.find).toHaveBeenCalledWith(
        expect.objectContaining({
          relations: {
            doctor: true,
            createdBy: true,
            slots: true,
          },
        }),
      );
    });

    it('should order results by createdAt DESC and slots by weekDay and startTime', async () => {
      await service.getAll(secretaryUser, 1, 10);

      expect(scheduleTemplateRepositoryMock.find).toHaveBeenCalledWith(
        expect.objectContaining({
          order: {
            createdAt: 'DESC',
            slots: { weekDay: 'ASC', startTime: 'ASC' },
          },
        }),
      );
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

    it('should create schedule template by secretary for a doctor', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      scheduleTemplateRepositoryMock.save.mockResolvedValue({
        id: 1,
        name: createDto.name,
        doctor: doctorEntity,
      });

      const result = await service.create(createDto, secretaryUser);

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

      const result = await service.create(
        { ...createDto, doctorId },
        doctorUser,
      );

      expect(doctorRepositoryMock.findOneBy).not.toHaveBeenCalled();
      expect(scheduleTemplateRepositoryMock.save).toHaveBeenCalled();
      expect(result).toBeDefined();
    });

    it('should throw BadRequestException if doctor tries to create for another doctor', async () => {
      const otherDoctorUser: TokenUser = {
        id: 99,
        email: 'other@test.com',
        role: 'doctor',
      };

      await expect(service.create(createDto, otherDoctorUser)).rejects.toThrow(
        new BadRequestException(
          'Doctors can only create schedule templates for themselves',
        ),
      );
    });

    it('should throw BadRequestException if doctor not found', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(service.create(createDto, secretaryUser)).rejects.toThrow(
        new BadRequestException('Doctor not found'),
      );
    });

    it('should throw BadRequestException if slots have invalid time ranges', async () => {
      // Create overlapping slots to trigger validation error
      const invalidDto: CreateDocScheduleTemplateDto = {
        name: 'Invalid Schedule',
        doctorId,
        slots: [
          { day: 1, startTime: '09:00', endTime: '12:00' },
          { day: 1, startTime: '11:00', endTime: '13:00' }, // Overlaps
        ],
      };

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);

      await expect(service.create(invalidDto, secretaryUser)).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges in slots'),
      );
    });
  });

  describe('update', () => {
    const templateId = 1;
    const doctorId = 10;
    const doctorId2 = 20;
    const secretaryId = 1;

    const doctorEntity: Doctor = {
      userId: doctorId,
    } as Doctor;

    const doctorEntity2: Doctor = {
      userId: doctorId2,
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

      const result = await service.update(updateDto, templateId, secretaryUser);

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

      const result = await service.update(updateDto, templateId, doctorUser);

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
        service.update(updateDto, templateId, otherDoctorUser),
      ).rejects.toThrow(
        new BadRequestException(
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
        service.update(updateDto, templateId, secretaryUser),
      ).rejects.toThrow(new BadRequestException('Schedule template not found'));
    });

    it('should throw error if doctor tries to change doctorId', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        doctorId: doctorId2,
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
        service.update(updateDto, templateId, doctorUser),
      ).rejects.toThrow(
        new BadRequestException(
          'Doctors cannot change the doctor of a schedule template',
        ),
      );
    });

    it('should allow secretary to change doctorId', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        doctorId: doctorId2,
      };

      const mockTemplateRepo = {
        findOne: jest.fn().mockResolvedValue(existingTemplate),
        save: jest.fn().mockResolvedValue({
          ...existingTemplate,
          doctor: doctorEntity2,
        }),
      };

      const mockDoctorRepo = {
        findOneBy: jest.fn().mockResolvedValue(doctorEntity2),
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

      await service.update(updateDto, templateId, secretaryUser);

      expect(mockDoctorRepo.findOneBy).toHaveBeenCalledWith({
        userId: doctorId2,
      });
      expect(mockTemplateRepo.save).toHaveBeenCalled();
    });

    it('should throw error if new doctor not found', async () => {
      const updateDto: UpdateDocScheduleTemplateDto = {
        doctorId: doctorId2,
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

      await expect(
        service.update(updateDto, templateId, secretaryUser),
      ).rejects.toThrow(new BadRequestException('Doctor not found'));
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

      await service.update(updateDto, templateId, secretaryUser);

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
        service.update(updateDto, templateId, secretaryUser),
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

      await service.update(updateDto, templateId, secretaryUser);

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

      await service.delete(templateId, secretaryUser);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
      });
    });

    it('should delete schedule template by doctor who created it', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId, doctorUser);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
      });
    });

    it('should throw error if doctor tries to delete another doctor template', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );

      await expect(service.delete(templateId, otherDoctorUser)).rejects.toThrow(
        new BadRequestException(
          'Doctors can only delete their own schedule templates',
        ),
      );
    });

    it('should throw BadRequestException if template not found on fetch', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(null);

      await expect(service.delete(templateId, secretaryUser)).rejects.toThrow(
        new BadRequestException('Schedule template not found'),
      );
    });

    it('should throw BadRequestException if delete affects zero rows', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 0 });

      await expect(service.delete(templateId, secretaryUser)).rejects.toThrow(
        new BadRequestException('Schedule template not found'),
      );
    });

    it('should only delete single template even if query matches multiple', async () => {
      scheduleTemplateRepositoryMock.findOne.mockResolvedValue(
        existingTemplate,
      );
      scheduleTemplateRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(templateId, secretaryUser);

      expect(scheduleTemplateRepositoryMock.delete).toHaveBeenCalledWith({
        id: templateId,
      });
    });
  });
});
