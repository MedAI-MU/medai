import request from 'supertest';
import { Test } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { UsersModule } from 'src/users/users.module';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { RegisterDto } from 'src/users/dtos/register.dto';
import { AuthModule } from 'src/auth/auth.module';
import cookieParser from 'cookie-parser';
import { ConfigModule } from '@nestjs/config';
import jwtConfig from 'src/auth/jwt.config';
import { App } from 'supertest/types';
import { DataSource } from 'typeorm';
import { DocScheduleTemplate } from '../src/schedules/entities/doc-schedule-template.entity';
import { DocScheduleSlot } from '../src/schedules/entities/doc-schedule-slot.entity';
import { DocScheduleTemplateSlot } from '../src/schedules/entities/doc-schedule-template-slot.entity';
import { DocSchedule } from '../src/schedules/entities/doc-schedule.entity';
import { Doctor } from '../src/doctors/entities/doctor.entity';

describe('AuthController (e2e)', () => {
  let app: INestApplication;
  let dataSource: DataSource;

  const REGISTER_USER_URL = '/api/users';
  const LOGIN_USER_URL = '/api/auth/login';
  const REFRESH_TOKEN_URL = '/api/auth/refresh-token';

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [
        UsersModule,
        AuthModule,
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
        ConfigModule.forRoot({ isGlobal: true, load: [jwtConfig] }),
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
    app.use(cookieParser());

    await app.init();

    dataSource = moduleRef.get(DataSource);
  });

  beforeEach(async () => {
    // Only clear the data that changes between tests instead of full synchronize
    await dataSource.getRepository(RefreshToken).clear();
    await dataSource.getRepository(User).clear();
    await dataSource.getRepository(Doctor).clear();
  });

  describe('/api/auth/login (POST)', () => {
    const invalidLoginDtotestCases = [
      { email: 'invalid-email', password: 'validPass123' },
      { email: '', password: 'validPass123' },
      { email: 'user@example.com' },
      { email: 'user2@example.com', password: 123456 }, // non-string password
    ];

    it.each(invalidLoginDtotestCases)(
      'should return 400 for invalid login dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(LOGIN_USER_URL)
          .send(invalidDto)
          .expect(400);
      },
    );

    it("should return 400 for invalid credentials (user don't exist with this email and password)", async () => {
      const invalidCredentialsDto = {
        email: 'doesnotexist@test.com',
        password: 'somepassword',
      };
      await request(app.getHttpServer() as App)
        .post(LOGIN_USER_URL)
        .send(invalidCredentialsDto)
        .expect(400);
    });

    it('should return 200, set authentication cookie and refresh cookie for correct credentials', async () => {
      const registerDto: RegisterDto = {
        email: 'test@test.com',
        password: 'strongPassword',
        name: 'Test User',
        phone: '01123456789',
        role: 'doctor',
      };
      // first register the user
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(registerDto)
        .expect(201);

      const loginDto = {
        email: registerDto.email,
        password: registerDto.password,
      };
      const response = await request(app.getHttpServer() as App)
        .post(LOGIN_USER_URL)
        .send(loginDto)
        .expect(200);

      const setCookies = response.headers['set-cookie'] as string | string[];
      expect(setCookies).toBeDefined();
      const cookiesArray = Array.isArray(setCookies)
        ? setCookies
        : [setCookies];

      const authCookie = cookiesArray.find((c) =>
        c.startsWith('Authentication='),
      );
      expect(authCookie).toBeDefined();
      expect(authCookie).toContain('Path=/');

      const refreshCookie = cookiesArray.find((c) => c.startsWith('Refresh='));
      expect(refreshCookie).toBeDefined();
      expect(refreshCookie).toContain('Path=/api/auth');
    });

    it('should replace existing refresh token and access token on re-login', async () => {
      const registerDto: RegisterDto = {
        email: 'test@test.com',
        password: 'strongPassword',
        name: 'Test User',
        phone: '01123456789',
        role: 'doctor',
      };

      // first register the user
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(registerDto)
        .expect(201);

      const loginDto = {
        email: registerDto.email,
        password: registerDto.password,
      };

      const firstLoginResponse = await request(app.getHttpServer() as App)
        .post(LOGIN_USER_URL)
        .send(loginDto)
        .expect(200);

      const firstSetCookies = firstLoginResponse.headers['set-cookie'] as
        | string
        | string[];
      const firstCookiesArray = Array.isArray(firstSetCookies)
        ? firstSetCookies
        : [firstSetCookies];
      const firstAuthCookie = firstCookiesArray.find((c) =>
        c.startsWith('Authentication='),
      );
      const firstRefreshCookie = firstCookiesArray.find((c) =>
        c.startsWith('Refresh='),
      );

      await new Promise((res) => setTimeout(res, 1000)); // ensure some time difference to get different tokens

      const secondLoginResponse = await request(app.getHttpServer() as App)
        .post(LOGIN_USER_URL)
        .set('Cookie', [firstAuthCookie, firstRefreshCookie].join('; '))
        .send(loginDto)
        .expect(200);

      const secondSetCookies = secondLoginResponse.headers['set-cookie'] as
        | string
        | string[];
      const secondCookiesArray = Array.isArray(secondSetCookies)
        ? secondSetCookies
        : [secondSetCookies];
      const secondAuthCookie = secondCookiesArray.find((c) =>
        c.startsWith('Authentication='),
      );
      const secondRefreshCookie = secondCookiesArray.find((c) =>
        c.startsWith('Refresh='),
      );

      expect(firstAuthCookie).toBeDefined();
      expect(firstRefreshCookie).toBeDefined();
      expect(secondAuthCookie).toBeDefined();
      expect(secondRefreshCookie).toBeDefined();

      expect(firstAuthCookie).not.toEqual(secondAuthCookie);
      expect(firstRefreshCookie).not.toEqual(secondRefreshCookie);
    });
  });

  describe('/api/auth/refresh-token (POST)', () => {
    it('should return 401 if no refresh token cookie is provided', async () => {
      await request(app.getHttpServer() as App)
        .post(REFRESH_TOKEN_URL)
        .expect(401);
    });

    it('should return 401 if invalid refresh token cookie is provided', async () => {
      await request(app.getHttpServer() as App)
        .post(REFRESH_TOKEN_URL)
        .set('Cookie', 'Refresh=invalidtoken; Path=/api/auth; HttpOnly')
        .send()
        .expect(401);
    });

    it('should return 200 and set new cookies when valid refresh token is provided', async () => {
      const registerDto: RegisterDto = {
        email: 'test@test.com',
        password: 'strongPassword',
        name: 'Test User',
        phone: '01123456789',
        role: 'doctor',
      };

      // first register the user
      await request(app.getHttpServer() as App)
        .post(REGISTER_USER_URL)
        .send(registerDto)
        .expect(201);

      const loginDto = {
        email: registerDto.email,
        password: registerDto.password,
      };

      const loginResponse = await request(app.getHttpServer() as App)
        .post(LOGIN_USER_URL)
        .send(loginDto)
        .expect(200);

      const setCookies = loginResponse.headers['set-cookie'] as
        | string
        | string[];
      const cookiesArray = Array.isArray(setCookies)
        ? setCookies
        : [setCookies];

      const refreshCookie = cookiesArray.find((c) => c.startsWith('Refresh='));
      expect(refreshCookie).toBeDefined();

      await new Promise((res) => setTimeout(res, 1000)); // ensure some time difference to get different tokens

      // Use the refresh token to get new tokens
      const refreshResponse = await request(app.getHttpServer() as App)
        .post(REFRESH_TOKEN_URL)
        .set('Cookie', refreshCookie as string)
        .send()
        .expect(200);

      const refreshSetCookies = refreshResponse.headers['set-cookie'] as
        | string
        | string[];
      expect(refreshSetCookies).toBeDefined();
      const refreshCookiesArray = Array.isArray(refreshSetCookies)
        ? refreshSetCookies
        : [refreshSetCookies];

      const newRefreshCookie = refreshCookiesArray.find((c) =>
        c.startsWith('Refresh='),
      );
      expect(newRefreshCookie).toBeDefined();
      const newAuthCookie = refreshCookiesArray.find((c) =>
        c.startsWith('Authentication='),
      );
      expect(newAuthCookie).toBeDefined();

      expect(newRefreshCookie).not.toEqual(refreshCookie);
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
