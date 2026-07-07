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
import { AuthMailerService } from './auth-mailer.service';
import { NotFoundException, ConflictException } from '@nestjs/common';

describe('AuthService', () => {
  let authService: AuthService;

  const usersRepositoryMock = {
    findOne: jest.fn(),
    save: jest.fn(),
    update: jest.fn(),
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

  const authMailerServiceMock = {
    sendVerificationEmail: jest.fn(),
    sendPasswordResetEmail: jest.fn(),
  };

  const now = new Date('2025-12-05T12:00:00Z');

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
        {
          provide: AuthMailerService,
          useValue: authMailerServiceMock,
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
      jest.useFakeTimers({ now: now.getTime() });
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
            expiresAt: new Date(now.getTime() + 60 * 60 * 1000), // +1 hour
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
            expiresAt: new Date(now.getTime() - 60 * 60 * 1000), // -1 hour (expired)
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
            expiresAt: new Date(now.getTime() + 60 * 60 * 1000), // +1 hour
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
      jest.useFakeTimers({ now: now.getTime() });
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
        now.getTime() + jwtOptionsMock.tokenExpiresInMs,
      );
      expect(result.accessTokenExpiresAt.getTime()).toBe(
        expectedAccessExp.getTime(),
      );

      // Refresh token expiration is correct
      const expectedRefreshExp = new Date(
        now.getTime() + jwtOptionsMock.refreshTokenExpiresInMs,
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

  describe('sendVerificationEmail', () => {
    it('should do nothing when user is not found', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      await authService.sendVerificationEmail('unknown@test.com');

      expect(
        authMailerServiceMock.sendVerificationEmail,
      ).not.toHaveBeenCalled();
    });

    it('should do nothing when email is already verified', async () => {
      const fakeUser = {
        id: 1,
        email: 'test@test.com',
        emailVerified: true,
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      await authService.sendVerificationEmail('test@test.com');

      expect(
        authMailerServiceMock.sendVerificationEmail,
      ).not.toHaveBeenCalled();
    });

    it('should generate token, save user and send email', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
        emailVerified: false,
        verificationToken: undefined,
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);
      authMailerServiceMock.sendVerificationEmail.mockResolvedValue(undefined);

      await authService.sendVerificationEmail('test@test.com');

      expect(usersRepositoryMock.save).toHaveBeenCalledWith(
        expect.objectContaining({
          verificationToken: expect.any(String),
        }),
      );
      expect(authMailerServiceMock.sendVerificationEmail).toHaveBeenCalledWith(
        'test@test.com',
        'Test User',
        expect.any(String),
      );
    });
  });

  describe('verifyEmail', () => {
    it('should throw BadRequestException when token is invalid', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      await expect(authService.verifyEmail('invalid-token')).rejects.toThrow(
        'Invalid verification token',
      );
    });

    it('should mark email as verified and clear token', async () => {
      const fakeUser = {
        id: 1,
        emailVerified: false,
        verificationToken: 'valid-token',
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      await authService.verifyEmail('valid-token');

      expect(usersRepositoryMock.findOne).toHaveBeenCalledWith({
        where: { verificationToken: 'valid-token' },
      });
      expect(usersRepositoryMock.save).toHaveBeenCalledWith(
        expect.objectContaining({
          emailVerified: true,
          verificationToken: undefined,
        }),
      );
    });
  });

  describe('forgotPassword', () => {
    it('should do nothing when user is not found', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      await authService.forgotPassword('unknown@test.com');

      expect(
        authMailerServiceMock.sendPasswordResetEmail,
      ).not.toHaveBeenCalled();
    });

    it('should generate reset token and send email', async () => {
      const fakeUser = {
        id: 1,
        name: 'Test User',
        email: 'test@test.com',
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);
      authMailerServiceMock.sendPasswordResetEmail.mockResolvedValue(undefined);

      await authService.forgotPassword('test@test.com');

      expect(usersRepositoryMock.save).toHaveBeenCalledWith(
        expect.objectContaining({
          resetPasswordToken: expect.any(String),
          resetPasswordExpiresAt: expect.any(Date),
        }),
      );
      expect(authMailerServiceMock.sendPasswordResetEmail).toHaveBeenCalledWith(
        'test@test.com',
        'Test User',
        expect.any(String),
      );
    });
  });

  describe('resetPassword', () => {
    it('should throw BadRequestException when token is invalid', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      await expect(
        authService.resetPassword('invalid-token', 'newPassword123'),
      ).rejects.toThrow('Invalid or expired reset token');
    });

    it('should throw BadRequestException when token is expired', async () => {
      const fakeUser = {
        id: 1,
        resetPasswordToken: 'expired-token',
        resetPasswordExpiresAt: new Date(Date.now() - 3600000),
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      await expect(
        authService.resetPassword('expired-token', 'newPassword123'),
      ).rejects.toThrow('Invalid or expired reset token');
    });

    it('should hash new password and clear reset token', async () => {
      const fakeUser = {
        id: 1,
        password: 'old-hashed-password',
        resetPasswordToken: 'valid-token',
        resetPasswordExpiresAt: new Date(Date.now() + 3600000),
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);
      (argon2.hash as jest.Mock).mockResolvedValue('new-hashed-password');

      await authService.resetPassword('valid-token', 'newPassword123');

      expect(argon2.hash).toHaveBeenCalledWith('newPassword123');
      expect(usersRepositoryMock.save).toHaveBeenCalledWith(
        expect.objectContaining({
          password: 'new-hashed-password',
          resetPasswordToken: undefined,
          resetPasswordExpiresAt: undefined,
        }),
      );
    });
  });

  describe('verifyEmail (with pendingEmail)', () => {
    it('should update email from pendingEmail when present', async () => {
      const fakeUser = {
        id: 1,
        email: 'old@test.com',
        pendingEmail: 'new@test.com',
        emailVerified: false,
        verificationToken: 'token-123',
      };

      usersRepositoryMock.findOne.mockResolvedValue(fakeUser);

      await authService.verifyEmail('token-123');

      expect(usersRepositoryMock.save).toHaveBeenCalledWith(
        expect.objectContaining({
          email: 'new@test.com',
          pendingEmail: undefined,
          emailVerified: true,
          verificationToken: undefined,
        }),
      );
    });
  });

  describe('requestEmailChange', () => {
    it('should throw NotFoundException when user not found', async () => {
      usersRepositoryMock.findOne.mockResolvedValue(null);

      await expect(
        authService.requestEmailChange(1, 'password', 'new@test.com'),
      ).rejects.toThrow(NotFoundException);
    });

    it('should throw BadRequestException when password is wrong', async () => {
      usersRepositoryMock.findOne.mockResolvedValue({
        id: 1,
        password: 'hashed-password',
      });
      (argon2.verify as jest.Mock).mockResolvedValue(false);

      await expect(
        authService.requestEmailChange(1, 'wrong-password', 'new@test.com'),
      ).rejects.toThrow('Invalid password');
    });

    it('should throw BadRequestException when new email is same as current', async () => {
      usersRepositoryMock.findOne.mockResolvedValueOnce({
        id: 1,
        email: 'same@test.com',
        password: 'hashed-password',
      });
      (argon2.verify as jest.Mock).mockResolvedValue(true);

      await expect(
        authService.requestEmailChange(1, 'password', 'same@test.com'),
      ).rejects.toThrow('New email is the same as current email');
    });

    it('should throw ConflictException when new email is already taken', async () => {
      usersRepositoryMock.findOne
        .mockResolvedValueOnce({
          id: 1,
          email: 'old@test.com',
          password: 'hashed-password',
        })
        .mockResolvedValueOnce({
          id: 2,
          email: 'taken@test.com',
        });
      (argon2.verify as jest.Mock).mockResolvedValue(true);

      await expect(
        authService.requestEmailChange(1, 'password', 'taken@test.com'),
      ).rejects.toThrow(ConflictException);
    });

    it('should generate token, set pendingEmail, save and send email', async () => {
      const fakeUser = {
        id: 1,
        email: 'old@test.com',
        name: 'Test User',
        password: 'hashed-password',
        pendingEmail: undefined,
        verificationToken: undefined,
      };

      usersRepositoryMock.findOne
        .mockResolvedValueOnce(fakeUser)
        .mockResolvedValueOnce(null);
      (argon2.verify as jest.Mock).mockResolvedValue(true);
      authMailerServiceMock.sendVerificationEmail.mockResolvedValue(undefined);

      await authService.requestEmailChange(1, 'password', 'new@test.com');

      expect(usersRepositoryMock.save).toHaveBeenCalledWith(
        expect.objectContaining({
          pendingEmail: 'new@test.com',
          verificationToken: expect.any(String),
        }),
      );
      expect(authMailerServiceMock.sendVerificationEmail).toHaveBeenCalledWith(
        'new@test.com',
        'Test User',
        expect.any(String),
      );
    });
  });

  describe('updateProfile', () => {
    it('should update only provided fields', async () => {
      usersRepositoryMock.update.mockResolvedValue({ affected: 1 });

      await authService.updateProfile(1, { name: 'New Name' });

      expect(usersRepositoryMock.update).toHaveBeenCalledWith(1, {
        name: 'New Name',
      });
    });

    it('should update all fields when provided', async () => {
      usersRepositoryMock.update.mockResolvedValue({ affected: 1 });

      await authService.updateProfile(1, {
        name: 'New Name',
        phone: '01234567890',
        birthDate: '1990-06-15',
        gender: 'female',
      });

      expect(usersRepositoryMock.update).toHaveBeenCalledWith(1, {
        name: 'New Name',
        phone: '01234567890',
        birthDate: expect.any(Date),
        gender: 'female',
      });
    });

    it('should throw NotFoundException when user does not exist', async () => {
      usersRepositoryMock.update.mockResolvedValue({ affected: 0 });

      await expect(
        authService.updateProfile(999, { name: 'Ghost' }),
      ).rejects.toThrow(NotFoundException);
    });
  });
});
