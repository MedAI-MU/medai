import request from 'supertest';
import { Test } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { UsersModule } from 'src/users/users.module';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { RegisterDto } from 'src/users/dtos/register.dto';
import { App } from 'supertest/types';

describe('UsersController (e2e)', () => {
  let app: INestApplication;

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [
        UsersModule,
        TypeOrmModule.forRoot({
          type: 'sqljs',
          autoSave: false,
          location: ':memory:',
          synchronize: true,
          dropSchema: true,
          entities: [User, RefreshToken],
        }),
      ],
    }).compile();

    app = moduleRef.createNestApplication();
    app.setGlobalPrefix('api');
    app.useGlobalPipes(
      new ValidationPipe({
        whitelist: true,
        transform: true,
        forbidNonWhitelisted: true,
      }),
    );
    await app.init();
  });

  describe('/api/users (POST)', () => {
    const REGISTER_USER_URL = '/api/users';
    const invalidRegisterDtotestCases = [
      {
        name: 'a',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
      },
      {
        name: 'Valid Name',
        email: 'invalidemail',
        password: '123456',
        phone: '01012345678',
      },
      {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123',
        phone: '01012345678',
      },
      {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123456',
        phone: '1234567890',
      },
      { name: '', email: 'invalid', password: '123', phone: '000' },
      {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
        extraField: 'notallowed',
      },
    ];

    it.each(invalidRegisterDtotestCases)(
      'should return 400 for invalid register dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(REGISTER_USER_URL)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should register a user and return 201 status', async () => {
      const dto: RegisterDto = {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
      };
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(dto)
        .expect(201);
    });

    it('should return 409 if email is already in use', async () => {
      const duplicateDto: RegisterDto = {
        name: 'Test Name',
        email: 'test2@test.com',
        password: '123456',
        phone: '01012345678',
      };
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(duplicateDto);

      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(duplicateDto)
        .expect(409);
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
