import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { SpecialityService } from './speciality.service';
import { Speciality } from './entities/speciality.entity';
import { SpecialityDto } from './dtos/speciality.dto';

describe('SpecialityService', () => {
  let service: SpecialityService;

  const specialitiesRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    remove: jest.fn(),
  };

  const mockSpeciality: Speciality = {
    id: 1,
    name: 'Cardiology',
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        SpecialityService,
        {
          provide: getRepositoryToken(Speciality),
          useValue: specialitiesRepositoryMock,
        },
      ],
    }).compile();

    service = module.get<SpecialityService>(SpecialityService);

    specialitiesRepositoryMock.find.mockReset();
    specialitiesRepositoryMock.findOne.mockReset();
    specialitiesRepositoryMock.create.mockReset();
    specialitiesRepositoryMock.save.mockReset();
    specialitiesRepositoryMock.remove.mockReset();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('createSpeciality', () => {
    it('should create and return a new speciality', async () => {
      const specialityDto: SpecialityDto = { name: 'Cardiology' };
      const createdSpeciality = { ...mockSpeciality };

      specialitiesRepositoryMock.create.mockReturnValue(createdSpeciality);
      specialitiesRepositoryMock.save.mockResolvedValue(createdSpeciality);

      const result = await service.createSpeciality(specialityDto);

      expect(result).toEqual(createdSpeciality);
      expect(specialitiesRepositoryMock.create).toHaveBeenCalledWith({
        name: specialityDto.name,
      });
      expect(specialitiesRepositoryMock.save).toHaveBeenCalledWith(
        createdSpeciality,
      );
    });

    it('should create speciality with provided name', async () => {
      const specialityDto: SpecialityDto = { name: 'Neurology' };
      const createdSpeciality = { id: 2, name: 'Neurology' };

      specialitiesRepositoryMock.create.mockReturnValue(createdSpeciality);
      specialitiesRepositoryMock.save.mockResolvedValue(createdSpeciality);

      const result = await service.createSpeciality(specialityDto);

      expect(result.name).toBe('Neurology');
      expect(specialitiesRepositoryMock.create).toHaveBeenCalledWith({
        name: 'Neurology',
      });
    });
  });

  describe('updateSpeciality', () => {
    it('should update and return the speciality when found', async () => {
      const specialityDto: SpecialityDto = { name: 'Dermatology' };
      const existingSpeciality = { ...mockSpeciality };
      const updatedSpeciality = { ...existingSpeciality, name: 'Dermatology' };

      specialitiesRepositoryMock.findOne.mockResolvedValue(existingSpeciality);
      specialitiesRepositoryMock.save.mockResolvedValue(updatedSpeciality);

      const result = await service.updateSpeciality(1, specialityDto);

      expect(result).not.toBeNull();
      expect(result!.name).toBe('Dermatology');
      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(specialitiesRepositoryMock.save).toHaveBeenCalled();
    });

    it('should return null when speciality not found', async () => {
      const specialityDto: SpecialityDto = { name: 'Dermatology' };

      specialitiesRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.updateSpeciality(999, specialityDto);

      expect(result).toBeNull();
      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 999 },
      });
      expect(specialitiesRepositoryMock.save).not.toHaveBeenCalled();
    });

    it('should update the name property of the speciality', async () => {
      const specialityDto: SpecialityDto = { name: 'Oncology' };
      const existingSpeciality = { id: 1, name: 'Cardiology' };

      specialitiesRepositoryMock.findOne.mockResolvedValue(existingSpeciality);
      specialitiesRepositoryMock.save.mockImplementation((s) =>
        Promise.resolve(s),
      );

      const result = await service.updateSpeciality(1, specialityDto);

      expect(result!.name).toBe('Oncology');
      expect(existingSpeciality.name).toBe('Oncology');
    });
  });

  describe('deleteSpeciality', () => {
    it('should delete and return the speciality when found', async () => {
      const existingSpeciality = { ...mockSpeciality };

      specialitiesRepositoryMock.findOne.mockResolvedValue(existingSpeciality);
      specialitiesRepositoryMock.remove.mockResolvedValue(existingSpeciality);

      const result = await service.deleteSpeciality(1);

      expect(result).toEqual(existingSpeciality);
      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 1 },
      });
      expect(specialitiesRepositoryMock.remove).toHaveBeenCalledWith(
        existingSpeciality,
      );
    });

    it('should return null when speciality not found', async () => {
      specialitiesRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.deleteSpeciality(999);

      expect(result).toBeNull();
      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 999 },
      });
      expect(specialitiesRepositoryMock.remove).not.toHaveBeenCalled();
    });

    it('should call remove with the correct speciality', async () => {
      const existingSpeciality = { id: 5, name: 'Pediatrics' };

      specialitiesRepositoryMock.findOne.mockResolvedValue(existingSpeciality);
      specialitiesRepositoryMock.remove.mockResolvedValue(existingSpeciality);

      await service.deleteSpeciality(5);

      expect(specialitiesRepositoryMock.remove).toHaveBeenCalledTimes(1);
      expect(specialitiesRepositoryMock.remove).toHaveBeenCalledWith(
        existingSpeciality,
      );
    });
  });

  describe('findOne', () => {
    it('should return a speciality when found', async () => {
      specialitiesRepositoryMock.findOne.mockResolvedValue(mockSpeciality);

      const result = await service.findOne(1);

      expect(result).toEqual(mockSpeciality);
      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 1 },
      });
    });

    it('should return null when speciality not found', async () => {
      specialitiesRepositoryMock.findOne.mockResolvedValue(null);

      const result = await service.findOne(999);

      expect(result).toBeNull();
      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 999 },
      });
    });

    it('should query with the correct id', async () => {
      specialitiesRepositoryMock.findOne.mockResolvedValue(null);

      await service.findOne(42);

      expect(specialitiesRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { id: 42 },
      });
    });
  });

  describe('findAll', () => {
    it('should return an array of specialities', async () => {
      const specialities = [
        { id: 1, name: 'Cardiology' },
        { id: 2, name: 'Neurology' },
        { id: 3, name: 'Dermatology' },
      ];

      specialitiesRepositoryMock.find.mockResolvedValue(specialities);

      const result = await service.findAll();

      expect(result).toEqual(specialities);
      expect(result).toHaveLength(3);
      expect(specialitiesRepositoryMock.find).toHaveBeenCalledTimes(1);
    });

    it('should return an empty array when no specialities exist', async () => {
      specialitiesRepositoryMock.find.mockResolvedValue([]);

      const result = await service.findAll();

      expect(result).toEqual([]);
      expect(result).toHaveLength(0);
      expect(specialitiesRepositoryMock.find).toHaveBeenCalledTimes(1);
    });

    it('should return all specialities from the repository', async () => {
      const specialities = [
        { id: 1, name: 'Cardiology' },
        { id: 2, name: 'Neurology' },
      ];

      specialitiesRepositoryMock.find.mockResolvedValue(specialities);

      const result = await service.findAll();

      expect(result).toHaveLength(2);
      expect(result[0].name).toBe('Cardiology');
      expect(result[1].name).toBe('Neurology');
    });
  });
});
