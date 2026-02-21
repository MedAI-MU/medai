import { getRepositoryToken } from '@nestjs/typeorm';
import { Test } from '@nestjs/testing';
import { DoctorsService } from './doctors.service';
import { Doctor } from './entities/doctor.entity';
import { DoctorSpeciality } from './entities/doctor-speciality.entity';
import { Speciality } from './entities/speciality.entity';
import { CreateDoctorSpecialityDto } from './dtos/create-doctor-speciality.dto';
import { UpdateDoctorSpecialityDto } from './dtos/update-doctor-speciality.dto';
import { ILike } from 'typeorm';

describe('DoctorsService', () => {
  let service: DoctorsService;

  const doctorsRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
  };

  const mockSpeciality: Speciality = {
    id: 1,
    name: 'Cardiology',
  };

  const mockDoctorSpeciality: Partial<DoctorSpeciality> = {
    id: 1,
    doctor: {} as Doctor,
    speciality: mockSpeciality,
    isPrimary: true,
    yearsOfExperience: 5,
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockDoctor: Partial<Doctor> = {
    userId: 1,
    user: {
      id: 1,
      name: 'Dr. John Doe',
      email: 'john.doe@example.com',
    } as Doctor['user'],
    specialities: [],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  const mockDoctorWithSpecialities: Partial<Doctor> = {
    ...mockDoctor,
    specialities: [mockDoctorSpeciality as DoctorSpeciality],
  };

  beforeEach(async () => {
    const module = await Test.createTestingModule({
      providers: [
        DoctorsService,
        {
          provide: getRepositoryToken(Doctor),
          useValue: doctorsRepositoryMock,
        },
      ],
    }).compile();

    service = module.get<DoctorsService>(DoctorsService);

    doctorsRepositoryMock.find.mockReset();
    doctorsRepositoryMock.findOne.mockReset();
    doctorsRepositoryMock.create.mockReset();
    doctorsRepositoryMock.save.mockReset();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    it('should create and return a new doctor', async () => {
      const userId = 1;
      const createdDoctor = { ...mockDoctor } as Doctor;

      doctorsRepositoryMock.create.mockReturnValue(createdDoctor);
      doctorsRepositoryMock.save.mockResolvedValue(createdDoctor);

      const result = await service.create(userId);

      expect(result).toEqual(createdDoctor);
      expect(doctorsRepositoryMock.create).toHaveBeenCalledWith({
        user: { id: userId },
      });
      expect(doctorsRepositoryMock.save).toHaveBeenCalledWith(createdDoctor);
    });

    it('should create doctor with user reference', async () => {
      const userId = 2;
      const createdDoctor = { userId: 2, user: { id: 2 } } as Doctor;

      doctorsRepositoryMock.create.mockReturnValue(createdDoctor);
      doctorsRepositoryMock.save.mockResolvedValue(createdDoctor);

      const result = await service.create(userId);

      expect(result.userId).toBe(2);
      expect(doctorsRepositoryMock.create).toHaveBeenCalledWith({
        user: { id: userId },
      });
    });
  });

  describe('findOne', () => {
    it('should return a doctor with specialities when found', async () => {
      doctorsRepositoryMock.findOne.mockResolvedValue(
        mockDoctorWithSpecialities as Doctor,
      );

      const result = await service.findOne(1);

      expect(result).toEqual(mockDoctorWithSpecialities);
      expect(doctorsRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { userId: 1 },
        relations: {
          specialities: { speciality: true },
        },
      });
    });

    it('should return null when doctor is not found', async () => {
      doctorsRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.findOne(999);

      expect(result).toBeNull();
      expect(doctorsRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { userId: 999 },
        relations: {
          specialities: { speciality: true },
        },
      });
    });

    it('should load speciality relations nested', async () => {
      doctorsRepositoryMock.findOne.mockResolvedValue(mockDoctor as Doctor);

      await service.findOne(1);

      expect(doctorsRepositoryMock.findOne).toHaveBeenCalledWith(
        expect.objectContaining({
          relations: {
            specialities: { speciality: true },
          },
        }),
      );
    });
  });

  describe('findAllByName', () => {
    it('should return doctors matching the name', async () => {
      const doctors = [mockDoctor as Doctor];
      doctorsRepositoryMock.find.mockResolvedValue(doctors);

      const result = await service.findAllByName('John');

      expect(result).toEqual(doctors);
      expect(doctorsRepositoryMock.find).toHaveBeenCalledWith({
        where: { user: { name: ILike('%John%') } },
        relations: {
          specialities: { speciality: true },
        },
      });
    });

    it('should return empty array when no doctors match', async () => {
      doctorsRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findAllByName('NonexistentName');

      expect(result).toEqual([]);
      expect(doctorsRepositoryMock.find).toHaveBeenCalledTimes(1);
    });

    it('should use case-insensitive search with ILike', async () => {
      doctorsRepositoryMock.find.mockResolvedValue([]);

      await service.findAllByName('john');

      expect(doctorsRepositoryMock.find).toHaveBeenCalledWith({
        where: { user: { name: ILike('%john%') } },
        relations: {
          specialities: { speciality: true },
        },
      });
    });

    it('should return multiple doctors when multiple match', async () => {
      const doctors = [
        { ...mockDoctor, userId: 1 } as Doctor,
        {
          ...mockDoctor,
          userId: 2,
          user: { ...mockDoctor.user, name: 'Dr. John Smith' },
        } as Doctor,
      ];
      doctorsRepositoryMock.find.mockResolvedValue(doctors);

      const result = await service.findAllByName('John');

      expect(result).toHaveLength(2);
    });
  });

  describe('findAllBySpeciality', () => {
    it('should return doctors matching the speciality', async () => {
      const doctors = [mockDoctorWithSpecialities as Doctor];
      doctorsRepositoryMock.find.mockResolvedValue(doctors);

      const result = await service.findAllBySpeciality('Cardiology');

      expect(result).toEqual(doctors);
      expect(doctorsRepositoryMock.find).toHaveBeenCalledWith({
        where: {
          specialities: { speciality: { name: ILike('%Cardiology%') } },
        },
        relations: {
          specialities: { speciality: true },
        },
      });
    });

    it('should return empty array when no doctors match the speciality', async () => {
      doctorsRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findAllBySpeciality('Neurology');

      expect(result).toEqual([]);
    });

    it('should use case-insensitive search for speciality', async () => {
      doctorsRepositoryMock.find.mockResolvedValue([]);

      await service.findAllBySpeciality('cardiology');

      expect(doctorsRepositoryMock.find).toHaveBeenCalledWith({
        where: {
          specialities: { speciality: { name: ILike('%cardiology%') } },
        },
        relations: {
          specialities: { speciality: true },
        },
      });
    });
  });

  describe('addSpeciality', () => {
    it('should add a speciality to a doctor', async () => {
      const doctor = {
        ...mockDoctor,
        specialities: [],
      } as Doctor;

      const doctorSpecialityDto: CreateDoctorSpecialityDto = {
        specialityId: 1,
        isPrimary: true,
        yearsOfExperience: 5,
      };

      const savedDoctor = {
        ...doctor,
        specialities: [mockDoctorSpeciality],
      } as Doctor;

      doctorsRepositoryMock.save.mockResolvedValue(savedDoctor);
      doctorsRepositoryMock.findOne.mockResolvedValue(savedDoctor);

      const result = await service.addSpeciality(
        doctor,
        doctorSpecialityDto,
        mockSpeciality,
      );

      expect(result.specialities).toHaveLength(1);
      expect(doctorsRepositoryMock.save).toHaveBeenCalledWith(doctor);
      expect(doctorsRepositoryMock.findOne).toHaveBeenCalled();
    });

    it('should use default values when isPrimary and yearsOfExperience are not provided', async () => {
      const doctor = {
        ...mockDoctor,
        specialities: [],
      } as Doctor;

      const doctorSpecialityDto: Partial<CreateDoctorSpecialityDto> = {
        specialityId: 1,
      };

      const savedDoctor = { ...doctor } as Doctor;
      doctorsRepositoryMock.save.mockResolvedValue(savedDoctor);
      doctorsRepositoryMock.findOne.mockResolvedValue(savedDoctor);

      await service.addSpeciality(
        doctor,
        doctorSpecialityDto as CreateDoctorSpecialityDto,
        mockSpeciality,
      );

      expect(doctor.specialities[0].isPrimary).toBe(false);
      expect(doctor.specialities[0].yearsOfExperience).toBe(0);
    });

    it('should add to existing specialities without removing them', async () => {
      const existingSpeciality = {
        ...mockDoctorSpeciality,
        id: 1,
      } as DoctorSpeciality;
      const doctor = {
        ...mockDoctor,
        specialities: [existingSpeciality],
      } as Doctor;

      const newSpeciality: Speciality = {
        id: 2,
        name: 'Neurology',
      };

      const doctorSpecialityDto: CreateDoctorSpecialityDto = {
        specialityId: 2,
        isPrimary: false,
        yearsOfExperience: 3,
      };

      const savedDoctor = { ...doctor } as Doctor;
      doctorsRepositoryMock.save.mockResolvedValue(savedDoctor);
      doctorsRepositoryMock.findOne.mockResolvedValue(savedDoctor);

      const result = await service.addSpeciality(
        doctor,
        doctorSpecialityDto,
        newSpeciality,
      );

      expect(result.specialities).toHaveLength(2);
      expect(doctorsRepositoryMock.save).toHaveBeenCalledWith(doctor);
    });

    it('should re-fetch doctor after saving to get generated IDs', async () => {
      const doctor = {
        ...mockDoctor,
        userId: 1,
        specialities: [],
      } as Doctor;

      const doctorSpecialityDto: CreateDoctorSpecialityDto = {
        specialityId: 1,
        isPrimary: true,
        yearsOfExperience: 5,
      };

      const savedDoctor = { ...doctor } as Doctor;
      const refetchedDoctor = {
        ...doctor,
        specialities: [{ ...mockDoctorSpeciality, id: 100 }],
      } as Doctor;

      doctorsRepositoryMock.save.mockResolvedValue(savedDoctor);
      doctorsRepositoryMock.findOne.mockResolvedValue(refetchedDoctor);

      const result = await service.addSpeciality(
        doctor,
        doctorSpecialityDto,
        mockSpeciality,
      );

      expect(doctorsRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { userId: 1 },
        relations: {
          specialities: { speciality: true },
        },
      });
      expect(result).toBe(refetchedDoctor);
    });
  });

  describe('updateDoctorSpeciality', () => {
    it('should update an existing doctor speciality', async () => {
      const existingSpeciality = {
        ...mockDoctorSpeciality,
        id: 1,
        isPrimary: false,
        yearsOfExperience: 3,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [existingSpeciality],
      } as Doctor;

      const updateDto: UpdateDoctorSpecialityDto = {
        isPrimary: true,
        yearsOfExperience: 7,
      };

      doctorsRepositoryMock.save.mockResolvedValue(doctor);

      const result = await service.updateDoctorSpeciality(doctor, updateDto, 1);

      expect(result).not.toBeNull();
      expect(result!.specialities[0].isPrimary).toBe(true);
      expect(result!.specialities[0].yearsOfExperience).toBe(7);
      expect(doctorsRepositoryMock.save).toHaveBeenCalledWith(doctor);
    });

    it('should return null when speciality not found', async () => {
      const doctor = {
        ...mockDoctor,
        specialities: [],
      } as Doctor;

      const updateDto: UpdateDoctorSpecialityDto = {
        isPrimary: true,
      };

      const result = await service.updateDoctorSpeciality(
        doctor,
        updateDto,
        999,
      );

      expect(result).toBeNull();
      expect(doctorsRepositoryMock.save).not.toHaveBeenCalled();
    });

    it('should update only isPrimary when only isPrimary is provided', async () => {
      const existingSpeciality = {
        ...mockDoctorSpeciality,
        id: 1,
        isPrimary: false,
        yearsOfExperience: 5,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [existingSpeciality],
      } as Doctor;

      const updateDto: UpdateDoctorSpecialityDto = {
        isPrimary: true,
      };

      doctorsRepositoryMock.save.mockResolvedValue(doctor);

      const result = await service.updateDoctorSpeciality(doctor, updateDto, 1);

      expect(result!.specialities[0].isPrimary).toBe(true);
      expect(result!.specialities[0].yearsOfExperience).toBe(5);
    });

    it('should update only yearsOfExperience when only yearsOfExperience is provided', async () => {
      const existingSpeciality = {
        ...mockDoctorSpeciality,
        id: 1,
        isPrimary: true,
        yearsOfExperience: 5,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [existingSpeciality],
      } as Doctor;

      const updateDto: UpdateDoctorSpecialityDto = {
        yearsOfExperience: 10,
      };

      doctorsRepositoryMock.save.mockResolvedValue(doctor);

      const result = await service.updateDoctorSpeciality(doctor, updateDto, 1);

      expect(result!.specialities[0].isPrimary).toBe(true);
      expect(result!.specialities[0].yearsOfExperience).toBe(10);
    });

    it('should not update when dto has undefined values', async () => {
      const existingSpeciality = {
        ...mockDoctorSpeciality,
        id: 1,
        isPrimary: true,
        yearsOfExperience: 5,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [existingSpeciality],
      } as Doctor;

      const updateDto: UpdateDoctorSpecialityDto = {};

      doctorsRepositoryMock.save.mockResolvedValue(doctor);

      const result = await service.updateDoctorSpeciality(doctor, updateDto, 1);

      expect(result!.specialities[0].isPrimary).toBe(true);
      expect(result!.specialities[0].yearsOfExperience).toBe(5);
    });

    it('should find correct speciality among multiple', async () => {
      const speciality1 = {
        ...mockDoctorSpeciality,
        id: 1,
        isPrimary: true,
        yearsOfExperience: 5,
      } as DoctorSpeciality;

      const speciality2 = {
        ...mockDoctorSpeciality,
        id: 2,
        isPrimary: false,
        yearsOfExperience: 3,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [speciality1, speciality2],
      } as Doctor;

      const updateDto: UpdateDoctorSpecialityDto = {
        isPrimary: true,
      };

      doctorsRepositoryMock.save.mockResolvedValue(doctor);

      const result = await service.updateDoctorSpeciality(doctor, updateDto, 2);

      expect(result!.specialities[0].isPrimary).toBe(true);
      expect(result!.specialities[1].isPrimary).toBe(true);
    });
  });

  describe('removeDoctorSpeciality', () => {
    it('should remove an existing doctor speciality', async () => {
      const specialityToRemove = {
        ...mockDoctorSpeciality,
        id: 1,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [specialityToRemove],
      } as Doctor;

      const savedDoctor = {
        ...doctor,
        specialities: [] as DoctorSpeciality[],
      } as unknown as Doctor;

      doctorsRepositoryMock.save.mockResolvedValue(savedDoctor);

      const result = await service.removeDoctorSpeciality(doctor, 1);

      expect(result).not.toBeNull();
      expect(result!.specialities).toHaveLength(0);
      expect(doctorsRepositoryMock.save).toHaveBeenCalledWith(doctor);
    });

    it('should return null when speciality not found', async () => {
      const doctor = {
        ...mockDoctor,
        specialities: [] as DoctorSpeciality[],
      } as unknown as Doctor;

      const result = await service.removeDoctorSpeciality(doctor, 999);

      expect(result).toBeNull();
      expect(doctorsRepositoryMock.save).not.toHaveBeenCalled();
    });

    it('should remove only the specified speciality and keep others', async () => {
      const speciality1 = {
        ...mockDoctorSpeciality,
        id: 1,
        speciality: { id: 1, name: 'Cardiology' },
      } as DoctorSpeciality;

      const speciality2 = {
        ...mockDoctorSpeciality,
        id: 2,
        speciality: { id: 2, name: 'Neurology' },
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [speciality1, speciality2],
      } as Doctor;

      doctorsRepositoryMock.save.mockImplementation((d) => Promise.resolve(d));

      const result = await service.removeDoctorSpeciality(doctor, 1);

      expect(result!.specialities).toHaveLength(1);
      expect(result!.specialities[0].id).toBe(2);
    });

    it('should filter out the correct speciality by id', async () => {
      const speciality1 = {
        ...mockDoctorSpeciality,
        id: 1,
      } as DoctorSpeciality;
      const speciality2 = {
        ...mockDoctorSpeciality,
        id: 2,
      } as DoctorSpeciality;
      const speciality3 = {
        ...mockDoctorSpeciality,
        id: 3,
      } as DoctorSpeciality;

      const doctor = {
        ...mockDoctor,
        specialities: [speciality1, speciality2, speciality3],
      } as Doctor;

      doctorsRepositoryMock.save.mockImplementation((d) => Promise.resolve(d));

      const result = await service.removeDoctorSpeciality(doctor, 2);

      expect(result!.specialities).toHaveLength(2);
      expect(result!.specialities.map((s) => s.id)).toEqual([1, 3]);
    });
  });
});
