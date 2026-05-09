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
import { Doctor } from 'src/doctors/entities/doctor.entity';
import { DocScheduleTemplate } from 'src/schedules/entities/doc-schedule-template.entity';
import { DocScheduleSlot } from 'src/schedules/entities/doc-schedule-slot.entity';
import { DocScheduleTemplateSlot } from 'src/schedules/entities/doc-schedule-template-slot.entity';
import { DocSchedule } from 'src/schedules/entities/doc-schedule.entity';
import { PatientsModule } from 'src/patients/patients.module';
import { Patient } from 'src/patients/entities/patient.entity';
import { Allergy } from 'src/patients/entities/allergy.entity';
import { ChronicDisease } from 'src/patients/entities/chronic_disease.entity';
import { Surgery } from 'src/patients/entities/surgery.entity';
import { FamilyHistory } from 'src/patients/entities/family_history.entity';
import { EmergencyContact } from 'src/patients/entities/emergency_contact.entity';
import { PatientDto } from 'src/patients/dtos/patient.dto';
import type {
  BloodType,
  Gender,
  MaritalStatus,
} from 'src/patients/types/patient.types';

interface PatientResponse {
  userId: number;
  birthDate: string;
  height: number;
  weight: number;
  gender: Gender;
  bloodType: BloodType | null;
  maritalStatus: MaritalStatus | null;
  allergies: AllergyResponse[];
  chronicDiseases: ChronicDiseaseResponse[];
  surgeries: SurgeryResponse[];
  familyHistories: FamilyHistoryResponse[];
  emergencyContacts: EmergencyContactResponse[];
  createdAt: string;
  updatedAt: string;
}

interface AllergyResponse {
  id: number;
  name: string;
  description: string | null;
}

interface ChronicDiseaseResponse {
  id: number;
  name: string;
  description: string | null;
  diagnosisDate: string | null;
}

interface SurgeryResponse {
  id: number;
  name: string;
  description: string | null;
  date: string;
}

interface FamilyHistoryResponse {
  id: number;
  relation: string;
  condition: string;
  notes: string | null;
}

interface EmergencyContactResponse {
  id: number;
  name: string;
  relation: string;
  phoneNumber: string;
  email: string;
  address: string;
  notes: string | null;
}

