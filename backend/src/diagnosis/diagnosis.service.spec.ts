import { Test, TestingModule } from '@nestjs/testing';
import {
  BadRequestException,
  ForbiddenException,
  NotFoundException,
} from '@nestjs/common';
import { getRepositoryToken } from '@nestjs/typeorm';
import { DiagnosisService } from './diagnosis.service';
import { Diagnosis } from './entities/diagnosis.entity';
import { Appointment } from '../appointments/entities/appointment.entity';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import type { CreateDiagnosisDto } from './dtos/create-diagnosis.dto';
import type { UpdateDiagnosisDto } from './dtos/update-diagnosis.dto';
import type { UpdateSymptomsDto } from './dtos/update-symptoms.dto';

describe('DiagnosisService', () => {
  let service: DiagnosisService;

  const diagnosesRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    remove: jest.fn(),
  };

  const appointmentsRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
  };

  const secretaryUser: TokenUser = {
    id: 1,
    email: 'sec@test.com',
    role: 'secretary',
    status: 'approved',
  };

  const doctorUser: TokenUser = {
    id: 10,
    email: 'doc@test.com',
    role: 'doctor',
    status: 'approved',
  };

  const patientUser: TokenUser = {
    id: 5,
    email: 'pat@test.com',
    role: 'patient',
    status: 'approved',
  };

  const buildDiagnosis = (overrides = {}) => ({
    id: 1,
    patientUserId: 5,
    doctorUserId: 10,
    appointmentId: 100,
    symptoms: 'cough',
    summary: 'flu',
    createdAt: new Date(),
    updatedAt: new Date(),
    ...overrides,
  });

  const mockAppointment: Partial<Appointment> = {
    id: 100,
    doctorUserId: 10,
    patientUserId: 5,
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DiagnosisService,
        {
          provide: getRepositoryToken(Diagnosis),
          useValue: diagnosesRepositoryMock,
        },
        {
          provide: getRepositoryToken(Appointment),
          useValue: appointmentsRepositoryMock,
        },
      ],
    }).compile();

    service = module.get<DiagnosisService>(DiagnosisService);
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('findAll', () => {
    it('should return all diagnoses for a secretary', async () => {
      diagnosesRepositoryMock.find.mockResolvedValue([buildDiagnosis()]);
      const result = await service.findAll(secretaryUser);
      expect(result).toHaveLength(1);
      expect(diagnosesRepositoryMock.find).toHaveBeenCalledWith({
        order: { createdAt: 'DESC' },
      });
    });

    it('should return diagnoses for doctor related patients', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([{ patientUserId: 5 }]);
      diagnosesRepositoryMock.find.mockResolvedValue([buildDiagnosis()]);
      const result = await service.findAll(doctorUser);
      expect(result).toHaveLength(1);
      expect(diagnosesRepositoryMock.find).toHaveBeenCalledWith(
        expect.objectContaining({
          order: { createdAt: 'DESC' },
        }),
      );
    });

    it('should return empty array when doctor has no patients', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([]);
      const result = await service.findAll(doctorUser);
      expect(result).toEqual([]);
      expect(diagnosesRepositoryMock.find).not.toHaveBeenCalled();
    });
  });

  describe('findByPatient', () => {
    it('should return diagnoses for a patient', async () => {
      diagnosesRepositoryMock.find.mockResolvedValue([buildDiagnosis()]);
      const result = await service.findByPatient(5);
      expect(result).toHaveLength(1);
      expect(diagnosesRepositoryMock.find).toHaveBeenCalledWith({
        where: { patientUserId: 5 },
        order: { createdAt: 'DESC' },
      });
    });
  });

  describe('findOne', () => {
    it('should return diagnosis if found and owned by doctor', async () => {
      diagnosesRepositoryMock.findOne.mockResolvedValue(buildDiagnosis());
      const result = await service.findOne(1, 5, doctorUser);
      expect(result.id).toBe(1);
    });

    it('should throw NotFoundException when diagnosis not found', async () => {
      diagnosesRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.findOne(999, 5, doctorUser)).rejects.toThrow(
        NotFoundException,
      );
    });

    it("should throw ForbiddenException when patient tries to access another patient's diagnosis", async () => {
      diagnosesRepositoryMock.findOne.mockResolvedValue(
        buildDiagnosis({ patientUserId: 99 }),
      );
      await expect(service.findOne(1, 5, patientUser)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });

  describe('create', () => {
    const dto: CreateDiagnosisDto = {
      appointmentId: 100,
      symptoms: 'cough',
      summary: 'flu',
    };

    it('should create a diagnosis', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(mockAppointment);
      diagnosesRepositoryMock.findOne.mockResolvedValue(null);
      diagnosesRepositoryMock.create.mockReturnValue(buildDiagnosis());
      diagnosesRepositoryMock.save.mockResolvedValue(buildDiagnosis());

      const result = await service.create(dto, 10, 5);
      expect(result.id).toBe(1);
    });

    it('should throw NotFoundException when appointment not found', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.create(dto, 10, 5)).rejects.toThrow(
        NotFoundException,
      );
    });

    it('should throw BadRequestException when appointment does not belong to doctor', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(mockAppointment);
      await expect(service.create(dto, 99, 5)).rejects.toThrow(
        BadRequestException,
      );
    });

    it('should throw BadRequestException when patient does not match appointment', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(mockAppointment);
      await expect(service.create(dto, 10, 99)).rejects.toThrow(
        BadRequestException,
      );
    });

    it('should throw BadRequestException when diagnosis already exists for appointment', async () => {
      appointmentsRepositoryMock.findOne.mockResolvedValue(mockAppointment);
      diagnosesRepositoryMock.findOne.mockResolvedValue(buildDiagnosis());
      await expect(service.create(dto, 10, 5)).rejects.toThrow(
        BadRequestException,
      );
    });
  });

  describe('update', () => {
    const dto: UpdateDiagnosisDto = { symptoms: 'fever', summary: 'covid' };

    it('should update a diagnosis', async () => {
      const diag = buildDiagnosis();
      const updated = { ...diag, symptoms: 'fever', summary: 'covid' };
      diagnosesRepositoryMock.findOne.mockResolvedValue(diag);
      diagnosesRepositoryMock.save.mockResolvedValue(updated);

      const result = await service.update(1, dto, 5, doctorUser);
      expect(result.symptoms).toBe('fever');
      expect(result.summary).toBe('covid');
    });

    it('should throw NotFoundException when diagnosis not found', async () => {
      diagnosesRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.update(999, dto, 5, doctorUser)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('updateSymptoms', () => {
    const dto: UpdateSymptomsDto = { symptoms: 'headache' };

    it('should update symptoms only', async () => {
      const diag = buildDiagnosis();
      const updated = { ...diag, symptoms: 'headache' };
      diagnosesRepositoryMock.findOne.mockResolvedValue(diag);
      diagnosesRepositoryMock.save.mockResolvedValue(updated);

      const result = await service.updateSymptoms(1, dto, 5, doctorUser);
      expect(result.symptoms).toBe('headache');
      expect(result.summary).toBe('flu');
    });

    it('should throw NotFoundException when diagnosis not found', async () => {
      diagnosesRepositoryMock.findOne.mockResolvedValue(null);
      await expect(
        service.updateSymptoms(999, dto, 5, doctorUser),
      ).rejects.toThrow(NotFoundException);
    });
  });

  describe('delete', () => {
    it('should delete a diagnosis', async () => {
      const diag = buildDiagnosis();
      diagnosesRepositoryMock.findOne.mockResolvedValue(diag);
      diagnosesRepositoryMock.remove.mockResolvedValue(undefined);

      await service.delete(1, 5, doctorUser);
      expect(diagnosesRepositoryMock.remove).toHaveBeenCalledWith(diag);
    });

    it('should throw NotFoundException when diagnosis not found', async () => {
      diagnosesRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.delete(999, 5, doctorUser)).rejects.toThrow(
        NotFoundException,
      );
    });
  });
});
