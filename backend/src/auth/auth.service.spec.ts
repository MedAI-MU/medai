jest.mock('argon2', () => ({
  verify: jest.fn(),
  hash: jest.fn(),
}));

import { Test } from '@nestjs/testing';
import { AuthService } from './auth.service';
import { getRepositoryToken } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { JwtService } from '@nestjs/jwt';
import jwtConfig from './jwt.config';
import * as argon2 from 'argon2';
import { Request } from 'express';

describe('AuthService', () => {
  let authService: AuthService;

  const usersRepositoryMock = {
    findOne: jest.fn(),
  };
  const refreshTokensRepositoryMock = {
    findOne: jest.fn(),
    find: jest.fn(),
    save: jest.fn(),
    remove: jest.fn(),
  };
  const jwtServiceMock = {
    signAsync: jest.fn(),
  };
  const jwtOptionsMock = {
    tokenSecret: 'testSecret',
    tokenExpiresInMs: 900000,
    refreshTokenSecret: 'testRefreshSecret',
    refreshTokenExpiresInMs: 604800000,
  };

  const now = new Date('2025-12-05T12:00:00Z').getTime();

  beforeEach(async () => {
    const module = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: getRepositoryToken(User),
          useValue: usersRepositoryMock,
        },
        {
          provide: getRepositoryToken(RefreshToken),
          useValue: refreshTokensRepositoryMock,
        },
        {
          provide: JwtService,
          useValue: jwtServiceMock,
        },
        {
          provide: jwtConfig.KEY,
          useValue: jwtOptionsMock,
        },
      ],
    }).compile();

    authService = module.get<AuthService>(AuthService);

    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(authService).toBeDefined();
  });

  describe('validateUser', () => {
    it('should return null when user is not found', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      const result = await authService.validateUser(
        'test@test.com',
        'strongpassword',
      );

      expect(usersRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { email: 'test@test.com' },
      });

      expect(result).toBeNull();
    });

    it('should return null when password is invalid', async () => {
      const fakeUser = {
        id: 1,
        email: 'test@test.com',
        password: 'hashedpassword',
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      (argon2.verify as jest.Mock).mockResolvedValue(false);

      const result = await authService.validateUser('test@test.com', 'wrong');

      expect(argon2.verify).toHaveBeenCalledWith('hashedpassword', 'wrong');
      expect(result).toBeNull();
    });

    it('should return user without password when login is valid', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        password: 'hashedpassword',
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      (argon2.verify as jest.Mock).mockResolvedValue(true);

      const result = await authService.validateUser(
        'test@test.com',
        'strongpassword',
      );

      expect(argon2.verify).toHaveBeenCalledWith(
        'hashedpassword',
        'strongpassword',
      );

      expect(result).toEqual({
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
      });

      expect(result).not.toHaveProperty('password');
    });
  });

  describe('validateRefreshToken', () => {
    beforeEach(() => {
      // Mock system time for consistent expiresAt checks
      jest.useFakeTimers().setSystemTime(now);
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should return null when user is not found', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      const result = await authService.validateRefreshToken(1, 'refresh-token');

      expect(result).toBeNull();
    });

    it('should return null when no refresh tokens exist', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        password: 'hashedpassword',
        refreshTokens: [],
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      const result = await authService.validateRefreshToken(1, 'refresh-token');

      expect(result).toBeNull();
    });

    it('should return null when refresh token does not match', async () => {
      const fakeUser = {
        id: 1,
        email: 'test@test.com',
        name: 'Tester',
        password: 'hashedpassword',
        refreshTokens: [
          {
            id: 10,
            token: 'hashedRT',
            expiresAt: new Date(now + 60 * 60 * 1000), // +1 hour
          },
        ],
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      (argon2.verify as jest.Mock).mockResolvedValue(false); // token mismatch

      const result = await authService.validateRefreshToken(
        1,
        'wrong-refresh-token',
      );

      expect(argon2.verify).toHaveBeenCalledWith(
        'hashedRT',
        'wrong-refresh-token',
      );
      expect(result).toBeNull();
    });

    it('should return null when refresh token is expired', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        password: 'hashedpassword',
        refreshTokens: [
          {
            id: 10,
            token: 'hashedRT',
            expiresAt: new Date(now - 60 * 60 * 1000), // -1 hour (expired)
          },
        ],
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      (argon2.verify as jest.Mock).mockResolvedValue(true); // token matches but expired

      const result = await authService.validateRefreshToken(
        1,
        'correct-refresh-token',
      );

      expect(result).toBeNull();
    });

    it('should return user without password and refreshTokens when refresh token is valid and not expired', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        password: 'hashedpassword',
        refreshTokens: [
          {
            id: 10,
            token: 'hashedRT',
            expiresAt: new Date(now + 60 * 60 * 1000), // +1 hour
          },
        ],
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      (argon2.verify as jest.Mock).mockResolvedValue(true);

      const result = await authService.validateRefreshToken(
        1,
        'correct-refresh-token',
      );

      expect(argon2.verify).toHaveBeenCalledWith(
        'hashedRT',
        'correct-refresh-token',
      );

      expect(result).toEqual({
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
      });
    });
  });

  describe('login', () => {
    beforeEach(() => {
      // Mock system time for consistent expiresAt checks
      jest.useFakeTimers().setSystemTime(now);
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should issue access token and refresh token and saves hashed refresh token in database', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        role: 'doctor',
      };

      // Mock JWT generation
      jwtServiceMock.signAsync
        .mockResolvedValueOnce('accessToken')
        .mockResolvedValueOnce('refreshToken');

      // Mock hashing
      (argon2.hash as jest.Mock).mockResolvedValue('hashedNewRT');

      let savedTokenEntity: Partial<RefreshToken> = new RefreshToken({});
      refreshTokensRepositoryMock.save.mockImplementation(
        (entity: Partial<RefreshToken>) => {
          // Capture the saved entity for later assertions
          savedTokenEntity = entity;
          return Promise.resolve(entity);
        },
      );

      const result = await authService.login(fakeUser as User);

      // ---- Assertions ----

      // 1. JWTs are issued
      expect(jwtServiceMock.signAsync).toHaveBeenCalledTimes(2);
      expect(jwtServiceMock.signAsync).toHaveBeenCalledWith(
        { sub: fakeUser.id, email: fakeUser.email, role: fakeUser.role },
        {
          secret: jwtOptionsMock.tokenSecret,
          expiresIn: `${jwtOptionsMock.tokenExpiresInMs}ms`,
        },
      );
      expect(jwtServiceMock.signAsync).toHaveBeenCalledWith(
        { sub: fakeUser.id, email: fakeUser.email, role: fakeUser.role },
        {
          secret: jwtOptionsMock.refreshTokenSecret,
          expiresIn: `${jwtOptionsMock.refreshTokenExpiresInMs}ms`,
        },
      );

      // 2. New refresh token is hashed and saved
      expect(argon2.hash).toHaveBeenCalledWith('refreshToken');
      expect(savedTokenEntity.token).toBe('hashedNewRT');

      // 3. Return object contains both tokens and expiration dates
      expect(result.accessToken).toBe('accessToken');
      expect(result.refreshToken).toBe('refreshToken');
      expect(result.accessTokenExpiresAt).toBeInstanceOf(Date);
      expect(result.refreshTokenExpiresAt).toBeInstanceOf(Date);

      // Access token expiration is correct
      const expectedAccessExp = new Date(
        now + jwtOptionsMock.tokenExpiresInMs,
      );
      expect(result.accessTokenExpiresAt.getTime()).toBe(
        expectedAccessExp.getTime(),
      );

      // Refresh token expiration is correct
      const expectedRefreshExp = new Date(
        now + jwtOptionsMock.refreshTokenExpiresInMs,
      );
      expect(result.refreshTokenExpiresAt.getTime()).toBe(
        expectedRefreshExp.getTime(),
      );
    });
  });

  describe('removeOldRefreshToken', () => {
    const mockRequest = (cookieValue?: string): Request =>
      ({
        cookies: cookieValue ? { Refresh: cookieValue } : {},
      }) as Request;
    it('should do nothing if no refresh token cookie exists', async () => {
      const req = mockRequest();

      await authService.removeOldRefreshToken(req, { id: 1 } as User);

      expect(refreshTokensRepositoryMock.find).not.toHaveBeenCalled();
      expect(refreshTokensRepositoryMock.remove).not.toHaveBeenCalled();
    });

    it('should do nothing if no existing tokens for user', async () => {
      const req = mockRequest('some-refresh-token');

      refreshTokensRepositoryMock.find.mockResolvedValue([]);
      await authService.removeOldRefreshToken(req, { id: 1 } as User);

      expect(refreshTokensRepositoryMock.find).toHaveBeenCalledWith({
        where: { user: { id: 1 } },
      });
      expect(refreshTokensRepositoryMock.remove).not.toHaveBeenCalled();
    });

    it('should do nothing if no tokens match the refresh token cookie', async () => {
      const req = mockRequest('some-refresh-token');

      const existingTokens = [
        {
          id: 10,
          token: 'hashedRT1',
        },
        {
          id: 11,
          token: 'hashedRT2',
        },
      ];

      refreshTokensRepositoryMock.find.mockResolvedValue(existingTokens);
      (argon2.verify as jest.Mock).mockResolvedValue(false); // no matches

      await authService.removeOldRefreshToken(req, { id: 1 } as User);

      expect(refreshTokensRepositoryMock.find).toHaveBeenCalledWith({
        where: { user: { id: 1 } },
      });
      expect(argon2.verify).toHaveBeenCalledTimes(2);
      expect(refreshTokensRepositoryMock.remove).not.toHaveBeenCalled();
    });

    it('should remove matching refresh token from database', async () => {
      const req = mockRequest('some-refresh-token');
      const existingTokens = [
        {
          id: 10,
          token: 'hashedRT1',
        },
        {
          id: 11,
          token: 'hashedRT2',
        },
      ];

      refreshTokensRepositoryMock.find.mockResolvedValue(existingTokens);
      // First token does not match, second token matches
      (argon2.verify as jest.Mock)
        .mockResolvedValueOnce(false)
        .mockResolvedValueOnce(true);

      await authService.removeOldRefreshToken(req, { id: 1 } as User);

      expect(refreshTokensRepositoryMock.find).toHaveBeenCalledWith({
        where: { user: { id: 1 } },
      });
      expect(argon2.verify).toHaveBeenCalledTimes(2);
      expect(argon2.verify).toHaveBeenNthCalledWith(
        1,
        'hashedRT1',
        'some-refresh-token',
      );
      expect(argon2.verify).toHaveBeenNthCalledWith(
        2,
        'hashedRT2',
        'some-refresh-token',
      );
      expect(refreshTokensRepositoryMock.remove).toHaveBeenCalledTimes(1);
      expect(refreshTokensRepositoryMock.remove).toHaveBeenCalledWith(
        existingTokens[1],
      );
    });
  });
});
