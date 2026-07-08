import { Injectable } from '@nestjs/common';
import { MailerService } from '@nestjs-modules/mailer';

@Injectable()
export class AuthMailerService {
  constructor(private readonly mailerService: MailerService) {}

  private getBaseUrl(): string {
    return process.env.FRONTEND_URL || 'http://localhost:3000';
  }

  async sendVerificationEmail(
    email: string,
    name: string,
    token: string,
  ): Promise<void> {
    const link = `${this.getBaseUrl()}/verify-email?token=${token}`;
    await this.mailerService.sendMail({
      to: email,
      subject: 'Verify your MedAI account',
      html: `<p>Hi ${name},</p><p>Click <a href="${link}">here</a> to verify your email address.</p><p>This link expires in 24 hours.</p>`,
    });
  }

  async sendPasswordResetEmail(
    email: string,
    name: string,
    token: string,
  ): Promise<void> {
    const link = `${this.getBaseUrl()}/reset-password?token=${token}`;
    await this.mailerService.sendMail({
      to: email,
      subject: 'Reset your MedAI password',
      html: `<p>Hi ${name},</p><p>Click <a href="${link}">here</a> to reset your password.</p><p>This link expires in 1 hour.</p><p>If you did not request this, please ignore this email.</p>`,
    });
  }
}
