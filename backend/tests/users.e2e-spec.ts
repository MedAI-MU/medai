import request from 'supertest';
import { Test } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { UsersModule } from 'src/users/users.module';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { RegisterDto } from 'src/users/dtos/register.dto';
import { App } from 'supertest/types';
import { DataSource } from 'typeorm';
import { Doctor } from '../src/doctors/entities/doctor.entity';
import { DocScheduleTemplate } from '../src/schedules/entities/doc-schedule-template.entity';
import { DocScheduleSlot } from '../src/schedules/entities/doc-schedule-slot.entity';
import { DocScheduleTemplateSlot } from '../src/schedules/entities/doc-schedule-template-slot.entity';
import { DocSchedule } from '../src/schedules/entities/doc-schedule.entity';
import { UserRoles } from '../src/users/types/role.types';

describe('UsersController (e2e)', () => {
  let app: INestApplication;
  let dataSource: DataSource;

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
          entities: [
            User,
            RefreshToken,
            Doctor,
            DocSchedule,
            DocScheduleSlot,
            DocScheduleTemplate,
            DocScheduleTemplateSlot,
          ],
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

    dataSource = moduleRef.get(DataSource);
  });

  beforeEach(async () => {
    // Only clear the data that changes between tests instead of full synchronize
    await dataSource.getRepository(RefreshToken).clear();
    await dataSource.getRepository(User).clear();
    await dataSource.getRepository(Doctor).clear();
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
      {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
      },
      {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
        role: 10,
      },
      {
        name: 'Valid Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
        role: 'manager',
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
        name: 'Test Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
        role: UserRoles.DOCTOR,
      };
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(dto)
        .expect(201);
    });

    it('should return 409 if email is already in use', async () => {
      const firstDto: RegisterDto = {
        name: 'Test Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
        role: UserRoles.DOCTOR,
      };
      const duplicateEmailDto: RegisterDto = {
        name: 'Another Name',
        email: 'test@test.com',
        password: '654321',
        phone: '01087654321',
        role: UserRoles.DOCTOR,
      };
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(firstDto)
        .expect(201);

      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(duplicateEmailDto)
        .expect(409);
    });
    it('should return 409 if phone is already in use', async () => {
      const firstDto: RegisterDto = {
        name: 'Test Name',
        email: 'test@test.com',
        password: '123456',
        phone: '01012345678',
        role: UserRoles.DOCTOR,
      };
      const duplicatePhoneDto: RegisterDto = {
        name: 'Another Name',
        email: 'nottest@test.com',
        password: '654321',
        phone: '01012345678',
        role: UserRoles.DOCTOR,
      };
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(firstDto)
        .expect(201);

      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(duplicatePhoneDto)
        .expect(409);
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
