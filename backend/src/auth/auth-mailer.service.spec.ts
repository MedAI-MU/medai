import { Test } from '@nestjs/testing';
import { AuthMailerService } from './auth-mailer.service';
import { MailerService } from '@nestjs-modules/mailer';

const sendMailMock = jest.fn().mockResolvedValue(undefined);

describe('AuthMailerService', () => {
  let authMailerService: AuthMailerService;

  beforeEach(async () => {
    jest.clearAllMocks();

    const module = await Test.createTestingModule({
      providers: [
        AuthMailerService,
        {
          provide: MailerService,
          useValue: {
            sendMail: sendMailMock,
          },
        },
      ],
    }).compile();

    authMailerService = module.get<AuthMailerService>(AuthMailerService);
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

      const verifyEmailHtml: unknown = expect.stringContaining(
        'http://localhost:3000/verify-email?token=token123',
      );
      expect(sendMailMock).toHaveBeenCalledWith({
        to: 'test@test.com',
        subject: 'Verify your MedAI account',
        html: verifyEmailHtml,
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

      const verifyEmailHtml: unknown = expect.stringContaining(
        'http://localhost:3000/verify-email?token=token123',
      );
      expect(sendMailMock).toHaveBeenCalledWith({
        to: 'test@test.com',
        subject: 'Verify your MedAI account',
        html: verifyEmailHtml,
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

      const resetEmailHtml: unknown = expect.stringContaining(
        'http://localhost:3000/reset-password?token=token123',
      );
      expect(sendMailMock).toHaveBeenCalledWith({
        to: 'test@test.com',
        subject: 'Reset your MedAI password',
        html: resetEmailHtml,
      });

      process.env.FRONTEND_URL = originalUrl;
    });
  });
});
