import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { BadRequestException, ValidationPipe } from '@nestjs/common';
import cookieParser from 'cookie-parser';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  app.setGlobalPrefix('api');
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidNonWhitelisted: true,
      exceptionFactory: (errors) => {
        const messages = errors.map((err) => ({
          field: err.property,
          errors: Object.values(err.constraints!),
        }));

        return new BadRequestException({
          success: false,
          message: 'Validation failed',
          validationErrors: messages,
        });
      },
    }),
  );
  app.use(cookieParser());
  console.log(process.env.APP_PORT);
  await app.listen(process.env.APP_PORT || 8000);
}
bootstrap().catch((err) => {
  console.error('Error during application bootstrap:', err);
});
