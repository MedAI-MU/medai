# MedAI

> **Graduation Project — Mansoura University**
> An AI-powered healthcare ecosystem seamlessly connecting patients, doctors, secretaries, and hospital managers.

[![Platform Support](https://img.shields.io/badge/Platform-Web%20%7C%20Mobile%20%7C%20AI-blue?style=for-the-badge&logo=android)](https://github.com/)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-teal?style=for-the-badge&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)
[![Backend](https://img.shields.io/badge/NestJS-11-red?style=for-the-badge&logo=nestjs)](https://nestjs.com/)
[![Frontend](https://img.shields.io/badge/Next.js-16-black?style=for-the-badge&logo=next.js)](https://nextjs.org/)

---

## Overview

**medAI** is a comprehensive, production-grade medical platform designed to streamline clinical and administrative hospital workflows. The system integrates advanced AI capabilities, including:
1. **Egyptian Arabic Medical Voice-to-Report Transcription**: Tailored pipeline utilizing fine-tuned Whisper and Llama-3 models.
2. **AI Lab Report Analysis**: Document text parsing, OCR, and medical summarization.
3. **Cross-Platform Mobile App**: A dedicated Kotlin Multiplatform (Compose Multiplatform) client targeting Android & iOS for patients and doctors.
4. **Role-Based Web Dashboard**: Dashboards for patients, doctors, secretaries, and managers built with Next.js.hboards.

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Frontend** | Next.js 16 (React 19), Tailwind CSS v4, Radix UI, TanStack Query v5, react-hook-form + Zod v4, framer-motion, date-fns |
| **Backend** | NestJS 11 (Node.js), TypeScript, TypeORM, PostgreSQL 16 |
| **Auth** | Passport.js (Local + JWT), Argon2 hashing, httpOnly cookies, email verification |
| **Queue** | BullMQ v5 (Redis-backed async job processing) |
| **File Storage** | Azure Blob Storage (avatars, scan images, reports, audio) |
| **Email** | Brevo SMTP via Nodemailer |
| **AI** | Fine-tuned Whisper Large-V3 + Llama-3 8B (4-bit) for Egyptian Arabic medical voice-to-report |
| **Mobile** | Kotlin Multiplatform (Compose Multiplatform) — Android + iOS |
| **Infra** | Docker Compose (local/staging/prod), Terraform, GitHub Actions CI/CD |

---

## Architecture

```
┌─────────────┐     ┌──────────────────────────────────────┐     ┌───────────┐
│  Next.js 16 │────▶│          NestJS 11 API               │────▶│ PostgreSQL│
│  (Frontend) │     │  ┌─────┐ ┌──────┐ ┌──────────────┐  │     └───────────┘
└─────────────┘     │  │Auth │ │Users│ │  Patients    │  │
                    │  ├─────┤ ├──────┤ ├──────────────┤  │     ┌───────────┐
┌─────────────┐     │  │Docs │ │Sched │ │Appointments  │  │────▶│   Redis   │
│ Kotlin App  │────▶│  ├─────┤ ├──────┤ ├──────────────┤  │     └───────────┘
│  (Mobile)   │     │  │Scans│ │Report│ │Voice Reports │  │     ┌───────────┐
└─────────────┘     │  ├─────┤ └──────┘ └──────────────┘  │────▶│Azure Blob │
                    │  │Diagnosis │  BullMQ Workers        │     └───────────┘
                    │  │ (AI Pipe)│ ◀──────────────────────│────▶│  AI Server│
                    └──────────────────────────────────────┘     └───────────┘
```

---

## Features

### 🔐 Authentication & User Management
- Email/password registration with role selection (patient, doctor, secretary, manager)
- JWT authentication (access + refresh tokens as httpOnly cookies, auto-rotation)
- Email verification (token-based, Brevo SMTP)
- Forgot/reset password flow
- Email change with confirmation
- Profile management: name, phone, avatar upload (Azure Blob), bio, gender, birth date

### 👥 Role-Based Access Control
Four roles with separate dashboards and permissions:

| Role | Capabilities |
|------|-------------|
| **Patient** | Book appointments, view medical records, upload scans, view diagnoses, track AI reports |
| **Doctor** | Manage schedules, accept appointments, view patient scans, write diagnoses, record voice reports |
| **Secretary** | Manage appointments (approve/reject), manage patients & medical records, upload scans, manage doctor schedules & specialities |
| **Manager** | Approve/reject doctor & secretary registrations, manage all users, view patients |

### 📅 Appointment Booking System
- Hospital-grade workflow: `pending → confirmed/cancelled → completed`
- Patients browse doctors by name or speciality
- View real-time available time slots for selected date
- Book an appointment from an available slot
- Secretaries/managers approve or reject pending appointments
- Patients can rate and review completed appointments
- Blacklist support for problematic users

### 🩺 Doctor Schedule Management
- **Schedule Templates**: Define recurring weekly patterns (e.g., Mon–Wed 9:00–17:00)
- **Apply Template**: Generate concrete date slots from a template for a date range
- **Schedule Slots**: CRUD for individual date/time slots (e.g., 2026-07-14 09:00–09:30)
- Slots visible to patients for booking

### 📋 Patient Medical Records
Complete medical history management:
- Allergies (name, description)
- Chronic diseases (name, description, diagnosis date)
- Family history (relation, condition, notes)
- Surgeries (name, date, description)
- Emergency contacts (name, relation, phone, email, address)
- Patient vitals: height, weight, blood type, marital status

### 🖼️ Medical Scans & Reports
- Upload scan images (linked to patient & optional appointment)
- Upload medical reports (PDF/images)
- View scan images directly in browser (served from Azure Blob)
- All files stored securely in Azure Blob Storage
- Role-based access: patients see their own, doctors see assigned patients, secretaries see all

### 🤖 AI-Powered Report Analysis
- Upload a medical report (image/PDF)
- Async analysis via BullMQ queue → external AI server
- Poll for analysis status (processing, completed, failed)
- Analysis results stored as JSONB on the report

### 🎙️ Voice-to-Clinical Report (Egyptian Arabic)
- Doctors record spoken notes after an appointment
- Audio uploaded → BullMQ job triggers pipeline:
  1. FFmpeg converts to 16kHz mono WAV
  2. Uploads to Azure Blob
  3. Sends to AI server (fine-tuned Whisper Large-V3 + Llama-3 8B)
- Results: transcription + structured clinical JSON
- Supports Egyptian Arabic medical dialect

### 🩻 Doctor Diagnoses
- Doctors write diagnoses linked to patient & appointment
- Structured fields: symptoms + summary
- Viewable in patient medical records
- Editable (symptoms and full diagnosis)

### 📊 Role-Based Dashboards
- **Patient Dashboard**: Upcoming appointments, book appointment, medical records
- **Doctor Dashboard**: Appointment list with details, working hours management, availability viewer
- **Secretary Dashboard**: All appointments overview, doctor & patient management, specialities CRUD
- **Manager Dashboard**: User approval workflow, patient list, user role management

### 📱 Kotlin Multiplatform Mobile App (Android & iOS)
- **Shared Compose UI**: Declarative layouts shared between Android and iOS.
- **Unified Presentation**: Unidirectional MVI architecture using Voyager ScreenModels.
- **Native Dictation**: Low-latency recording (MPEG-4 AAC) via platform hardware APIs.
- **Silent Token Rotation**: Ktor client auth interceptor providing automated session refresh.
- **Lab Analysis Telemetry**: Cold Flow-based background status polling with real-time UI feedback.
- **Upload Guards**: Client-side multipart upload validation restricted to 10MB limits.
- **Custom Design System**: Consistent colors, custom forms, and widgets across platform views.

---

## Project Structure

```
MedAI/
├── backend/                  # NestJS API server
│   ├── src/
│   │   ├── auth/             # Authentication module
│   │   ├── users/            # User management module
│   │   ├── patients/         # Patient medical records module
│   │   ├── doctors/          # Doctor profiles & specialities module
│   │   ├── schedules/        # Schedule templates & slots module
│   │   ├── appointments/     # Appointment booking module
│   │   ├── scans/            # Medical scan & report uploads module
│   │   ├── diagnosis/        # Doctor diagnosis module
│   │   ├── voice-reports/    # Voice report + async AI pipeline module
│   │   ├── report-analysis/  # Lab report AI analysis module
│   │   ├── mail/             # Brevo SMTP email module
│   │   ├── database/         # TypeORM config, migrations (25+), seeds
│   │   └── shared/           # Shared entities, guards, decorators, services
│   ├── tests/                # E2E tests
│   └── docker/               # Dockerfiles (dev, test, prod)
│
├── frontend-next/            # Next.js 16 frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── page.js                          # Landing page
│   │   │   ├── auth/                            # Login, signup, forgot-password
│   │   │   ├── (dashboard)/
│   │   │   │   ├── patient/     # Patient dashboard pages
│   │   │   │   ├── doctor/      # Doctor dashboard pages
│   │   │   │   ├── secretary/   # Secretary dashboard pages
│   │   │   │   ├── manager/     # Manager dashboard pages
│   │   │   │   └── profile/     # User profile page
│   │   │   ├── reset-password/
│   │   │   └── download/
│   │   ├── components/
│   │   │   ├── ui/              # 60+ shadcn-inspired UI components
│   │   │   ├── landing/         # Landing page sections
│   │   │   ├── auth/            # Authentication components
│   │   │   ├── doctor/          # Doctor-related components (18)
│   │   │   ├── patient/         # Patient-related components (25)
│   │   │   ├── secretary/       # Secretary components
│   │   │   ├── manager/         # Manager components
│   │   │   ├── schedule/        # Schedule management components (22)
│   │   │   ├── appointments/    # Appointment components
│   │   │   ├── scans/           # Scan/report components
│   │   │   ├── diagnosis/       # Diagnosis components
│   │   │   └── voice-reports/   # Voice report components
│   │   ├── contexts/            # Auth, Sidebar, DoctorInfo contexts
│   │   ├── hooks/               # Custom React hooks
│   │   ├── services/            # API client services (server + client)
│   │   └── lib/                 # Utilities, Zod schemas, API fetch helpers
│   ├── public/
│   └── docker/
│
├── ai/                         # AI pipeline (Python)
│   └── egyptian-voice-medical-analyzer/
│       ├── Llama3_Egyptian_Medic_Final/  # Fine-tuned model
│       ├── models/                       # Model weights
│       ├── main_test.py                  # Inference runner
│       └── requirements.txt
│
├── mobile/                     # Mobile Application
│   └── MedAI/                  # Kotlin Multiplatform Project
│       ├── composeApp/         # Shared Compose UI + platform code
│       │   └── src/commonMain/kotlin/org/example/project/
│       │       ├── core/       # Base MVI framework classes
│       │       ├── data/       # Remote Ktor client, DTOs & repositories
│       │       ├── domain/     # Domain models, use cases & native audio contracts
│       │       ├── presentation/# View layer: screens (Voyager) & viewmodels (MVI)
│       │       └── design_system/# Palette, typography, spacing and forms
│       └── iosApp/             # iOS Xcode entry point and app delegate
│
├── infra/                      # Terraform (prod/staging environments)
├── docker-compose/             # Docker Compose (local/staging/prod)
└── Makefile                    # Dev commands
```

---

## Getting Started

### Prerequisites

- Docker & Docker Compose (with BuildKit enabled)
- Node.js 22+ (for running migrations/seeders locally)
- pnpm
- Java Development Kit (JDK) 17+ (for compiling the Mobile app)
- Android Studio / Xcode (for Android / iOS simulator builds)

### Local Development (Backend & Web)

```bash
# 1. Clone and enter the repo
git clone <repo-url> && cd MedAI

# 2. Configure environment
cp backend/.env.example backend/.env
# Edit backend/.env with your values (JWT secrets, Azure keys, Brevo SMTP, etc.)

# 3. Start all services
make run
```

This starts: NestJS API (port 8000), Next.js frontend (port 3000), PostgreSQL (5432), Redis (6379), runs migrations and seeds.

### Individual Commands

```bash
make run              # Full local environment
make stop             # Stop all containers
make migrate          # Run pending TypeORM migrations
make generate-migration MIGRATION_NAME=name   # Generate migration from entities
make seed             # Run database seeders
make lint             # Lint both frontend and backend
make test-backend     # Run backend tests with coverage
make format           # Format all code with Prettier
make clean-db         # Wipe PostgreSQL data volume
```

### Environment Variables

Key variables (see `backend/.env.example` for full list):

| Variable | Description |
|----------|-------------|
| `JWT_SECRET` / `REFRESH_TOKEN_SECRET` | Token signing secrets |
| `BREVO_SMTP_*` | Brevo email credentials (verification, password reset) |
| `AZURE_STORAGE_CONNECTION_STRING` | Azure Blob Storage (avatars, scans, reports) |
| `REDIS_HOST` / `REDIS_PORT` | Redis for BullMQ queues |
| `AI_SERVER_URL` | AI voice-to-report pipeline endpoint |
| `LAB_AI_SERVER_URL` | Lab report analysis AI endpoint |

---

### Compiling & Running the Mobile Client

The mobile app codebase is located at `mobile/MedAI/`.

#### 1. Set Backend Base Endpoint
Create/edit `local.properties` in `mobile/MedAI/` and add the backend base URL:
```properties
# Android Emulator loopback pointing to host machine localhost
medai.base_url=http://10.0.2.2:8000/api/

# For iOS Simulator (localhost works directly)
# medai.base_url=http://localhost:8000/api/
```

#### 2. Run Android Application
Ensure you have an Android virtual device running or a physical device connected:
* **macOS / Linux**:
  ```shell
  cd mobile/MedAI
  ./gradlew :composeApp:installDebug
  ```
* **Windows**:
  ```shell
  cd mobile\MedAI
  .\gradlew.bat :composeApp:installDebug
  ```

#### 3. Run iOS Application (macOS required)
Open the Xcode workspace inside `mobile/MedAI/iosApp`:
```bash
cd mobile/MedAI/iosApp
open iosApp.xcodeproj
```
Select your target simulator (e.g. iPhone 15) and click **Run** (Cmd + R) inside Xcode.

---

## API

The NestJS backend exposes a REST API at `/api/*`. Full Swagger documentation is available at `/api/docs` when the server is running.

### Key Endpoints

**Auth**: `POST /api/auth/login`, `POST /api/auth/refresh-token`, `POST /api/auth/forgot-password`, `POST /api/auth/reset-password`, `POST /api/auth/change-email`

**Users**: `POST /api/users` (register), `GET /api/users/me`, `PATCH /api/users/:id`, `POST /api/users/:id/avatar`

**Patients**: `GET /api/patients`, `GET /api/patients/:id`, CRUD for allergies, chronic diseases, family history, surgeries, emergency contacts

**Doctors**: `GET /api/doctors`, `POST /api/doctors/search/speciality`, `POST /api/doctors/search/name`, `GET /api/doctors/top-rated`, CRUD specialities

**Schedules**: CRUD schedule-templates, schedule-slots, `POST apply-template` (per doctor)

**Appointments**: `POST /api/appointments`, `GET /api/appointments/me`, `PATCH /api/appointments/:id/status`, `PATCH /api/appointments/:id/review`

**Voice Reports**: `POST /api/appointments/:id/voice-reports`, `GET /api/appointments/:id/voice-reports/:reportId`

**Scans & Reports**: `POST /api/scans/:id`, `POST /api/scans/:id/:scanId/reports`, `GET /api/scans/images/:id/:imageId/file`

**Diagnoses**: `POST /api/diagnosis/:id`, `PATCH /api/diagnosis/:id/:diagnosisId`

---

## AI Pipeline

### Voice-to-Clinical Report
1. Doctor records audio on the frontend
2. Audio uploaded to backend → stored in Azure Blob → BullMQ job queued
3. FFmpeg converts to 16kHz mono WAV
4. Sent to AI server running fine-tuned **Whisper Large-V3** (Egyptian Arabic STT)
5. Transcription fed into fine-tuned **Llama-3 8B** (4-bit quantized) for medical NLU
6. Structured clinical JSON returned and stored

### Medical Report Analysis
1. Secretary uploads report (image/PDF) for a patient
2. BullMQ job triggers analysis on `LAB_AI_SERVER_URL`
3. Frontend polls for status until complete

---

## Testing

```bash
# Backend unit + integration tests
make test-backend

# E2E tests
pnpm --prefix backend run test:e2e

# Linting
make lint

# Run Mobile KMP shared tests
cd mobile/MedAI
./gradlew :composeApp:testDebugUnitTest
```

---

## Deployment

Three Docker Compose environments:

| File | Purpose |
|------|---------|
| `docker-compose/local.yaml` | Local development (hot-reload, debug) |
| `docker-compose/staging.yaml` | Staging environment |
| `docker-compose/prod.yaml` | Production (standalone Next.js build) |

Terraform configurations under `infra/` for cloud infrastructure provisioning.