describe('PatientsController (e2e)', () => {
  let app: INestApplication;
  let dataSource: DataSource;

  const REGISTER_USER_URL = '/api/users';
  const LOGIN_USER_URL = '/api/auth/login';
  const PATIENTS_URL = '/api/patients';

  const getAuthCookie = async (
    email: string,
    password: string,
  ): Promise<string> => {
    const response = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send({ email, password });

    const setCookies = response.headers['set-cookie'] as string | string[];
    const cookiesArray = Array.isArray(setCookies) ? setCookies : [setCookies];
    return cookiesArray.find((c) => c.startsWith('Authentication=')) || '';
  };

  const registerAndLogin = async (
    role: 'patient' | 'doctor' | 'secretary' = 'patient',
  ): Promise<{ authCookie: string; user: RegisterDto }> => {
    const registerDto: RegisterDto = {
      email: `test-${Date.now()}-${Math.random()}@test.com`,
      password: 'strongPassword123',
      name: 'Test User',
      phone: `011${Math.floor(10000000 + Math.random() * 90000000)}`,
      role,
    };

    await request(app.getHttpServer() as App)
      .post(REGISTER_USER_URL)
      .send(registerDto)
      .expect(201);

    const authCookie = await getAuthCookie(
      registerDto.email,
      registerDto.password,
    );

    return { authCookie, user: registerDto };
  };

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [
        UsersModule,
        AuthModule,
        PatientsModule,
        TypeOrmModule.forRoot({
          type: 'postgres',
          host: process.env.DB_HOST || 'localhost',
          port: parseInt(process.env.DB_PORT || '5432', 10),
          username: process.env.DB_USER || 'medai',
          password: process.env.DB_PASSWORD || 'password',
          database: process.env.DB_NAME_TEST || 'medai_db_test',
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
            Patient,
            Allergy,
            ChronicDisease,
            Surgery,
            FamilyHistory,
            EmergencyContact,
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
  }, 30000);

  beforeEach(async () => {
    // Clear all patient-related tables in correct order (respect foreign keys)
    await dataSource.getRepository(EmergencyContact).delete({});
    await dataSource.getRepository(FamilyHistory).delete({});
    await dataSource.getRepository(Surgery).delete({});
    await dataSource.getRepository(ChronicDisease).delete({});
    await dataSource.getRepository(Allergy).delete({});
    await dataSource.getRepository(Patient).delete({});
    await dataSource.getRepository(RefreshToken).delete({});
    await dataSource.getRepository(Doctor).delete({});
    await dataSource.getRepository(User).delete({});
  });

  describe('POST /api/patients', () => {
    it('should create a patient profile for authenticated patient user', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
        bloodType: 'A+',
        maritalStatus: 'single',
      };

      const response = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const body = response.body as PatientResponse;
      expect(body).toHaveProperty('userId');
      expect(body.height).toBe(175);
      expect(body.weight).toBe(70);
      expect(body.gender).toBe('male');
      expect(body.bloodType).toBe('A+');
      expect(body.maritalStatus).toBe('single');
    });

    it('should return 401 when creating patient without authentication', async () => {
      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .send(patientDto)
        .expect(401);
    });

    it('should return 400 for invalid patient data', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const invalidPatientDto = {
        birthDate: 'not-a-date',
        height: 'tall',
        weight: 'heavy',
        gender: 'invalid',
      };

      await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(invalidPatientDto)
        .expect(400);
    });

    it('should create patient with optional fields omitted', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: Partial<PatientDto> = {
        birthDate: new Date('1995-06-20'),
        height: 165,
        weight: 60,
        gender: 'female',
      };

      const response = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const body = response.body as PatientResponse;
      expect(body.gender).toBe('female');
      expect(body.bloodType).toBeNull();
      expect(body.maritalStatus).toBeNull();
    });
  });

  describe('GET /api/patients/:id', () => {
    it('should return patient with all relations for authorized user', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
        bloodType: 'A+',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const response = await request(app.getHttpServer() as App)
        .get(`${PATIENTS_URL}/${patientId}`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.userId).toBe(patientId);
      expect(body).toHaveProperty('allergies');
      expect(body).toHaveProperty('chronicDiseases');
      expect(body).toHaveProperty('surgeries');
      expect(body).toHaveProperty('familyHistories');
      expect(body).toHaveProperty('emergencyContacts');
    });

    it('should return 401 when getting patient without authentication', async () => {
      await request(app.getHttpServer() as App)
        .get(`${PATIENTS_URL}/1`)
        .expect(401);
    });
  });

  describe('PATCH /api/patients/:id', () => {
    it('should update patient profile', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const updateDto = {
        height: 180,
        weight: 75,
        bloodType: 'O+',
      };

      const response = await request(app.getHttpServer() as App)
        .patch(`${PATIENTS_URL}/${patientId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.height).toBe(180);
      expect(body.weight).toBe(75);
      expect(body.bloodType).toBe('O+');
    });

    it('should update only provided fields', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
        bloodType: 'A+',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const updateDto = {
        weight: 72,
      };

      const response = await request(app.getHttpServer() as App)
        .patch(`${PATIENTS_URL}/${patientId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.weight).toBe(72);
      expect(body.height).toBe(175);
      expect(body.bloodType).toBe('A+');
    });
  });

  describe('PUT /api/patients/:id/allergies', () => {
    it('should update patient allergies', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const allergies = [
        { name: 'Peanuts', description: 'Severe allergy' },
        { name: 'Shellfish', description: 'Mild reaction' },
      ];

      const response = await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/allergies`)
        .set('Cookie', authCookie)
        .send(allergies)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.allergies).toHaveLength(2);
      expect(body.allergies[0].name).toBe('Peanuts');
      expect(body.allergies[1].name).toBe('Shellfish');
    });

    it('should return 400 when allergies is not an array', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/allergies`)
        .set('Cookie', authCookie)
        .send({ allergies: [{ name: 'Peanuts' }] })
        .expect(400);
    });
  });

  describe('PUT /api/patients/:id/chronic-diseases', () => {
    it('should update patient chronic diseases', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const chronicDiseases = [
        { name: 'Diabetes', description: 'Type 2' },
        { name: 'Hypertension', description: 'Controlled with medication' },
      ];

      const response = await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/chronic-diseases`)
        .set('Cookie', authCookie)
        .send(chronicDiseases)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.chronicDiseases).toHaveLength(2);
      expect(body.chronicDiseases[0].name).toBe('Diabetes');
    });
  });

  describe('PUT /api/patients/:id/surgeries', () => {
    it('should update patient surgeries', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const surgeries = [
        {
          name: 'Appendectomy',
          description: 'Performed in 2015',
          date: new Date('2015-03-20'),
        },
      ];

      const response = await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/surgeries`)
        .set('Cookie', authCookie)
        .send(surgeries)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.surgeries).toHaveLength(1);
      expect(body.surgeries[0].name).toBe('Appendectomy');
    });
  });

  describe('PUT /api/patients/:id/family-histories', () => {
    it('should update patient family histories', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const familyHistories = [
        {
          relation: 'father',
          condition: 'Heart Disease',
          notes: 'Father had heart attack at 55',
        },
      ];

      const response = await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/family-histories`)
        .set('Cookie', authCookie)
        .send(familyHistories)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.familyHistories).toHaveLength(1);
      expect(body.familyHistories[0].condition).toBe('Heart Disease');
    });
  });

  describe('PUT /api/patients/:id/emergency-contacts', () => {
    it('should update patient emergency contacts', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const emergencyContacts = [
        {
          name: 'John Doe',
          relation: 'father',
          phoneNumber: '01012345678',
          email: 'john@example.com',
          address: '123 Main St',
          notes: 'Primary contact',
        },
      ];

      const response = await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/emergency-contacts`)
        .set('Cookie', authCookie)
        .send(emergencyContacts)
        .expect(200);

      const body = response.body as PatientResponse;
      expect(body.emergencyContacts).toHaveLength(1);
      expect(body.emergencyContacts[0].name).toBe('John Doe');
      expect(body.emergencyContacts[0].relation).toBe('father');
    });

    it('should validate emergency contact data', async () => {
      const { authCookie } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      const createResponse = await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', authCookie)
        .send(patientDto)
        .expect(201);

      const createBody = createResponse.body as PatientResponse;
      const patientId = createBody.userId;

      const invalidEmergencyContacts = [
        {
          name: 'John Doe',
          relation: 'invalid-relation',
          phoneNumber: 'invalid-phone',
          email: 'invalid-email',
        },
      ];

      await request(app.getHttpServer() as App)
        .put(`${PATIENTS_URL}/${patientId}/emergency-contacts`)
        .set('Cookie', authCookie)
        .send(invalidEmergencyContacts)
        .expect(400);
    });
  });

  describe('GET /api/patients (secretary access)', () => {
    it('should allow secretary to get all patients', async () => {
      // Create a patient first
      const { authCookie: patientAuth } = await registerAndLogin('patient');

      const patientDto: PatientDto = {
        birthDate: new Date('1990-01-15'),
        height: 175,
        weight: 70,
        gender: 'male',
      };

      await request(app.getHttpServer() as App)
        .post(PATIENTS_URL)
        .set('Cookie', patientAuth)
        .send(patientDto)
        .expect(201);

      // Login as secretary
      const { authCookie: secretaryAuth } = await registerAndLogin('secretary');

      const response = await request(app.getHttpServer() as App)
        .get(PATIENTS_URL)
        .set('Cookie', secretaryAuth)
        .expect(200);

      const body = response.body as PatientResponse[];
      expect(Array.isArray(body)).toBe(true);
      expect(body.length).toBeGreaterThanOrEqual(1);
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
