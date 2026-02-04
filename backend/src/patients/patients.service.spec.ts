import { getRepositoryToken } from '@nestjs/typeorm';
import { Test } from '@nestjs/testing';
import { PatientsService } from './patients.service';
import { Patient } from './entities/patient.entity';
import { UpdatePatientDto } from './dtos/update_patient.dto';
import type { Gender, BloodType, MaritalStatus } from './types/patient.types';

describe('PatientsService', () => {
  let service: PatientsService;

  const patientsRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    update: jest.fn(),
  };

  const mockPatient: Partial<Patient> = {
    userId: 1,
    birthDate: new Date('1990-01-15'),
    height: 175,
    weight: 70,
    gender: 'male' as Gender,
    bloodType: 'A+' as BloodType,
    maritalStatus: 'single' as MaritalStatus,
    allergies: [],
    chronicDiseases: [],
    surgeries: [],
    familyHistories: [],
    emergencyContacts: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockPatientWithRelations: Partial<Patient> = {
    ...mockPatient,
    allergies: [
      {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0],
    ],
    chronicDiseases: [
      {
        id: 1,
        name: 'Diabetes',
        description: 'Type 2',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['chronicDiseases'][0],
    ],
  };

  beforeEach(async () => {
    const module = await Test.createTestingModule({
      providers: [
        PatientsService,
        {
          provide: getRepositoryToken(Patient),
          useValue: patientsRepositoryMock,
        },
      ],
    }).compile();

    service = module.get<PatientsService>(PatientsService);

    patientsRepositoryMock.find.mockReset();
    patientsRepositoryMock.findOne.mockReset();
    patientsRepositoryMock.create.mockReset();
    patientsRepositoryMock.save.mockReset();
    patientsRepositoryMock.update.mockReset();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('findAll', () => {
    it('should return an array of patients', async () => {
      const patients = [mockPatient as Patient];
      patientsRepositoryMock.find.mockResolvedValue(patients);

      const result = await service.findAll();

      expect(result).toEqual(patients);
      expect(patientsRepositoryMock.find).toHaveBeenCalledTimes(1);
    });

    it('should return an empty array when no patients exist', async () => {
      patientsRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findAll();

      expect(result).toEqual([]);
      expect(patientsRepositoryMock.find).toHaveBeenCalledTimes(1);
    });
  });

  describe('findOne', () => {
    it('should return a patient with all relations when found', async () => {
      patientsRepositoryMock.findOne.mockResolvedValue(
        mockPatientWithRelations as Patient,
      );

      const result = await service.findOne(1);

      expect(result).toEqual(mockPatientWithRelations);
      expect(patientsRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { userId: 1 },
        relations: {
          allergies: true,
          chronicDiseases: true,
          surgeries: true,
          familyHistories: true,
          emergencyContacts: true,
        },
      });
    });

    it('should return null when patient is not found', async () => {
      patientsRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.findOne(999);

      expect(result).toBeNull();
      expect(patientsRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { userId: 999 },
        relations: {
          allergies: true,
          chronicDiseases: true,
          surgeries: true,
          familyHistories: true,
          emergencyContacts: true,
        },
      });
    });

    it('should load all required relations', async () => {
      patientsRepositoryMock.findOne.mockResolvedValue(mockPatient as Patient);

      await service.findOne(1);

      expect(patientsRepositoryMock.findOne).toHaveBeenCalledWith(
        expect.objectContaining({
          relations: {
            allergies: true,
            chronicDiseases: true,
            surgeries: true,
            familyHistories: true,
            emergencyContacts: true,
          },
        }),
      );
    });
  });

  describe('create', () => {
    it('should create and return a new patient', async () => {
      const patientData = {
        userId: 2,
        birthDate: new Date('1985-05-20'),
        height: 180,
        weight: 75,
        gender: 'female' as Gender,
        bloodType: 'B+' as BloodType,
        maritalStatus: 'married' as MaritalStatus,
      } as Patient;

      const createdPatient = { ...patientData } as Patient;

      patientsRepositoryMock.create.mockReturnValue(createdPatient);
      patientsRepositoryMock.save.mockResolvedValue(createdPatient);

      const result = await service.create(patientData);

      expect(result).toEqual(createdPatient);
      expect(patientsRepositoryMock.create).toHaveBeenCalledWith(patientData);
      expect(patientsRepositoryMock.save).toHaveBeenCalledWith(createdPatient);
    });

    it('should create patient with optional fields as undefined', async () => {
      const patientData = {
        userId: 3,
        birthDate: new Date('2000-01-01'),
        height: 165,
        weight: 60,
        gender: 'male' as Gender,
      } as Patient;

      const createdPatient = { ...patientData } as Patient;

      patientsRepositoryMock.create.mockReturnValue(createdPatient);
      patientsRepositoryMock.save.mockResolvedValue(createdPatient);

      const result = await service.create(patientData);

      expect(result).toEqual(createdPatient);
      expect(patientsRepositoryMock.create).toHaveBeenCalledWith(patientData);
    });
  });

  describe('update', () => {
    it('should update and return the updated patient', async () => {
      const updateDto: UpdatePatientDto = {
        height: 180,
        weight: 75,
        bloodType: 'O+' as BloodType,
        maritalStatus: 'married' as MaritalStatus,
      };

      const updatedPatient = {
        ...mockPatient,
        ...updateDto,
      } as Patient;

      patientsRepositoryMock.update.mockResolvedValue({ affected: 1 });
      patientsRepositoryMock.findOne.mockResolvedValue(updatedPatient);

      const result = await service.update(updateDto, 1);

      expect(result).toEqual(updatedPatient);
      expect(patientsRepositoryMock.update).toHaveBeenCalledWith(1, updateDto);
      expect(patientsRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { userId: 1 },
      });
    });

    it('should update only provided fields', async () => {
      const updateDto: UpdatePatientDto = {
        weight: 80,
      } as UpdatePatientDto;

      const updatedPatient = {
        ...mockPatient,
        weight: 80,
      } as Patient;

      patientsRepositoryMock.update.mockResolvedValue({ affected: 1 });
      patientsRepositoryMock.findOne.mockResolvedValue(updatedPatient);

      const result = await service.update(updateDto, 1);

      expect(result.weight).toBe(80);
      expect(patientsRepositoryMock.update).toHaveBeenCalledWith(1, updateDto);
    });

    it('should return the patient even when no fields are updated', async () => {
      const updateDto = {} as UpdatePatientDto;

      patientsRepositoryMock.update.mockResolvedValue({ affected: 0 });
      patientsRepositoryMock.findOne.mockResolvedValue(mockPatient as Patient);

      const result = await service.update(updateDto, 1);

      expect(result).toEqual(mockPatient);
      expect(patientsRepositoryMock.update).toHaveBeenCalledWith(1, updateDto);
    });

    it('should update blood type correctly', async () => {
      const updateDto: UpdatePatientDto = {
        bloodType: 'AB-' as BloodType,
      } as UpdatePatientDto;

      const updatedPatient = {
        ...mockPatient,
        bloodType: 'AB-',
      } as Patient;

      patientsRepositoryMock.update.mockResolvedValue({ affected: 1 });
      patientsRepositoryMock.findOne.mockResolvedValue(updatedPatient);

      const result = await service.update(updateDto, 1);

      expect(result.bloodType).toBe('AB-');
    });

    it('should update marital status correctly', async () => {
      const updateDto: UpdatePatientDto = {
        maritalStatus: 'divorced' as MaritalStatus,
      } as UpdatePatientDto;

      const updatedPatient = {
        ...mockPatient,
        maritalStatus: 'divorced',
      } as Patient;

      patientsRepositoryMock.update.mockResolvedValue({ affected: 1 });
      patientsRepositoryMock.findOne.mockResolvedValue(updatedPatient);

      const result = await service.update(updateDto, 1);

      expect(result.maritalStatus).toBe('divorced');
    });
  });
});
