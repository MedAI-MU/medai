import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import {
  BadRequestException,
  NotFoundException,
  UnauthorizedException,
} from '@nestjs/common';
import { DocScheduleSlotsService } from './doc-schedule-slots.service';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { DocSchedule } from './entities/doc-schedule.entity';

describe('DocScheduleSlotsService', () => {
  let service: DocScheduleSlotsService;
  const doctorRepositoryMock = {
    findOneBy: jest.fn(),
  };
  const userRepositoryMock = {
    findOneBy: jest.fn(),
  };
  const scheduleRepositoryMock = {
    findAndCount: jest.fn(),
    find: jest.fn(),
    findOne: jest.fn(),
    save: jest.fn(),
  };
  const scheduleSlotRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    findOneBy: jest.fn(),
    save: jest.fn(),
    delete: jest.fn(),
    createQueryBuilder: jest.fn(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DocScheduleSlotsService,
        {
          provide: getRepositoryToken(Doctor),
          useValue: doctorRepositoryMock,
        },
        {
          provide: getRepositoryToken(User),
          useValue: userRepositoryMock,
        },
        {
          provide: getRepositoryToken(DocSchedule),
          useValue: scheduleRepositoryMock,
        },
        {
          provide: getRepositoryToken(DocScheduleSlot),
          useValue: scheduleSlotRepositoryMock,
        },
      ],
    }).compile();

    service = module.get<DocScheduleSlotsService>(DocScheduleSlotsService);

    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    const secretaryUser: TokenUser = {
      id: 1,
      email: 'secretary@test.com',
      role: 'secretary',
      status: 'approved',
    };
    const doctorUser: TokenUser = {
      id: 10,
      email: 'doctor@test.com',
      role: 'doctor',
      status: 'approved',
    };
    const anotherDoctorUser: TokenUser = {
      id: 20,
      email: 'doctor2@test.com',
      role: 'doctor',
      status: 'approved',
    };
    const doctorId = 10;
    const doctorEntity: Doctor = {
      userId: doctorId,
    } as Doctor;

    const createDto: CreateDocScheduleDto = {
      days: [
        {
          date: '2024-01-15',
          slots: [
            {
              startTime: '09:00',
              endTime: '10:00',
            },
            {
              startTime: '11:00',
              endTime: '12:00',
            },
          ],
        },
      ],
    };

    it('should create schedule slots successfully as secretary', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      scheduleRepositoryMock.find.mockResolvedValue([]);

      const mockSchedule = {
        id: 1,
        doctor: doctorEntity,
        dayDate: '2024-01-15',
        createdBy: new User({ id: secretaryUser.id }),
      } as DocSchedule;

      scheduleRepositoryMock.save.mockResolvedValue([mockSchedule]);

      const slotsToSave = createDto.days[0].slots.map(
        (slot) =>
          new DocScheduleSlot({
            schedule: mockSchedule,
            startTime: slot.startTime,
            endTime: slot.endTime,
          }),
      );

      scheduleSlotRepositoryMock.save.mockResolvedValue(slotsToSave);

      const result = await service.create(doctorId, createDto, secretaryUser);

      expect(doctorRepositoryMock.findOneBy).toHaveBeenCalledWith({
        userId: doctorId,
      });
      expect(result).toEqual(slotsToSave);
    });

    it('should create schedule slots successfully as doctor for themselves', async () => {
      scheduleRepositoryMock.find.mockResolvedValue([]);

      const mockSchedule = {
        id: 1,
        doctor: new Doctor({ userId: doctorUser.id }),
        dayDate: '2024-01-15',
        createdBy: new User({ id: doctorUser.id }),
      } as DocSchedule;

      scheduleRepositoryMock.save.mockResolvedValue([mockSchedule]);

      const slotsToSave = createDto.days[0].slots.map(
        (slot) =>
          new DocScheduleSlot({
            schedule: mockSchedule,
            startTime: slot.startTime,
            endTime: slot.endTime,
          }),
      );

      scheduleSlotRepositoryMock.save.mockResolvedValue(slotsToSave);

      const result = await service.create(doctorId, createDto, doctorUser);

      expect(result).toEqual(slotsToSave);
    });

    it('should throw UnauthorizedException when doctor tries to create schedule for another doctor', async () => {
      await expect(
        service.create(doctorId, createDto, anotherDoctorUser),
      ).rejects.toThrow(
        new UnauthorizedException(
          'Doctors can only create schedules for themselves',
        ),
      );
    });

    it('should throw NotFoundException if doctor not found (secretary)', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(
        service.create(doctorId, createDto, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Doctor not found'));
    });

    it('should throw BadRequestException if slots have invalid time ranges', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      // Create overlapping slots to trigger validation error
      const invalidDto: CreateDocScheduleDto = {
        days: [
          {
            date: '2024-01-15',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
              {
                startTime: '09:30',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      scheduleRepositoryMock.find.mockResolvedValue([]);

      await expect(
        service.create(doctorId, invalidDto, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should throw BadRequestException if new slots overlap with existing slots', async () => {
      const mockSchedule = {
        id: 1,
        doctor: doctorEntity,
        dayDate: '2024-01-15',
        slots: [
          {
            id: 1,
            startTime: '09:00',
            endTime: '10:30',
            status: 'available',
          } as DocScheduleSlot,
        ],
      } as DocSchedule;

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      scheduleRepositoryMock.find.mockResolvedValue([mockSchedule]);

      await expect(
        service.create(doctorId, createDto, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should handle multiple days correctly', async () => {
      const multiDayDto: CreateDocScheduleDto = {
        days: [
          {
            date: '2024-01-15',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
          {
            date: '2024-01-16',
            slots: [
              {
                startTime: '14:00',
                endTime: '15:00',
              },
            ],
          },
        ],
      };

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      scheduleRepositoryMock.find.mockResolvedValue([]);
      scheduleRepositoryMock.save.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue([]);

      await service.create(doctorId, multiDayDto, secretaryUser);

      expect(scheduleRepositoryMock.find).toHaveBeenCalled();
    });

    it('should handle empty slots array', async () => {
      const emptyDto: CreateDocScheduleDto = {
        days: [],
      };

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      scheduleRepositoryMock.find.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue([]);

      await service.create(doctorId, emptyDto, secretaryUser);

      expect(scheduleSlotRepositoryMock.save).toHaveBeenCalled();
    });
  });

  describe('update', () => {
    const slotId = 1;
    const secretaryUser: TokenUser = {
      id: 1,
      email: 'secretary@test.com',
      role: 'secretary',
      status: 'approved',
    };
    const doctorUser: TokenUser = {
      id: 10,
      email: 'doctor@test.com',
      role: 'doctor',
      status: 'approved',
    };
    const anotherDoctorUser: TokenUser = {
      id: 20,
      email: 'doctor2@test.com',
      role: 'doctor',
      status: 'approved',
    };
    const doctorEntity: Doctor = {
      userId: 10,
    } as Doctor;
    const mockSchedule: DocSchedule = {
      id: 1,
      doctor: doctorEntity,
      dayDate: '2024-01-15',
    } as DocSchedule;
    const existingSlot: DocScheduleSlot = {
      id: slotId,
      schedule: mockSchedule,
      startTime: '09:00',
      endTime: '10:00',
      status: 'available',
    } as DocScheduleSlot;

    it('should update schedule slot successfully as secretary', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:00',
        endTime: '11:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      const scheduleWithSlots = {
        ...mockSchedule,
        slots: [existingSlot],
      };
      scheduleRepositoryMock.findOne.mockResolvedValue(scheduleWithSlots);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...existingSlot,
        startTime: updateDto.startTime,
        endTime: updateDto.endTime,
      });

      const result = await service.update(
        updateDto,
        slotId,
        doctorEntity.userId,
        secretaryUser,
      );

      expect(scheduleSlotRepositoryMock.findOne).toHaveBeenCalledWith({
        where: {
          id: slotId,
          schedule: { doctor: { userId: doctorEntity.userId } },
        },
        relations: ['schedule', 'schedule.doctor'],
      });
      expect(result.startTime).toBe(updateDto.startTime);
      expect(result.endTime).toBe(updateDto.endTime);
    });

    it('should update schedule slot successfully as doctor for their own slot', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:00',
        endTime: '11:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      const scheduleWithSlots = {
        ...mockSchedule,
        slots: [existingSlot],
      };
      scheduleRepositoryMock.findOne.mockResolvedValue(scheduleWithSlots);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...existingSlot,
        startTime: updateDto.startTime,
        endTime: updateDto.endTime,
      });

      const result = await service.update(
        updateDto,
        slotId,
        doctorEntity.userId,
        doctorUser,
      );

      expect(scheduleSlotRepositoryMock.findOne).toHaveBeenCalledWith({
        where: {
          id: slotId,
          schedule: { doctor: { userId: doctorEntity.userId } },
        },
        relations: ['schedule', 'schedule.doctor'],
      });
      expect(result.startTime).toBe(updateDto.startTime);
      expect(result.endTime).toBe(updateDto.endTime);
    });

    it('should throw UnauthorizedException when doctor tries to update another doctors slot', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:00',
        endTime: '11:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);

      await expect(
        service.update(
          updateDto,
          slotId,
          doctorEntity.userId,
          anotherDoctorUser,
        ),
      ).rejects.toThrow(
        new UnauthorizedException(
          'Doctors can only update their own schedule slots',
        ),
      );
    });

    it('should throw NotFoundException if slot not found', async () => {
      scheduleSlotRepositoryMock.findOne.mockResolvedValue(null);

      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:00',
      };

      await expect(
        service.update(updateDto, slotId, doctorEntity.userId, secretaryUser),
      ).rejects.toThrow(new NotFoundException('Schedule slot not found'));
    });

    it('should throw BadRequestException if updated time ranges are invalid', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '11:00',
        endTime: '10:00', // Invalid: end time is before start time
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      scheduleSlotRepositoryMock.find.mockResolvedValue([existingSlot]);

      await expect(
        service.update(updateDto, slotId, doctorEntity.userId, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should update only startTime when less than endTime', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '08:00',
      };

      const slotWithSeconds = {
        ...existingSlot,
        startTime: '09:00:00',
        endTime: '10:00:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(slotWithSeconds);
      const scheduleWithSlots = {
        ...mockSchedule,
        slots: [slotWithSeconds],
      };
      scheduleRepositoryMock.findOne.mockResolvedValue(scheduleWithSlots);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...slotWithSeconds,
        startTime: updateDto.startTime,
      });

      const result = await service.update(
        updateDto,
        slotId,
        doctorEntity.userId,
        secretaryUser,
      );

      expect(result.startTime).toBe(updateDto.startTime);
      expect(result.endTime).toBe(slotWithSeconds.endTime);
    });

    it('should throw BadRequestException when updating only startTime to be greater than endTime', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '11:00',
      };

      const slotWithSeconds = {
        ...existingSlot,
        startTime: '09:00:00',
        endTime: '10:00:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(slotWithSeconds);
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);

      await expect(
        service.update(updateDto, slotId, doctorEntity.userId, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should update only endTime when greater than startTime', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        endTime: '11:00',
      };

      const slotWithSeconds = {
        ...existingSlot,
        startTime: '09:00:00',
        endTime: '10:00:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(slotWithSeconds);
      const scheduleWithSlots = {
        ...mockSchedule,
        slots: [slotWithSeconds],
      };
      scheduleRepositoryMock.findOne.mockResolvedValue(scheduleWithSlots);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...slotWithSeconds,
        endTime: updateDto.endTime,
      });

      const result = await service.update(
        updateDto,
        slotId,
        doctorEntity.userId,
        secretaryUser,
      );

      expect(result.startTime).toBe(slotWithSeconds.startTime);
      expect(result.endTime).toBe(updateDto.endTime);
    });

    it('should throw BadRequestException when updating only endTime to be less than startTime', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        endTime: '08:00',
      };

      const slotWithSeconds = {
        ...existingSlot,
        startTime: '09:00:00',
        endTime: '10:00:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(slotWithSeconds);
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);

      await expect(
        service.update(updateDto, slotId, doctorEntity.userId, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should update day if provided', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        day: '2024-01-16',
      };

      const slotWithSeconds = {
        ...existingSlot,
        startTime: '09:00:00',
        endTime: '10:00:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(slotWithSeconds);
      scheduleRepositoryMock.findOne.mockResolvedValue(null);
      const newSchedule = {
        ...mockSchedule,
        dayDate: updateDto.day,
      };
      scheduleRepositoryMock.save.mockResolvedValue(newSchedule);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...slotWithSeconds,
        schedule: newSchedule,
      });

      const result = await service.update(
        updateDto,
        slotId,
        doctorEntity.userId,
        secretaryUser,
      );

      expect(result.schedule.dayDate).toBe(updateDto.day);
    });

    it('should update successfully with existing slots and no overlap', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '13:00',
        endTime: '14:00',
      };

      const otherSlot: DocScheduleSlot = {
        id: 2,
        schedule: mockSchedule,
        startTime: '10:00:00',
        endTime: '11:00:00',
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      const scheduleWithSlots = {
        ...mockSchedule,
        slots: [existingSlot, otherSlot],
      };
      scheduleRepositoryMock.findOne.mockResolvedValue(scheduleWithSlots);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...existingSlot,
        startTime: updateDto.startTime,
        endTime: updateDto.endTime,
      });

      const result = await service.update(
        updateDto,
        slotId,
        doctorEntity.userId,
        secretaryUser,
      );

      expect(scheduleSlotRepositoryMock.save).toHaveBeenCalled();
      expect(result.startTime).toBe(updateDto.startTime);
      expect(result.endTime).toBe(updateDto.endTime);
    });

    it('should throw BadRequestException when updating with existing slots that cause overlap', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:30',
        endTime: '11:30',
      };

      const otherSlot: DocScheduleSlot = {
        id: 2,
        schedule: mockSchedule,
        startTime: '11:00:00',
        endTime: '12:00:00',
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      const scheduleWithSlots = {
        ...mockSchedule,
        slots: [existingSlot, otherSlot],
      };
      scheduleRepositoryMock.findOne.mockResolvedValue(scheduleWithSlots);

      await expect(
        service.update(updateDto, slotId, doctorEntity.userId, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });
  });

  describe('delete', () => {
    const slotId = 1;
    const secretaryUser: TokenUser = {
      id: 1,
      email: 'secretary@test.com',
      role: 'secretary',
      status: 'approved',
    };
    const doctorUser: TokenUser = {
      id: 10,
      email: 'doctor@test.com',
      role: 'doctor',
      status: 'approved',
    };
    const _anotherDoctorUser: TokenUser = {
      id: 20,
      email: 'doctor2@test.com',
      role: 'doctor',
      status: 'approved',
    };
    const _doctorEntity: Doctor = {
      userId: 10,
    } as Doctor;

    it('should delete available schedule slot successfully as secretary', async () => {
      const queryBuilderMock = {
        delete: jest.fn().mockReturnThis(),
        from: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        setParameter: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({ affected: 1 }),
      };

      scheduleSlotRepositoryMock.createQueryBuilder.mockReturnValue(
        queryBuilderMock,
      );

      await service.delete(slotId, doctorUser.id, secretaryUser);

      expect(scheduleSlotRepositoryMock.createQueryBuilder).toHaveBeenCalled();
      expect(queryBuilderMock.delete).toHaveBeenCalled();
      expect(queryBuilderMock.from).toHaveBeenCalledWith(DocScheduleSlot);
      expect(queryBuilderMock.where).toHaveBeenCalledWith('id = :slotId', {
        slotId,
      });
      expect(queryBuilderMock.andWhere).toHaveBeenCalledWith(
        'status = :status',
        {
          status: 'available',
        },
      );
      expect(queryBuilderMock.execute).toHaveBeenCalled();
    });

    it('should delete available schedule slot successfully as doctor for their own slot', async () => {
      const queryBuilderMock = {
        delete: jest.fn().mockReturnThis(),
        from: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        setParameter: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({ affected: 1 }),
      };

      scheduleSlotRepositoryMock.createQueryBuilder.mockReturnValue(
        queryBuilderMock,
      );

      await service.delete(slotId, doctorUser.id, doctorUser);

      expect(scheduleSlotRepositoryMock.createQueryBuilder).toHaveBeenCalled();
      expect(queryBuilderMock.delete).toHaveBeenCalled();
      expect(queryBuilderMock.from).toHaveBeenCalledWith(DocScheduleSlot);
      expect(queryBuilderMock.where).toHaveBeenCalledWith('id = :slotId', {
        slotId,
      });
      expect(queryBuilderMock.andWhere).toHaveBeenCalledWith(
        'status = :status',
        {
          status: 'available',
        },
      );
      // Should have additional andWhere for doctor authorization
      expect(queryBuilderMock.andWhere).toHaveBeenCalledTimes(2);
      expect(queryBuilderMock.setParameter).toHaveBeenCalledWith(
        'doctorId',
        doctorUser.id,
      );
      expect(queryBuilderMock.execute).toHaveBeenCalled();
    });

    it('should throw BadRequestException when slot is not found or not available', async () => {
      const queryBuilderMock = {
        delete: jest.fn().mockReturnThis(),
        from: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        setParameter: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({ affected: 0 }),
      };

      scheduleSlotRepositoryMock.createQueryBuilder.mockReturnValue(
        queryBuilderMock,
      );

      await expect(
        service.delete(slotId, doctorUser.id, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException(
          'Slot not found, not available, or not authorized to delete',
        ),
      );
    });

    it('should throw BadRequestException when deletion affects 0 rows', async () => {
      const queryBuilderMock = {
        delete: jest.fn().mockReturnThis(),
        from: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        setParameter: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({ affected: 0 }),
      };

      scheduleSlotRepositoryMock.createQueryBuilder.mockReturnValue(
        queryBuilderMock,
      );

      await expect(
        service.delete(slotId, doctorUser.id, secretaryUser),
      ).rejects.toThrow(
        new BadRequestException(
          'Slot not found, not available, or not authorized to delete',
        ),
      );
    });

    it('should include doctor authorization check for doctor role', async () => {
      const queryBuilderMock = {
        delete: jest.fn().mockReturnThis(),
        from: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        setParameter: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({ affected: 1 }),
      };

      scheduleSlotRepositoryMock.createQueryBuilder.mockReturnValue(
        queryBuilderMock,
      );

      await service.delete(slotId, doctorUser.id, doctorUser);

      // Verify andWhere is called for doctor authorization
      // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
      const andWhereCall = queryBuilderMock.andWhere.mock.calls.find(
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access, @typescript-eslint/no-unsafe-call
        (call) => call[0].includes('docScheduleId'),
      );
      expect(andWhereCall).toBeDefined();
      expect(queryBuilderMock.setParameter).toHaveBeenCalledWith(
        'doctorId',
        doctorUser.id,
      );
    });

    it('should not include doctor authorization check for secretary role', async () => {
      const queryBuilderMock = {
        delete: jest.fn().mockReturnThis(),
        from: jest.fn().mockReturnThis(),
        where: jest.fn().mockReturnThis(),
        andWhere: jest.fn().mockReturnThis(),
        setParameter: jest.fn().mockReturnThis(),
        execute: jest.fn().mockResolvedValue({ affected: 1 }),
      };

      scheduleSlotRepositoryMock.createQueryBuilder.mockReturnValue(
        queryBuilderMock,
      );

      await service.delete(slotId, doctorUser.id, secretaryUser);

      // Should have exactly 2 andWhere calls (status + doctor subquery)
      expect(queryBuilderMock.andWhere).toHaveBeenCalledTimes(2);
    });
  });
});
