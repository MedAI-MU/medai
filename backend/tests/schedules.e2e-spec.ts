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
import { SchedulesModule } from '../src/schedules/schedules.module';
import { PagedListDto } from '../src/shared/dtos/paged-list.dto';
import { DocScheduleTemplateDto } from '../src/schedules/dtos/doc-schedule-template.dto';
import {
  DocScheduleDto,
  DocScheduleDayDto,
} from '../src/schedules/dtos/doc-schedule.dto';
import * as argon2 from 'argon2';

describe('SchedulesController (e2e)', () => {
  let app: INestApplication;
  let dataSource: DataSource;

  const REGISTER_USER_URL = '/api/users';
  const LOGIN_USER_URL = '/api/auth/login';
  const CREATE_SCHEDULE_TEMPLATE_URL = '/api/doctors/schedules/templates';
  const UPDATE_SCHEDULE_TEMPLATE_URL = '/api/doctors/schedules/templates';
  const DELETE_SCHEDULE_TEMPLATE_URL = '/api/doctors/schedules/templates';
  const CREATE_SCHEDULE_SLOTS_URL = '/api/doctors/schedules';
  const UPDATE_SCHEDULE_SLOT_URL = '/api/doctors/schedules/slots';
  const DELETE_SCHEDULE_SLOT_URL = '/api/doctors/schedules/slots';

  let authCookie: string;
  let doctorUserId: number;
  let doctorAuthCookie: string;
  let anotherDoctorUserId: number;
  let anotherDoctorAuthCookie: string;

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [
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
        UsersModule,
        AuthModule,
        SchedulesModule,
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

    // Create users once for all tests
    // Seed a secretary user directly in the database
    const registerDto: RegisterDto = {
      email: 'secretary@test.com',
      password: 'strongPassword123',
      name: 'Test Secretary',
      phone: '01123456789',
      role: 'secretary',
    };

    const secretary = new User({
      email: registerDto.email,
      password: await argon2.hash(registerDto.password),
      name: registerDto.name,
      phone: registerDto.phone,
      role: registerDto.role,
    });
    await dataSource.getRepository(User).save(secretary);

    const loginDto = {
      email: registerDto.email,
      password: registerDto.password,
    };

    const loginResponse = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send(loginDto)
      .expect(200);

    const setCookies = loginResponse.headers['set-cookie'] as string | string[];
    const cookiesArray = Array.isArray(setCookies) ? setCookies : [setCookies];
    const foundAuthCookie = cookiesArray.find((c) =>
      c.startsWith('Authentication='),
    );
    expect(foundAuthCookie).toBeDefined();
    authCookie = foundAuthCookie as string;

    // Register a doctor user and get their ID
    const doctorRegisterDto: RegisterDto = {
      email: 'doctor@test.com',
      password: 'strongPassword123',
      name: 'Test Doctor',
      phone: '01198765432',
      role: 'doctor',
    };

    await request(app.getHttpServer() as App)
      .post(REGISTER_USER_URL)
      .send(doctorRegisterDto)
      .expect(201);

    // Get the doctor user from the database
    const doctorUser = await dataSource
      .getRepository(User)
      .findOne({ where: { email: 'doctor@test.com' } });
    expect(doctorUser).toBeDefined();
    doctorUserId = doctorUser!.id;

    // Manually create a Doctor entity for the doctor user
    const doctor = new Doctor({
      userId: doctorUserId,
      specialty: 'General Practitioner',
    });
    await dataSource.getRepository(Doctor).save(doctor);

    // Login as doctor to get their auth cookie
    const doctorLoginDto = {
      email: 'doctor@test.com',
      password: 'strongPassword123',
    };

    const doctorLoginResponse = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send(doctorLoginDto)
      .expect(200);

    const doctorSetCookies = doctorLoginResponse.headers['set-cookie'] as
      | string
      | string[];
    const doctorCookiesArray = Array.isArray(doctorSetCookies)
      ? doctorSetCookies
      : [doctorSetCookies];
    const foundDoctorAuthCookie = doctorCookiesArray.find((c) =>
      c.startsWith('Authentication='),
    );
    expect(foundDoctorAuthCookie).toBeDefined();
    doctorAuthCookie = foundDoctorAuthCookie as string;

    // Register another doctor user
    const anotherDoctorRegisterDto: RegisterDto = {
      email: 'doctor2@test.com',
      password: 'strongPassword123',
      name: 'Test Doctor 2',
      phone: '01187654321',
      role: 'doctor',
    };

    await request(app.getHttpServer() as App)
      .post(REGISTER_USER_URL)
      .send(anotherDoctorRegisterDto)
      .expect(201);

    // Get the second doctor user from the database
    const anotherDoctorUser = await dataSource
      .getRepository(User)
      .findOne({ where: { email: 'doctor2@test.com' } });
    expect(anotherDoctorUser).toBeDefined();
    anotherDoctorUserId = anotherDoctorUser!.id;

    // Manually create a Doctor entity for the second doctor user
    const anotherDoctor = new Doctor({
      userId: anotherDoctorUserId,
      specialty: 'Cardiologist',
    });
    await dataSource.getRepository(Doctor).save(anotherDoctor);

    // Login as second doctor to get their auth cookie
    const anotherDoctorLoginDto = {
      email: 'doctor2@test.com',
      password: 'strongPassword123',
    };

    const anotherDoctorLoginResponse = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send(anotherDoctorLoginDto)
      .expect(200);

    const anotherDoctorSetCookies = anotherDoctorLoginResponse.headers[
      'set-cookie'
    ] as string | string[];
    const anotherDoctorCookiesArray = Array.isArray(anotherDoctorSetCookies)
      ? anotherDoctorSetCookies
      : [anotherDoctorSetCookies];
    const foundAnotherDoctorAuthCookie = anotherDoctorCookiesArray.find((c) =>
      c.startsWith('Authentication='),
    );
    expect(foundAnotherDoctorAuthCookie).toBeDefined();
    anotherDoctorAuthCookie = foundAnotherDoctorAuthCookie as string;
  });

  beforeEach(async () => {
    // Only clear the data that changes between tests instead of full synchronize
    await dataSource.getRepository(DocScheduleSlot).clear();
    await dataSource.getRepository(DocSchedule).clear();
    await dataSource.getRepository(DocScheduleTemplateSlot).clear();
    await dataSource.getRepository(DocScheduleTemplate).clear();
  });

  describe('/api/doctors/schedules/templates (GET)', () => {
    beforeEach(async () => {
      // Create multiple templates for testing
      const templates = [
        {
          name: 'Morning Clinic',
          doctorId: doctorUserId,
          slots: [
            { weekDay: 1, startTime: '09:00', endTime: '12:00' },
            { weekDay: 3, startTime: '09:00', endTime: '12:00' },
          ],
        },
        {
          name: 'Evening Clinic',
          doctorId: doctorUserId,
          slots: [
            { weekDay: 2, startTime: '14:00', endTime: '17:00' },
            { weekDay: 4, startTime: '14:00', endTime: '17:00' },
          ],
        },
        {
          name: 'Weekend Schedule',
          doctorId: doctorUserId,
          slots: [{ weekDay: 5, startTime: '10:00', endTime: '13:00' }],
        },
      ];

      for (const template of templates) {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_TEMPLATE_URL)
          .set('Cookie', authCookie)
          .send(template)
          .expect(201);
      }
    });

    it('should return paginated templates with default pageNo and pageSize', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body).toHaveProperty('data');
      expect(body).toHaveProperty('totalCount');
      expect(body).toHaveProperty('currentPage', 1);
      expect(body).toHaveProperty('pageSize', 10);
      expect(body).toHaveProperty('hasNext');
      expect(body).toHaveProperty('hasPrevious');
      expect(Array.isArray(body.data)).toBe(true);
      expect(body.totalCount).toBeGreaterThanOrEqual(3);
    });

    it('should return templates with correct structure', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      const template = body.data[0];
      expect(template).toHaveProperty('id');
      expect(template).toHaveProperty('name');
      expect(template).toHaveProperty('doctor');
      expect(template.doctor).toHaveProperty('id');
      expect(template.doctor).toHaveProperty('name');
      expect(template.doctor).toHaveProperty('specialty');
      expect(template).toHaveProperty('slots');
      expect(Array.isArray(template.slots)).toBe(true);
      expect(template).toHaveProperty('createdBy');
      expect(template.createdBy).toHaveProperty('id');
      expect(template.createdBy).toHaveProperty('name');
      expect(template).toHaveProperty('createdAt');
      expect(template).toHaveProperty('updatedAt');
    });

    it('should apply custom pagination', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?pageNo=1&pageSize=2`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.currentPage).toBe(1);
      expect(body.pageSize).toBe(2);
      expect(body.data.length).toBeLessThanOrEqual(2);
    });

    // Skipped: Requires PostgreSQL pg_trgm extension (similarity function)
    // TODO: Test with testcontainers using actual PostgreSQL
    it.skip('should filter templates by name', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?name=Morning`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data.length).toBeGreaterThan(0);
      const hasMatchingName = body.data.some((template) =>
        template.name.toLowerCase().includes('morning'),
      );
      expect(hasMatchingName).toBe(true);
    });

    it('should filter templates by doctorId', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?doctorId=${doctorUserId}`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data.length).toBeGreaterThan(0);
      body.data.forEach((template) => {
        expect(template.doctor.id).toBe(doctorUserId);
      });
    });

    it('should return empty data for non-existent doctor', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?doctorId=9999`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data).toEqual([]);
      expect(body.totalCount).toBe(0);
    });

    it('should order slots by weekDay and startTime', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      const templateWithMultipleSlots = body.data.find(
        (t) => t.slots.length > 1,
      );

      if (templateWithMultipleSlots) {
        const slots = templateWithMultipleSlots.slots;
        for (let i = 1; i < slots.length; i++) {
          const prevSlot = slots[i - 1];
          const currentSlot = slots[i];
          expect(prevSlot.weekDay).toBeLessThanOrEqual(currentSlot.weekDay);
        }
      }
    });

    // Skipped: Requires PostgreSQL pg_trgm extension (similarity function)
    // TODO: Test with testcontainers using actual PostgreSQL
    it.skip('should combine name and doctorId filters', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(
          `${CREATE_SCHEDULE_TEMPLATE_URL}?name=Morning&doctorId=${doctorUserId}`,
        )
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      body.data.forEach((template) => {
        expect(template.doctor.id).toBe(doctorUserId);
      });
    });
  });

  describe('/api/doctors/schedules/templates (POST)', () => {
    const invalidCreateTemplateDtoTestCases = [
      {
        name: 'T',
        doctorId: 1,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 'invalid',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [
          {
            weekDay: 7,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [
          {
            weekDay: 0,
            startTime: 'invalid',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: 'invalid',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [
          {
            weekDay: 0,
            startTime: '10:00',
            endTime: '09:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
        extraField: 'notallowed',
      },
    ];

    it.each(invalidCreateTemplateDtoTestCases)(
      'should return 400 for invalid create template dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_TEMPLATE_URL)
          .set('Cookie', authCookie)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should return 400 when doctor does not exist', async () => {
      const dto = {
        name: 'Valid Template Name',
        doctorId: 9999,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });

    it('should create a schedule template and return 201 status', async () => {
      const dto = {
        name: 'Morning Shifts',
        doctorId: doctorUserId,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            weekDay: 1,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(201);
    });

    it('should return 400 for overlapping time slots in the same day', async () => {
      const dto = {
        name: 'Template with overlapping slots',
        doctorId: doctorUserId,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            weekDay: 0,
            startTime: '11:00',
            endTime: '14:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/templates/:id (PATCH)', () => {
    let templateId: number;

    beforeEach(async () => {
      // Create a template first
      const createDto = {
        name: 'Original Template',
        doctorId: doctorUserId,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created template ID from the database
      const template = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { name: 'Original Template' } });
      expect(template).toBeDefined();
      templateId = template!.id;
    });

    it('should return 400 when template does not exist', async () => {
      const updateDto = {
        name: 'Updated Template Name',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/9999`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });

    it('should update template name and return 204 status', async () => {
      const updateDto = {
        name: 'Updated Template Name',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { id: templateId } });
      expect(updatedTemplate).toBeDefined();
      expect(updatedTemplate!.name).toBe('Updated Template Name');
    });

    it('should update template slots and return 204 status', async () => {
      const updateDto = {
        slots: [
          {
            weekDay: 1,
            startTime: '14:00',
            endTime: '17:00',
          },
          {
            weekDay: 2,
            startTime: '14:00',
            endTime: '17:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the slots were updated
      const updatedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({
          where: { id: templateId },
          relations: ['slots'],
        });
      expect(updatedTemplate).toBeDefined();
      expect(updatedTemplate!.slots).toHaveLength(2);
    });

    it('should return 400 for overlapping time slots in update', async () => {
      const updateDto = {
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            weekDay: 0,
            startTime: '11:00',
            endTime: '14:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/templates/:id (DELETE)', () => {
    let templateId: number;

    beforeEach(async () => {
      // Create a template first
      const createDto = {
        name: 'Template to delete',
        doctorId: doctorUserId,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created template ID
      const template = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { name: 'Template to delete' } });
      expect(template).toBeDefined();
      templateId = template!.id;
    });

    it('should return 400 when template does not exist', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_TEMPLATE_URL}/9999`)
        .set('Cookie', authCookie)
        .expect(400);
    });

    it('should delete a template and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
        .set('Cookie', authCookie)
        .expect(204);

      // Verify the template was deleted
      const deletedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { id: templateId } });
      expect(deletedTemplate).toBeNull();
    });
  });

  describe('/api/doctors/schedules (POST)', () => {
    const getInvalidCreateScheduleDtoTestCases = () => [
      {
        doctorId: 'invalid',
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      },
      {
        doctorId: doctorUserId,
        days: [],
      },
      {
        doctorId: doctorUserId,
        days: [
          {
            date: 'invalid-date',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      },
      {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: 'invalid',
                endTime: '10:00',
              },
            ],
          },
        ],
      },
      {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '10:00',
                endTime: '09:00',
              },
            ],
          },
        ],
      },
      {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
        extraField: 'notallowed',
      },
    ];

    it.each(getInvalidCreateScheduleDtoTestCases())(
      'should return 400 for invalid create schedule dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_SLOTS_URL)
          .set('Cookie', authCookie)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should return 404 when doctor does not exist', async () => {
      const dto = {
        doctorId: 9999,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(404);
    });

    it('should create schedule slots and return 201 status', async () => {
      const dto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
          {
            date: '2026-02-02',
            slots: [
              {
                startTime: '14:00',
                endTime: '15:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(201);
    });

    it('should create schedule slots successfully as doctor for themselves', async () => {
      const dto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-03',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', doctorAuthCookie)
        .send(dto)
        .expect(201);
    });

    it('should return 401 when doctor tries to create schedule for another doctor', async () => {
      const dto = {
        doctorId: anotherDoctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', doctorAuthCookie)
        .send(dto)
        .expect(401);
    });

    it('should return 400 for overlapping time slots on the same day', async () => {
      const dto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '12:00',
              },
              {
                startTime: '11:00',
                endTime: '14:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });
  });

  describe('/api/doctors/:doctorId/schedules (GET)', () => {
    const GET_DOCTOR_SCHEDULES_URL = '/api/doctors';

    beforeEach(async () => {
      // Create multiple schedule slots for the doctor
      const createDto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-10',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
              {
                startTime: '11:00',
                endTime: '12:00',
              },
            ],
          },
          {
            date: '2026-02-11',
            slots: [
              {
                startTime: '14:00',
                endTime: '15:00',
              },
            ],
          },
          {
            date: '2026-02-15',
            slots: [
              {
                startTime: '10:00',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);
    });

    it('should return 404 when doctor does not exist', async () => {
      await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/9999/schedules`)
        .set('Cookie', authCookie)
        .expect(404);
    });

    it('should get doctor schedules with default pagination', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body).toHaveProperty('doctorId', doctorUserId);
      expect(body).toHaveProperty('name');
      expect(body).toHaveProperty('speciality');
      expect(body).toHaveProperty('days');
      expect(body.days).toHaveProperty('data');
      expect(body.days).toHaveProperty('totalCount', 3);
      expect(body.days).toHaveProperty('currentPage', 1);
      expect(body.days).toHaveProperty('pageSize', 10);
      expect(body.days.data).toHaveLength(3);
    });

    it('should return schedules with correct structure', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      const firstDay = body.days.data[0];
      expect(firstDay).toHaveProperty('day');
      expect(firstDay).toHaveProperty('slots');
      expect(Array.isArray(firstDay.slots)).toBe(true);
      expect(firstDay.slots[0]).toHaveProperty('id');
      expect(firstDay.slots[0]).toHaveProperty('startTime');
      expect(firstDay.slots[0]).toHaveProperty('endTime');
      expect(firstDay.slots[0]).toHaveProperty('status');
    });

    it('should apply custom pagination', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .query({ pageNo: 1, pageSize: 2 })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(3);
      expect(body.days.currentPage).toBe(1);
      expect(body.days.pageSize).toBe(2);
      expect(body.days.data).toHaveLength(2);
    });

    it('should filter schedules by fromDate', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .query({ fromDate: '2026-02-11' })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(2);
      expect(body.days.data[0].day).toBe('2026-02-11');
    });

    it('should filter schedules by toDate', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .query({ toDate: '2026-02-11' })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(2);
      expect(body.days.data[1].day).toBe('2026-02-11');
    });

    it('should filter schedules by date range', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .query({ fromDate: '2026-02-10', toDate: '2026-02-11' })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(2);
      expect(body.days.data[0].day).toBe('2026-02-10');
      expect(body.days.data[1].day).toBe('2026-02-11');
    });

    it('should order schedules by date ascending', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      const days = body.days.data.map((d: DocScheduleDayDto) => d.day);
      expect(days).toEqual(['2026-02-10', '2026-02-11', '2026-02-15']);
    });

    it('should order slots within a day by startTime ascending', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${doctorUserId}/schedules`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      const firstDaySlots = body.days.data[0].slots;
      expect(firstDaySlots[0].startTime).toContain('09:00');
      expect(firstDaySlots[1].startTime).toContain('11:00');
    });

    it('should return empty data for doctor with no schedules', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_DOCTOR_SCHEDULES_URL}/${anotherDoctorUserId}/schedules`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(0);
      expect(body.days.data).toHaveLength(0);
    });
  });

  describe('/api/doctors/schedules/slots/:id (PATCH)', () => {
    let slotId: number;
    let doctorOwnSlotId: number;

    beforeEach(async () => {
      // Secretary creates a schedule slot for doctorUserId
      const createDto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created slot ID
      const slot = await dataSource.getRepository(DocScheduleSlot).findOne({
        where: {},
        order: { id: 'DESC' },
      });
      expect(slot).toBeDefined();
      slotId = slot!.id;

      // Doctor creates their own slot
      const doctorCreateDto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-05',
            slots: [
              {
                startTime: '10:00',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', doctorAuthCookie)
        .send(doctorCreateDto)
        .expect(201);

      // Get the doctor's own slot ID
      const doctorSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({
          where: {},
          order: { id: 'DESC' },
        });
      expect(doctorSlot).toBeDefined();
      doctorOwnSlotId = doctorSlot!.id;
    });

    it('should return 404 when slot does not exist', async () => {
      const updateDto = {
        startTime: '11:00',
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/9999`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(404);
    });

    it('should update slot start time as secretary and return 204 status', async () => {
      const updateDto = {
        startTime: '08:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(updatedSlot).toBeDefined();
      expect(updatedSlot!.startTime).toContain('08:00');
    });

    it('should update slot as doctor for their own slot and return 204 status', async () => {
      const updateDto = {
        startTime: '11:00',
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${doctorOwnSlotId}`)
        .set('Cookie', doctorAuthCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: doctorOwnSlotId } });
      expect(updatedSlot).toBeDefined();
      expect(updatedSlot!.startTime).toContain('11:00');
    });

    it('should return 401 when doctor tries to update another doctors slot', async () => {
      const updateDto = {
        startTime: '11:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', anotherDoctorAuthCookie)
        .send(updateDto)
        .expect(401);
    });

    it('should update slot end time and return 204 status', async () => {
      const updateDto = {
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(updatedSlot).toBeDefined();
      expect(updatedSlot!.endTime).toContain('12:00');
    });

    it('should return 400 when updating with invalid time range', async () => {
      const updateDto = {
        startTime: '12:00',
        endTime: '11:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/slots/:id (DELETE)', () => {
    let slotId: number;
    let doctorOwnSlotId: number;

    beforeEach(async () => {
      // Secretary creates a schedule slot
      const createDto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created slot ID
      const slot = await dataSource.getRepository(DocScheduleSlot).findOne({
        where: {},
        order: { id: 'DESC' },
      });
      expect(slot).toBeDefined();
      slotId = slot!.id;

      // Doctor creates their own slot
      const doctorCreateDto = {
        doctorId: doctorUserId,
        days: [
          {
            date: '2026-02-06',
            slots: [
              {
                startTime: '10:00',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', doctorAuthCookie)
        .send(doctorCreateDto)
        .expect(201);

      // Get the doctor's own slot ID
      const doctorSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({
          where: {},
          order: { id: 'DESC' },
        });
      expect(doctorSlot).toBeDefined();
      doctorOwnSlotId = doctorSlot!.id;
    });

    it('should return 400 when slot does not exist', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL}/9999`)
        .set('Cookie', authCookie)
        .expect(400);
    });

    it('should delete a slot as secretary and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', authCookie)
        .expect(204);

      // Verify the slot was deleted
      const deletedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(deletedSlot).toBeNull();
    });

    it('should delete a slot as doctor for their own slot and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL}/${doctorOwnSlotId}`)
        .set('Cookie', doctorAuthCookie)
        .expect(204);

      // Verify the slot was deleted
      const deletedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: doctorOwnSlotId } });
      expect(deletedSlot).toBeNull();
    });

    it('should return 400 when doctor tries to delete another doctors slot', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', anotherDoctorAuthCookie)
        .expect(400);
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
