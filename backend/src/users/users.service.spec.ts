jest.mock('argon2', () => ({
  hash: jest.fn(),
}));

import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from './entities/user.entity';
import { Doctor } from '../doctors/entities/doctor.entity';
import { UsersService } from './users.service';
import { Test } from '@nestjs/testing';
import { RegisterDto } from './dtos/register.dto';
import * as argon2 from 'argon2';
import { ConflictException } from '@nestjs/common';

describe('users.service', () => {
  let usersService: UsersService;

  const usersRepositoryMock = {
    save: jest.fn(),
    findOne: jest.fn(),
  };

  const doctorsRepositoryMock = {
    save: jest.fn(),
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
          provide: getRepositoryToken(Doctor),
          useValue: doctorsRepositoryMock,
        },
      ],
    }).compile();

    usersService = module.get<UsersService>(UsersService);

    usersRepositoryMock.findOne.mockReset();
    usersRepositoryMock.save.mockReset();
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
        role: 'doctor',
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
        role: 'doctor',
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
        role: 'doctor',
      };

      await expect(usersService.registerUser(dto)).rejects.toThrow(
        ConflictException,
      );
    });
  });
});
