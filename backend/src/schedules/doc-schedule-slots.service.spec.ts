import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { BadRequestException } from '@nestjs/common';
import { DocScheduleSlotsService } from './doc-schedule-slots.service';
import { DocScheduleSlot } from './entities/doc-schedule-slot.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { User } from '../users/entities/user.entity';
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';

describe('DocScheduleSlotsService', () => {
  let service: DocScheduleSlotsService;
  const doctorRepositoryMock = {
    findOneBy: jest.fn(),
  };
  const userRepositoryMock = {
    findOneBy: jest.fn(),
  };
  const scheduleSlotRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    findOneBy: jest.fn(),
    save: jest.fn(),
    delete: jest.fn(),
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
    const secretaryId = 1;
    const doctorId = 10;
    const doctorEntity: Doctor = {
      userId: doctorId,
    } as Doctor;
    const secretaryEntity: User = {
      id: secretaryId,
      name: 'Secretary',
    } as User;

    const createDto: CreateDocScheduleDto = {
      doctorId,
      slots: [
        {
          day: '2024-01-15',
          startTime: '09:00',
          endTime: '10:00',
        },
        {
          day: '2024-01-15',
          startTime: '11:00',
          endTime: '12:00',
        },
      ],
    };

    it('should create schedule slots successfully', async () => {
      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(secretaryEntity);
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);

      const slotsToSave = createDto.slots.map(
        (slot) =>
          new DocScheduleSlot({
            doctor: doctorEntity,
            secretary: secretaryEntity,
            dayDate: slot.day,
            startTime: slot.startTime,
            endTime: slot.endTime,
          }),
      );

      scheduleSlotRepositoryMock.save.mockResolvedValue(slotsToSave);

      const result = await service.create(createDto, secretaryId);

      expect(doctorRepositoryMock.findOneBy).toHaveBeenCalledWith({
        userId: doctorId,
      });
      expect(userRepositoryMock.findOneBy).toHaveBeenCalledWith({
        id: secretaryId,
      });
      expect(scheduleSlotRepositoryMock.find).toHaveBeenCalled();
      expect(scheduleSlotRepositoryMock.save).toHaveBeenCalled();
      expect(result).toEqual(slotsToSave);
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
      const invalidDto: CreateDocScheduleDto = {
        doctorId,
        slots: [
          {
            day: '2024-01-15',
            startTime: '09:00',
            endTime: '10:00',
          },
          {
            day: '2024-01-15',
            startTime: '09:30',
            endTime: '11:00',
          },
        ],
      };
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);

      await expect(service.create(invalidDto, secretaryId)).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should throw BadRequestException if new slots overlap with existing slots', async () => {
      const existingSlots: DocScheduleSlot[] = [
        {
          id: 1,
          doctor: doctorEntity,
          secretary: secretaryEntity,
          dayDate: '2024-01-15',
          startTime: '09:00',
          endTime: '10:30',
          status: 'available',
        } as DocScheduleSlot,
      ];

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(secretaryEntity);
      scheduleSlotRepositoryMock.find.mockResolvedValue(existingSlots);

      await expect(service.create(createDto, secretaryId)).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });

    it('should handle multiple days correctly', async () => {
      const multiDayDto: CreateDocScheduleDto = {
        doctorId,
        slots: [
          {
            day: '2024-01-15',
            startTime: '09:00',
            endTime: '10:00',
          },
          {
            day: '2024-01-16',
            startTime: '14:00',
            endTime: '15:00',
          },
        ],
      };

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(secretaryEntity);
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue([]);

      await service.create(multiDayDto, secretaryId);

      expect(scheduleSlotRepositoryMock.find).toHaveBeenCalled();
      const findCall = scheduleSlotRepositoryMock.find.mock.calls[0][0];
      expect(findCall.where.dayDate).toBeDefined();
    });

    it('should handle empty slots array', async () => {
      const emptyDto: CreateDocScheduleDto = {
        doctorId,
        slots: [],
      };

      doctorRepositoryMock.findOneBy.mockResolvedValue(doctorEntity);
      userRepositoryMock.findOneBy.mockResolvedValue(secretaryEntity);
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue([]);

      await service.create(emptyDto, secretaryId);

      expect(scheduleSlotRepositoryMock.save).toHaveBeenCalled();
    });
  });

  describe('update', () => {
    const slotId = 1;
    const doctorEntity: Doctor = {
      userId: 10,
    } as Doctor;
    const secretaryEntity: User = {
      id: 1,
      name: 'Secretary',
    } as User;
    const existingSlot: DocScheduleSlot = {
      id: slotId,
      doctor: doctorEntity,
      secretary: secretaryEntity,
      dayDate: '2024-01-15',
      startTime: '09:00',
      endTime: '10:00',
      status: 'available',
    } as DocScheduleSlot;

    it('should update schedule slot successfully', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:00',
        endTime: '11:00',
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      scheduleSlotRepositoryMock.find.mockResolvedValue([existingSlot]);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...existingSlot,
        startTime: updateDto.startTime,
        endTime: updateDto.endTime,
      });

      const result = await service.update(updateDto, slotId);

      expect(scheduleSlotRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: slotId },
        relations: ['doctor'],
      });
      expect(result.startTime).toBe(updateDto.startTime);
      expect(result.endTime).toBe(updateDto.endTime);
    });

    it('should throw BadRequestException if slot not found', async () => {
      scheduleSlotRepositoryMock.findOne.mockResolvedValue(null);

      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '10:00',
      };

      await expect(service.update(updateDto, slotId)).rejects.toThrow(
        new BadRequestException('Schedule slot not found'),
      );
    });

    it('should throw BadRequestException if updated time ranges are invalid', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '11:00',
        endTime: '10:00', // Invalid: end time is before start time
      };

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      scheduleSlotRepositoryMock.find.mockResolvedValue([existingSlot]);

      await expect(service.update(updateDto, slotId)).rejects.toThrow(
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
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...slotWithSeconds,
        startTime: updateDto.startTime,
      });

      const result = await service.update(updateDto, slotId);

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

      await expect(service.update(updateDto, slotId)).rejects.toThrow(
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
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...slotWithSeconds,
        endTime: updateDto.endTime,
      });

      const result = await service.update(updateDto, slotId);

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

      await expect(service.update(updateDto, slotId)).rejects.toThrow(
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
      scheduleSlotRepositoryMock.find.mockResolvedValue([]);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...slotWithSeconds,
        dayDate: updateDto.day,
      });

      const result = await service.update(updateDto, slotId);

      expect(result.dayDate).toBe(updateDto.day);
    });

    it('should update successfully with existing slots and no overlap', async () => {
      const updateDto: UpdateDocScheduleSlotDto = {
        startTime: '13:00',
        endTime: '14:00',
      };

      const otherSlot: DocScheduleSlot = {
        id: 2,
        doctor: doctorEntity,
        secretary: secretaryEntity,
        dayDate: '2024-01-15',
        startTime: '10:00:00',
        endTime: '11:00:00',
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      scheduleSlotRepositoryMock.find.mockResolvedValue([
        existingSlot,
        otherSlot,
      ]);
      scheduleSlotRepositoryMock.save.mockResolvedValue({
        ...existingSlot,
        startTime: updateDto.startTime,
        endTime: updateDto.endTime,
      });

      const result = await service.update(updateDto, slotId);

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
        doctor: doctorEntity,
        secretary: secretaryEntity,
        dayDate: '2024-01-15',
        startTime: '11:00:00',
        endTime: '12:00:00',
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOne.mockResolvedValue(existingSlot);
      scheduleSlotRepositoryMock.find.mockResolvedValue([
        existingSlot,
        otherSlot,
      ]);

      await expect(service.update(updateDto, slotId)).rejects.toThrow(
        new BadRequestException('Invalid or overlapping time ranges detected'),
      );
    });
  });

  describe('delete', () => {
    const slotId = 1;

    it('should delete available schedule slot successfully', async () => {
      const slot: DocScheduleSlot = {
        id: slotId,
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOneBy.mockResolvedValue(slot);
      scheduleSlotRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(slotId);

      expect(scheduleSlotRepositoryMock.findOneBy).toHaveBeenCalledWith({
        id: slotId,
      });
      expect(scheduleSlotRepositoryMock.delete).toHaveBeenCalledWith({
        id: slotId,
        status: 'available',
      });
    });

    it('should throw BadRequestException if slot not found', async () => {
      scheduleSlotRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(service.delete(slotId)).rejects.toThrow(
        new BadRequestException('Schedule slot not found'),
      );
    });

    it('should throw BadRequestException if slot is not available', async () => {
      const slot: DocScheduleSlot = {
        id: slotId,
        status: 'booked',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOneBy.mockResolvedValue(slot);
      scheduleSlotRepositoryMock.delete.mockResolvedValue({ affected: 0 });

      await expect(service.delete(slotId)).rejects.toThrow(
        new BadRequestException('Only available slots can be deleted'),
      );
    });

    it('should only delete slots with available status', async () => {
      const slot: DocScheduleSlot = {
        id: slotId,
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOneBy.mockResolvedValue(slot);
      scheduleSlotRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      await service.delete(slotId);

      const deleteCall = scheduleSlotRepositoryMock.delete.mock.calls[0][0];
      expect(deleteCall.status).toBe('available');
    });

    it('should handle edge case when slot status changes to booked before delete', async () => {
      const slot: DocScheduleSlot = {
        id: slotId,
        status: 'available',
      } as DocScheduleSlot;

      scheduleSlotRepositoryMock.findOneBy.mockResolvedValue(slot);
      scheduleSlotRepositoryMock.delete.mockResolvedValue({ affected: 0 });

      await expect(service.delete(slotId)).rejects.toThrow(
        new BadRequestException('Only available slots can be deleted'),
      );
    });
  });
});
