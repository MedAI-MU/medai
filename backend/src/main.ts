import { NestFactory, Reflector } from '@nestjs/core';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';
import { AppModule } from './app.module';
import {
  BadRequestException,
  ClassSerializerInterceptor,
  ValidationPipe,
} from '@nestjs/common';
import cookieParser from 'cookie-parser';
import { ValidationError } from 'class-validator';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  // CORS CONFIGURATION
  app.enableCors({
    origin:
      process.env.NODE_ENV == 'production'
        ? process.env.FRONTEND_URL
        : 'http://localhost:3000',
    methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS', 'HEAD'],
    credentials: true,
    exposedHeaders: ['set-cookie'],
  });
  app.setGlobalPrefix('api');
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      transform: true,
      forbidNonWhitelisted: true,
      exceptionFactory: (errors: ValidationError[]) => {
        const flattenErrors = (
          errs: ValidationError[],
          parent = '',
        ): { field: string; error: string }[] =>
          errs.flatMap((err) => [
            ...(err.constraints
              ? Object.values(err.constraints).map((msg) => ({
                  field: parent ? `${parent}.${err.property}` : err.property,
                  error: msg,
                }))
              : []),
            ...(err.children?.length
              ? flattenErrors(
                  err.children,
                  parent ? `${parent}.${err.property}` : err.property,
                )
              : []),
          ]);

        return new BadRequestException({
          success: false,
          message: 'Validation failed',
          validationErrors: flattenErrors(errors),
        });
      },
    }),
  );

  app.use(cookieParser());
  app.useGlobalInterceptors(new ClassSerializerInterceptor(app.get(Reflector)));

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
