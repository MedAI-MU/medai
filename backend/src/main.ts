import { NestFactory } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
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

  const config = new DocumentBuilder()
    .setTitle('MedAI API')
    .setDescription('API documentation for MedAI')
    .setVersion('1.0')
    .build();

  SwaggerModule.setup(
    'api/docs',
    app,
    SwaggerModule.createDocument(app, config),
    {
      jsonDocumentUrl: 'api/docs/json',
    },
  );
  await app.listen(process.env.APP_PORT || 8000);
}
bootstrap().catch((err) => {
  console.error('Error during application bootstrap:', err);
});
