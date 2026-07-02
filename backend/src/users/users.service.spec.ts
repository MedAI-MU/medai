jest.mock('argon2', () => ({
  hash: jest.fn(),
}));

import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { Patient } from '../patients/entities/patient.entity';
import { UsersService } from './users.service';
import { Test } from '@nestjs/testing';
import { RegisterDto } from './dtos/register.dto';
import * as argon2 from 'argon2';
import { ConflictException, NotFoundException } from '@nestjs/common';
import { DataSource, EntityManager } from 'typeorm';

describe('users.service', () => {
  let usersService: UsersService;

  const usersRepositoryMock = {
    save: jest.fn(),
    findOne: jest.fn(),
    findOneBy: jest.fn(),
    update: jest.fn(),
    remove: jest.fn(),
  };

  const patientRepoMock = {
    delete: jest.fn(),
    save: jest.fn(),
  };

  const doctorRepoMock = {
    save: jest.fn(),
    delete: jest.fn(),
  };

  const dataSourceMock = {
    transaction: jest.fn(),
  };

  const mockManager = {
    getRepository: jest.fn(),
  };

  beforeEach(async () => {
    const module = await Test.createTestingModule({
      providers: [
        UsersService,
        {
          provide: getRepositoryToken(User),
          useValue: usersRepositoryMock,
        },
        {
          provide: DataSource,
          useValue: dataSourceMock,
        },
      ],
    }).compile();

    usersService = module.get<UsersService>(UsersService);

    usersRepositoryMock.findOne.mockReset();
    usersRepositoryMock.save.mockReset();
    usersRepositoryMock.findOneBy.mockReset();
    usersRepositoryMock.update.mockReset();
    patientRepoMock.delete.mockReset();
    patientRepoMock.save.mockReset();
    doctorRepoMock.save.mockReset();
    doctorRepoMock.delete.mockReset();
    dataSourceMock.transaction.mockReset();
    mockManager.getRepository.mockReset();
    (argon2.hash as jest.Mock).mockReset();
  });

  it('should be defined', () => {
    expect(usersService).toBeDefined();
  });

  describe('registerUser', () => {
    it('should hash the password, register the user and set id', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);
      (argon2.hash as jest.Mock).mockResolvedValue('hashedpassword');
      usersRepositoryMock.save.mockImplementation((user: User) => {
        user.id = 1;
        return user;
      });
      const dto: RegisterDto = {
        name: 'Test User',
        email: 'test@test.com',
        password: 'strongpassword',
        phone: '00000000000',
        role: 'patient',
      };

      const result = await usersService.registerUser(dto);

      expect(argon2.hash).toHaveBeenCalledWith(dto.password);
      expect(result.id).toBe(1);
      expect(result.password).toBe('hashedpassword');
    });

    it('should throw a conflict error if email is already in use', async () => {
      usersRepositoryMock.findOne.mockResolvedValue({
        id: 1,
        email: 'test@test.com',
        phone: '11111111111',
      } as User);
      const dto: RegisterDto = {
        name: 'Test User',
        email: 'test@test.com',
        password: 'strongpassword',
        phone: '00000000000',
        role: 'patient',
      };

      await expect(usersService.registerUser(dto)).rejects.toThrow(
        ConflictException,
      );
    });
    it('should throw a conflict error if phone is already in use', async () => {
      usersRepositoryMock.findOne.mockResolvedValue({
        id: 1,
        email: 'nottest@test.com',
        phone: '00000000000',
      } as User);
      const dto: RegisterDto = {
        name: 'Test User',
        email: 'test@test.com',
        password: 'strongpassword',
        phone: '00000000000',
        role: 'patient',
      };

      await expect(usersService.registerUser(dto)).rejects.toThrow(
        ConflictException,
      );
    });
  });

  describe('addDoctorRole', () => {
    it('should remove patient, create doctor, and update user role', async () => {
      const userId = 1;
      usersRepositoryMock.findOneBy.mockResolvedValue({ id: userId } as User);

      mockManager.getRepository.mockImplementation((entity) => {
        if (entity === Patient) return patientRepoMock;
        if (entity === Doctor) return doctorRepoMock;
        if (entity === User) return usersRepositoryMock;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> =>
          cb(mockManager as unknown as EntityManager),
      );

      await usersService.addDoctorRole(userId);

      expect(dataSourceMock.transaction).toHaveBeenCalled();
      expect(patientRepoMock.delete).toHaveBeenCalledWith(userId);
      expect(doctorRepoMock.save).toHaveBeenCalledWith({ userId });
      expect(usersRepositoryMock.update).toHaveBeenCalledWith(userId, {
        role: 'doctor',
        status: 'approved',
      });
    });

    it('should throw NotFoundException if user does not exist', async () => {
      usersRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(usersService.addDoctorRole(1)).rejects.toThrow(
        NotFoundException,
      );
      expect(dataSourceMock.transaction).not.toHaveBeenCalled();
    });
  });

  describe('addSecretaryRole', () => {
    it('should remove patient and update user role', async () => {
      const userId = 1;
      usersRepositoryMock.findOneBy.mockResolvedValue({ id: userId } as User);

      mockManager.getRepository.mockImplementation((entity) => {
        if (entity === Patient) return patientRepoMock;
        if (entity === User) return usersRepositoryMock;
        return {};
      });

      dataSourceMock.transaction.mockImplementation(
        async <T>(cb: (manager: EntityManager) => Promise<T>): Promise<T> =>
          cb(mockManager as unknown as EntityManager),
      );

      await usersService.addSecretaryRole(userId);

      expect(dataSourceMock.transaction).toHaveBeenCalled();
      expect(patientRepoMock.delete).toHaveBeenCalledWith(userId);
      expect(usersRepositoryMock.update).toHaveBeenCalledWith(userId, {
        role: 'secretary',
        status: 'approved',
      });
    });

    it('should throw NotFoundException if user does not exist', async () => {
      usersRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(usersService.addSecretaryRole(1)).rejects.toThrow(
        NotFoundException,
      );
      expect(dataSourceMock.transaction).not.toHaveBeenCalled();
    });
  });

  describe('removeUser', () => {
    it('should delete the user', async () => {
      const userId = 1;
      const user = { id: userId } as User;
      usersRepositoryMock.findOneBy.mockResolvedValue(user);

      await usersService.removeUser(userId);

      expect(usersRepositoryMock.remove).toHaveBeenCalledWith(user);
    });

    it('should throw NotFoundException if user does not exist', async () => {
      usersRepositoryMock.findOneBy.mockResolvedValue(null);

      await expect(usersService.removeUser(1)).rejects.toThrow(
        NotFoundException,
      );
    });
  });
});
