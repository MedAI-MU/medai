import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { BadRequestException, ForbiddenException } from '@nestjs/common';
import { AppointmentsService } from './appointments.service';
import { Appointment } from './entities/appointment.entity';
import { DocScheduleSlot } from '../schedules/entities/doc-schedule-slot.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { AppointmentStatusEnum } from './enums/appointment-status.enum';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import type { CreateAppointmentDto } from './dtos/create-appointment.dto';
import type { UpdateAppointmentStatusDto } from './dtos/update-appointment-status.dto';
import type { ReviewAppointmentDto } from './dtos/review-appointment.dto';

describe('AppointmentsService', () => {
  let service: AppointmentsService;

  const appointmentsRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    delete: jest.fn(),
    remove: jest.fn(),
  };

  const slotsRepositoryMock = {
    findOne: jest.fn(),
    save: jest.fn(),
  };

  const doctorsRepositoryMock = {
    findOne: jest.fn(),
  };

  // ── Shared fixtures ────────────────────────────────────────────────────────

  const mockSlot: Partial<DocScheduleSlot> = {
    id: 10,
    status: 'available',
    startTime: '09:00',
    endTime: '09:30',
    schedule: {
      id: 1,
      doctor: { userId: 2 } as Doctor,
    } as DocScheduleSlot['schedule'],
  };

  const mockAppointment: Partial<Appointment> = {
    id: 1,
    patientUserId: 5,
    doctorUserId: 2,
    scheduleSlotId: 10,
    status: 'pending',
    confirmedByUserId: null,
    rating: null,
    review: null,
    scheduleSlot: { id: 10, status: 'booked' } as DocScheduleSlot,
  };

  const patientUser: TokenUser = {
    id: 5,
    email: 'patient@test.com',
    role: 'patient',
    status: 'approved',
  };
  const secretaryUser: TokenUser = {
    id: 99,
    email: 'sec@test.com',
    role: 'secretary',
    status: 'approved',
  };
  const otherPatientUser: TokenUser = {
    id: 7,
    email: 'other@test.com',
    role: 'patient',
    status: 'approved',
  };

  // ── Module setup ───────────────────────────────────────────────────────────

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AppointmentsService,
        {
          provide: getRepositoryToken(Appointment),
          useValue: appointmentsRepositoryMock,
        },
        {
          provide: getRepositoryToken(DocScheduleSlot),
          useValue: slotsRepositoryMock,
        },
        {
          provide: getRepositoryToken(Doctor),
          useValue: doctorsRepositoryMock,
        },
      ],
    }).compile();

    service = module.get<AppointmentsService>(AppointmentsService);

    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  // ── create ─────────────────────────────────────────────────────────────────

  describe('create', () => {
    const dto: CreateAppointmentDto = { slotId: 10, doctorId: 2 };

    it('should return null when the slot does not exist', async () => {
      slotsRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.create(patientUser.id, dto);

      expect(result).toBeNull();
      expect(appointmentsRepositoryMock.create).not.toHaveBeenCalled();
    });

    it('should throw BadRequestException when slot is not available', async () => {
      slotsRepositoryMock.findOne.mockResolvedValue({
        ...mockSlot,
        status: 'booked',
      });

      await expect(service.create(patientUser.id, dto)).rejects.toThrow(
        new BadRequestException('Schedule slot is not available'),
      );
    });

    it('should throw BadRequestException when slot belongs to a different doctor', async () => {
      slotsRepositoryMock.findOne.mockResolvedValue({
        ...mockSlot,
        schedule: { doctor: { userId: 99 } },
      });

      await expect(service.create(patientUser.id, dto)).rejects.toThrow(
        new BadRequestException(
          'Schedule slot does not belong to the specified doctor',
        ),
      );
    });

    it('should mark the slot as booked and return the saved appointment', async () => {
      const slot = { ...mockSlot, status: 'available' };
      const createdAppointment = { ...mockAppointment };
      slotsRepositoryMock.findOne.mockResolvedValue(slot);
      appointmentsRepositoryMock.create.mockReturnValue(createdAppointment);
      appointmentsRepositoryMock.save.mockResolvedValue(createdAppointment);

      const result = await service.create(patientUser.id, dto);

      expect(slot.status).toBe('booked');
      expect(slotsRepositoryMock.save).toHaveBeenCalledWith(slot);
      expect(appointmentsRepositoryMock.create).toHaveBeenCalledWith({
        patientUserId: patientUser.id,
        doctorUserId: dto.doctorId,
        scheduleSlotId: dto.slotId,
      });
      expect(result).toEqual(createdAppointment);
    });
  });

  // ── findByPatient ──────────────────────────────────────────────────────────

  describe('findByPatient', () => {
    it('should return appointments for the given patient', async () => {
      const appointments = [mockAppointment as Appointment];
      appointmentsRepositoryMock.find.mockResolvedValue(appointments);

      const result = await service.findByPatient(patientUser.id);

      expect(result).toEqual(appointments);
      expect(appointmentsRepositoryMock.find).toHaveBeenCalledWith({
        where: { patientUserId: patientUser.id },
        relations: {
          doctor: { user: true, specialities: { speciality: true } },
          scheduleSlot: { schedule: true },
          confirmedBy: true,
        },
      });
    });

    it('should return an empty array when the patient has no appointments', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findByPatient(patientUser.id);

      expect(result).toEqual([]);
    });
  });

  // ── findByDoctor ───────────────────────────────────────────────────────────

  describe('findByDoctor', () => {
    it('should return null when the doctor does not exist', async () => {
      doctorsRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.findByDoctor(2);

      expect(result).toBeNull();
      expect(appointmentsRepositoryMock.find).not.toHaveBeenCalled();
    });

    it('should return appointments for the given doctor', async () => {
      const appointments = [mockAppointment as Appointment];
      doctorsRepositoryMock.findOne.mockResolvedValue({ userId: 2 });
      appointmentsRepositoryMock.find.mockResolvedValue(appointments);

      const result = await service.findByDoctor(2);

      expect(result).toEqual(appointments);
      expect(appointmentsRepositoryMock.find).toHaveBeenCalledWith({
        where: { doctorUserId: 2 },
        relations: {
          patient: { user: true },
          scheduleSlot: { schedule: true },
        },
      });
    });

    it('should return an empty array when the doctor has no appointments', async () => {
      doctorsRepositoryMock.findOne.mockResolvedValue({ userId: 2 });
      appointmentsRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findByDoctor(2);

      expect(result).toEqual([]);
    });
  });

  // ── findAll ────────────────────────────────────────────────────────────────

  describe('findAll', () => {
    it('should return all appointments with full relations', async () => {
      const appointments = [mockAppointment as Appointment];
      appointmentsRepositoryMock.find.mockResolvedValue(appointments);

      const result = await service.findAll();

      expect(result).toEqual(appointments);
      expect(appointmentsRepositoryMock.find).toHaveBeenCalledWith({
        relations: {
          patient: { user: true },
          doctor: { user: true },
          scheduleSlot: { schedule: true },
          confirmedBy: true,
        },
      });
    });

    it('should return an empty array when there are no appointments', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findAll();

      expect(result).toEqual([]);
    });
  });

  // ── delete ─────────────────────────────────────────────────────────────────

  describe('delete', () => {
    it('should return true when the appointment is deleted', async () => {
      appointmentsRepositoryMock.delete.mockResolvedValue({ affected: 1 });

      const result = await service.delete(1);

      expect(result).toBe(true);
      expect(appointmentsRepositoryMock.delete).toHaveBeenCalledWith(1);
    });

    it('should return false when no appointment matched the id', async () => {
      appointmentsRepositoryMock.delete.mockResolvedValue({ affected: 0 });

      const result = await service.delete(999);

      expect(result).toBe(false);
    });

    it('should return false when affected is null', async () => {
      appointmentsRepositoryMock.delete.mockResolvedValue({ affected: null });

      const result = await service.delete(1);

      expect(result).toBe(false);
    });
  });

  // ── updateStatus ───────────────────────────────────────────────────────────

  describe('updateStatus', () => {
    it('should return null when the appointment does not exist', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(null);

      const dto: UpdateAppointmentStatusDto = {
        status: AppointmentStatusEnum.CONFIRMED,
      };
      const result = await service.updateStatus(999, dto, secretaryUser);

      expect(result).toBeNull();
    });

    describe('patient role', () => {
      it("should throw ForbiddenException when patient tries to update another patient's appointment", async () => {
        appointmentsRepositoryMock.findOne.mockResolvedValue({
          ...mockAppointment,
        });

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.CANCELLED,
        };
        await expect(
          service.updateStatus(1, dto, otherPatientUser),
        ).rejects.toThrow(
          new ForbiddenException('You can only update your own appointments'),
        );
      });

      it('should throw ForbiddenException when patient tries to confirm an appointment', async () => {
        appointmentsRepositoryMock.findOne.mockResolvedValue({
          ...mockAppointment,
        });

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.CONFIRMED,
        };
        await expect(service.updateStatus(1, dto, patientUser)).rejects.toThrow(
          new ForbiddenException('Patients can only cancel appointments'),
        );
      });

      it('should throw ForbiddenException when patient tries to finish an appointment', async () => {
        appointmentsRepositoryMock.findOne.mockResolvedValue({
          ...mockAppointment,
        });

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.FINISHED,
        };
        await expect(service.updateStatus(1, dto, patientUser)).rejects.toThrow(
          new ForbiddenException('Patients can only cancel appointments'),
        );
      });

      it('should cancel the appointment, free the slot, and delete the appointment when patient cancels', async () => {
        const slot = { id: 10, status: 'booked' };
        const appointment = {
          ...mockAppointment,
          status: 'pending',
          scheduleSlot: slot,
        };
        appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.CANCELLED,
        };
        const result = await service.updateStatus(1, dto, patientUser);

        expect(slot.status).toBe('available');
        expect(slotsRepositoryMock.save).toHaveBeenCalledWith(slot);
        expect(appointment.status).toBe('cancelled');
        expect(appointmentsRepositoryMock.remove).toHaveBeenCalledWith(
          appointment,
        );
        expect(result).toEqual(appointment);
      });
    });

    describe('secretary role', () => {
      it('should confirm the appointment and set confirmedByUserId', async () => {
        const appointment = {
          ...mockAppointment,
          status: 'pending',
          confirmedByUserId: null,
        };
        appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);
        appointmentsRepositoryMock.save.mockResolvedValue({
          ...appointment,
          status: 'confirmed',
          confirmedByUserId: secretaryUser.id,
        });

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.CONFIRMED,
        };
        await service.updateStatus(1, dto, secretaryUser);

        expect(appointment.confirmedByUserId).toBe(secretaryUser.id);
        expect(appointment.status).toBe('confirmed');
        expect(slotsRepositoryMock.save).not.toHaveBeenCalled();
      });

      it('should cancel the appointment, free the slot, and delete', async () => {
        const slot = { id: 10, status: 'booked' };
        const appointment = {
          ...mockAppointment,
          status: 'confirmed',
          scheduleSlot: slot,
        };
        appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.CANCELLED,
        };
        const result = await service.updateStatus(1, dto, secretaryUser);

        expect(slot.status).toBe('available');
        expect(slotsRepositoryMock.save).toHaveBeenCalledWith(slot);
        expect(appointment.status).toBe('cancelled');
        expect(appointmentsRepositoryMock.remove).toHaveBeenCalledWith(
          appointment,
        );
        expect(result).toEqual(appointment);
      });

      it('should free the slot and delete when cancelling an already cancelled appointment', async () => {
        const slot = { id: 10, status: 'available' };
        const appointment = {
          ...mockAppointment,
          status: 'cancelled',
          scheduleSlot: slot,
        };
        appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.CANCELLED,
        };
        const result = await service.updateStatus(1, dto, secretaryUser);

        expect(slot.status).toBe('available');
        expect(slotsRepositoryMock.save).toHaveBeenCalledWith(slot);
        expect(appointment.status).toBe('cancelled');
        expect(appointmentsRepositoryMock.remove).toHaveBeenCalledWith(
          appointment,
        );
        expect(result).toEqual(appointment);
      });

      it('should mark appointment as finished without touching the slot', async () => {
        const slot = { id: 10, status: 'booked' };
        const appointment = {
          ...mockAppointment,
          status: 'confirmed',
          scheduleSlot: slot,
        };
        appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);
        appointmentsRepositoryMock.save.mockResolvedValue({
          ...appointment,
          status: 'finished',
        });

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.FINISHED,
        };
        await service.updateStatus(1, dto, secretaryUser);

        expect(slotsRepositoryMock.save).not.toHaveBeenCalled();
        expect(appointment.status).toBe('finished');
      });

      it('should not set confirmedByUserId when status is not confirmed', async () => {
        const appointment = {
          ...mockAppointment,
          status: 'pending',
          confirmedByUserId: null,
        };
        appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);
        appointmentsRepositoryMock.save.mockResolvedValue(appointment);

        const dto: UpdateAppointmentStatusDto = {
          status: AppointmentStatusEnum.FINISHED,
        };
        await service.updateStatus(1, dto, secretaryUser);

        expect(appointment.confirmedByUserId).toBeNull();
      });
    });
  });

  // ── addReview ──────────────────────────────────────────────────────────────

  describe('addReview', () => {
    const dto: ReviewAppointmentDto = {
      rating: 5,
      review: 'Excellent doctor.',
    };

    it('should return null when the appointment does not exist', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.addReview(999, dto, patientUser.id);

      expect(result).toBeNull();
    });

    it("should throw ForbiddenException when patient tries to review another patient's appointment", async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue({
        ...mockAppointment,
        status: 'finished',
      });

      await expect(
        service.addReview(1, dto, otherPatientUser.id),
      ).rejects.toThrow(
        new ForbiddenException('You can only review your own appointments'),
      );
    });

    it('should throw BadRequestException when appointment is not finished', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue({
        ...mockAppointment,
        status: 'confirmed',
      });

      await expect(service.addReview(1, dto, patientUser.id)).rejects.toThrow(
        new BadRequestException('Only finished appointments can be reviewed'),
      );
    });

    it('should throw BadRequestException when appointment is pending', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue({
        ...mockAppointment,
        status: 'pending',
      });

      await expect(service.addReview(1, dto, patientUser.id)).rejects.toThrow(
        new BadRequestException('Only finished appointments can be reviewed'),
      );
    });

    it('should save rating and review for a finished appointment', async () => {
      const appointment = { ...mockAppointment, status: 'finished' };
      const saved = { ...appointment, rating: dto.rating, review: dto.review };
      appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);
      appointmentsRepositoryMock.save.mockResolvedValue(saved);

      const result = await service.addReview(1, dto, patientUser.id);

      expect(appointment.rating).toBe(dto.rating);
      expect(appointment.review).toBe(dto.review);
      expect(appointmentsRepositoryMock.save).toHaveBeenCalledWith(appointment);
      expect(result).toEqual(saved);
    });

    it('should set review to null when not provided', async () => {
      const dtoNoReview: ReviewAppointmentDto = { rating: 4 };
      const appointment = { ...mockAppointment, status: 'finished' };
      appointmentsRepositoryMock.findOne.mockResolvedValue(appointment);
      appointmentsRepositoryMock.save.mockResolvedValue({
        ...appointment,
        rating: 4,
        review: null,
      });

      await service.addReview(1, dtoNoReview, patientUser.id);

      expect(appointment.review).toBeNull();
    });
  });
});
