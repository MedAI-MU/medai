import { Test } from '@nestjs/testing';
import { AuthMailerService } from './auth-mailer.service';
import { MailerService } from '@nestjs-modules/mailer';

describe('AuthMailerService', () => {
  let authMailerService: AuthMailerService;
  let mailerService: MailerService;

  beforeEach(async () => {
    const module = await Test.createTestingModule({
      providers: [
        AuthMailerService,
        {
          provide: MailerService,
          useValue: {
            sendMail: jest.fn().mockResolvedValue(undefined),
          },
        },
      ],
    }).compile();

    authMailerService = module.get<AuthMailerService>(AuthMailerService);
    mailerService = module.get<MailerService>(MailerService);

    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(authMailerService).toBeDefined();
  });

  describe('sendVerificationEmail', () => {
    it('should send verification email with correct link', async () => {
      const originalUrl = process.env.FRONTEND_URL;
      process.env.FRONTEND_URL = 'http://localhost:3000';

      await authMailerService.sendVerificationEmail(
        'test@test.com',
        'Test User',
        'token123',
      );

      expect(mailerService.sendMail).toHaveBeenCalledWith({
        to: 'test@test.com',
        subject: 'Verify your MedAI account',
        html: expect.stringContaining(
          'http://localhost:3000/verify-email?token=token123',
        ),
      });

      process.env.FRONTEND_URL = originalUrl;
    });

    it('should use default URL when FRONTEND_URL is not set', async () => {
      const originalUrl = process.env.FRONTEND_URL;
      delete process.env.FRONTEND_URL;

      await authMailerService.sendVerificationEmail(
        'test@test.com',
        'Test User',
        'token123',
      );

      expect(mailerService.sendMail).toHaveBeenCalledWith({
        to: 'test@test.com',
        subject: 'Verify your MedAI account',
        html: expect.stringContaining(
          'http://localhost:3000/verify-email?token=token123',
        ),
      });

      process.env.FRONTEND_URL = originalUrl;
    });
  });

  describe('sendPasswordResetEmail', () => {
    it('should send password reset email with correct link', async () => {
      const originalUrl = process.env.FRONTEND_URL;
      process.env.FRONTEND_URL = 'http://localhost:3000';

      await authMailerService.sendPasswordResetEmail(
        'test@test.com',
        'Test User',
        'token123',
      );

      expect(mailerService.sendMail).toHaveBeenCalledWith({
        to: 'test@test.com',
        subject: 'Reset your MedAI password',
        html: expect.stringContaining(
          'http://localhost:3000/reset-password?token=token123',
        ),
      });

      process.env.FRONTEND_URL = originalUrl;
    });
  });
});
