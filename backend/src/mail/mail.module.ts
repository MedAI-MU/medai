import { Module } from '@nestjs/common';
import { MailerModule } from '@nestjs-modules/mailer';
import { ConfigService } from '@nestjs/config';

@Module({
  imports: [
    MailerModule.forRootAsync({
      inject: [ConfigService],
      useFactory: (configService: ConfigService) => ({
        transport: {
          host: configService.get('BREVO_SMTP_HOST'),
          port: parseInt(configService.get('BREVO_SMTP_PORT') || '587', 10),
          secure: false,
          auth: {
            user: configService.get('BREVO_SMTP_USER'),
            pass: configService.get('BREVO_SMTP_PASS'),
          },
        },
        defaults: {
          from: configService.get('BREVO_DEFAULT_FROM'),
        },
      }),
    }),
  ],
  exports: [MailerModule],
})
export class MailModule {}
