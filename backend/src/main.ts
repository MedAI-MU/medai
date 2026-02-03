import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { BadRequestException, ValidationPipe } from '@nestjs/common';
import cookieParser from 'cookie-parser';
import { ValidationError } from 'class-validator';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
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
  await app.listen(process.env.APP_PORT || 8000);
}
bootstrap().catch((err) => {
  console.error('Error during application bootstrap:', err);
});
