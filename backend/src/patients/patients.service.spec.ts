import { getRepositoryToken } from '@nestjs/typeorm';
import { Test } from '@nestjs/testing';
import { NotFoundException } from '@nestjs/common';
import { PatientsService } from './patients.service';
import { Patient } from './entities/patient.entity';
import { UpdatePatientDto } from './dtos/update_patient.dto';
import type { Gender, BloodType, MaritalStatus } from './types/patient.types';
import type { GenericPatientRelation } from './interfaces/generic-patient-relation.interface';

describe('PatientsService', () => {
  let service: PatientsService;

  const managerMock = {
    remove: jest.fn(),
  };

  const patientsRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    update: jest.fn(),
    manager: managerMock,
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
    managerMock.remove.mockReset();
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

  describe('addRelation', () => {
    it('should add an allergy to a patient', async () => {
      const patient = {
        ...mockPatient,
        allergies: [],
      } as Patient;

      const newAllergy = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0];

      patientsRepositoryMock.save.mockResolvedValue(patient);

      const result = await service.addRelation(
        patient,
        'allergies',
        newAllergy,
      );

      expect(result.allergies).toContain(newAllergy);
      expect(patientsRepositoryMock.save).toHaveBeenCalledWith(patient);
    });

    it('should add a chronic disease to a patient', async () => {
      const patient = {
        ...mockPatient,
        chronicDiseases: [],
      } as Patient;

      const newChronicDisease = {
        id: 1,
        name: 'Diabetes',
        description: 'Type 2',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['chronicDiseases'][0];

      patientsRepositoryMock.save.mockResolvedValue(patient);

      const result = await service.addRelation(
        patient,
        'chronicDiseases',
        newChronicDisease,
      );

      expect(result.chronicDiseases).toContain(newChronicDisease);
      expect(patientsRepositoryMock.save).toHaveBeenCalledWith(patient);
    });

    it('should add to existing relations without removing them', async () => {
      const existingAllergy = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0];

      const patient = {
        ...mockPatient,
        allergies: [existingAllergy],
      } as Patient;

      const newAllergy = {
        id: 2,
        name: 'Shellfish',
        description: 'Mild allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0];

      patientsRepositoryMock.save.mockResolvedValue(patient);

      const result = await service.addRelation(
        patient,
        'allergies',
        newAllergy,
      );

      expect(result.allergies).toHaveLength(2);
      expect(result.allergies).toContain(existingAllergy);
      expect(result.allergies).toContain(newAllergy);
    });
  });

  describe('updateRelation', () => {
    it('should update an existing allergy', async () => {
      const existingAllergy = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0] & GenericPatientRelation;

      const patient = {
        ...mockPatient,
        allergies: [existingAllergy],
      } as Patient;

      const updatedAllergyData = {
        id: 1,
        name: 'Peanuts',
        description: 'Mild allergy now',
      } as Patient['allergies'][0] & GenericPatientRelation;

      patientsRepositoryMock.save.mockResolvedValue(patient);

      const result = await service.updateRelation(
        patient,
        'allergies',
        1,
        updatedAllergyData,
      );

      expect(result.allergies[0].description).toBe('Mild allergy now');
      expect(result.allergies[0].updatedAt).toBeInstanceOf(Date);
      expect(patientsRepositoryMock.save).toHaveBeenCalledWith(patient);
    });

    it('should throw NotFoundException when relation item not found', async () => {
      const patient = {
        ...mockPatient,
        allergies: [],
      } as Patient;

      const updateData = {
        id: 999,
        name: 'Nonexistent',
      } as Patient['allergies'][0] & GenericPatientRelation;

      await expect(
        service.updateRelation(patient, 'allergies', 999, updateData),
      ).rejects.toThrow(NotFoundException);

      expect(patientsRepositoryMock.save).not.toHaveBeenCalled();
    });

    it('should update the updatedAt timestamp', async () => {
      const oldDate = new Date('2020-01-01');
      const existingAllergy = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: oldDate,
        updatedAt: oldDate,
      } as Patient['allergies'][0] & GenericPatientRelation;

      const patient = {
        ...mockPatient,
        allergies: [existingAllergy],
      } as Patient;

      const updateData = {
        id: 1,
        name: 'Peanuts Updated',
      } as Patient['allergies'][0] & GenericPatientRelation;

      patientsRepositoryMock.save.mockResolvedValue(patient);

      const beforeUpdate = new Date();
      const result = await service.updateRelation(
        patient,
        'allergies',
        1,
        updateData,
      );

      expect(result.allergies[0].updatedAt.getTime()).toBeGreaterThanOrEqual(
        beforeUpdate.getTime(),
      );
    });

    it('should use Object.assign to merge data', async () => {
      const existingAllergy = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0] & GenericPatientRelation;

      const patient = {
        ...mockPatient,
        allergies: [existingAllergy],
      } as Patient;

      const partialUpdateData = {
        id: 1,
        description: 'Now mild',
      } as Patient['allergies'][0] & GenericPatientRelation;

      patientsRepositoryMock.save.mockResolvedValue(patient);

      const result = await service.updateRelation(
        patient,
        'allergies',
        1,
        partialUpdateData,
      );

      expect(result.allergies[0].name).toBe('Peanuts');
      expect(result.allergies[0].description).toBe('Now mild');
    });
  });

  describe('removeRelation', () => {
    it('should remove an existing allergy', async () => {
      const allergyToRemove = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0] & GenericPatientRelation;

      const patient = {
        ...mockPatient,
        allergies: [allergyToRemove],
      } as Patient;

      managerMock.remove.mockResolvedValue(allergyToRemove);

      const result = await service.removeRelation(patient, 'allergies', 1);

      expect(result.allergies).toHaveLength(0);
      expect(managerMock.remove).toHaveBeenCalledWith(allergyToRemove);
    });

    it('should throw NotFoundException when relation item not found', async () => {
      const patient = {
        ...mockPatient,
        allergies: [],
      } as Patient;

      await expect(
        service.removeRelation(patient, 'allergies', 999),
      ).rejects.toThrow(NotFoundException);

      expect(managerMock.remove).not.toHaveBeenCalled();
    });

    it('should remove only the specified item and keep others', async () => {
      const allergy1 = {
        id: 1,
        name: 'Peanuts',
        description: 'Severe allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0] & GenericPatientRelation;

      const allergy2 = {
        id: 2,
        name: 'Shellfish',
        description: 'Mild allergy',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['allergies'][0] & GenericPatientRelation;

      const patient = {
        ...mockPatient,
        allergies: [allergy1, allergy2],
      } as Patient;

      managerMock.remove.mockResolvedValue(allergy1);

      const result = await service.removeRelation(patient, 'allergies', 1);

      expect(result.allergies).toHaveLength(1);
      expect(result.allergies[0]).toBe(allergy2);
      expect(managerMock.remove).toHaveBeenCalledWith(allergy1);
    });

    it('should call manager.remove with the correct item', async () => {
      const chronicDisease = {
        id: 5,
        name: 'Diabetes',
        description: 'Type 2',
        createdAt: new Date(),
        updatedAt: new Date(),
      } as Patient['chronicDiseases'][0] & GenericPatientRelation;

      const patient = {
        ...mockPatient,
        chronicDiseases: [chronicDisease],
      } as Patient;

      managerMock.remove.mockResolvedValue(chronicDisease);

      await service.removeRelation(patient, 'chronicDiseases', 5);

      expect(managerMock.remove).toHaveBeenCalledTimes(1);
      expect(managerMock.remove).toHaveBeenCalledWith(chronicDisease);
    });
  });
});
