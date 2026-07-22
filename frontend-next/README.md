# MedAI — Web Client

> **Next.js 16 & React 19 Web Application**
> AI-powered clinical operations platform for patients, doctors, secretaries, and managers.

[![Next.js](https://img.shields.io/badge/Next.js-16.2.2-black.svg?style=for-the-badge&logo=next.js)](https://nextjs.org/)
[![React](https://img.shields.io/badge/React-19.2.6-61DAFB.svg?style=for-the-badge&logo=react)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4-06B6D4.svg?style=for-the-badge&logo=tailwindcss)](https://tailwindcss.com/)
[![TanStack Query](https://img.shields.io/badge/TanStack_Query-5-FF4154.svg?style=for-the-badge&logo=reactquery)](https://tanstack.com/query/)
[![shadcn/ui](https://img.shields.io/badge/shadcn/ui-Radix_Nova-000000.svg?style=for-the-badge)](https://ui.shadcn.com/)

---

## Technical Overview

The **MedAI Web Client** is a modern, full-featured medical management platform built with **Next.js 16** (App Router) and **React 19**. It delivers responsive, role-based dashboards for all four organizational roles with a consistent design system and robust data management.

### Role-Based Workflows

- **Patients**: Browse doctors by medical speciality, book and manage appointments with real-time slot selection, view comprehensive medical records (personal info, allergies, chronic diseases, family history, surgeries, emergency contacts), upload scans and lab reports, and track AI-driven analysis results.

- **Doctors**: Manage daily appointment queues with patient details at a glance, log diagnoses and prescriptions for each visit, dictate and review Egyptian Arabic voice reports with AI transcription, and configure personal schedules and working hours.

- **Secretaries**: Create and apply weekly schedule templates for doctors, manage appointment queues (confirm/reschedule/cancel), register new patients, upload medical scans and reports (up to 10MB), and view doctor availability across specialities.

- **Managers**: Approve pending doctor and secretary registrations, manage all user accounts (patients, doctors, secretaries, managers), view and edit doctor specialities, and oversee the entire platform registry.

---

## Tech Stack

| Category             | Library / Tool                                          | Purpose                                                          |
| -------------------- | ------------------------------------------------------- | ---------------------------------------------------------------- |
| **Framework**        | Next.js 16 (App Router)                                 | React meta-framework with server components, routing, middleware |
| **UI Library**       | React 19                                                | Component-based UI rendering                                     |
| **Styling**          | Tailwind CSS v4                                         | Utility-first CSS with `@theme` design tokens                    |
| **UI Primitives**    | shadcn/ui (Radix Nova)                                  | Accessible, unstyled component primitives                        |
| **State Management** | @tanstack/react-query v5                                | Server-state fetching, caching, and mutations                    |
| **Forms**            | react-hook-form + Zod                                   | Performant form management with schema validation                |
| **Client State**     | React Context                                           | Auth context, sidebar toggle, doctor info flow                   |
| **Animations**       | framer-motion                                           | Declarative animations and transitions                           |
| **Icons**            | lucide-react                                            | Consistent, tree-shakeable icon library                          |
| **Dark Mode**        | next-themes                                             | Theme provider with system/local preference support              |
| **Date Handling**    | date-fns + react-day-picker                             | Date utilities and accessible date picker                        |
| **Notifications**    | react-hot-toast                                         | Lightweight toast notifications                                  |
| **HTTP Client**      | Native fetch (client) + Next.js server actions (server) | API communication with auto JWT refresh interceptor              |

---

## Architecture

### Layer Structure

```
 ┌─────────────────────────────────────────────────────────┐
 │                    PRESENTATION LAYER                    │
 │    Server Components (RSC)  +  Client Components         │
 │    App Router Pages  →  Reusable UI Components           │
 └──────────────────────────┬──────────────────────────────┘
                            │ (useQuery / useMutation)
                            ▼
 ┌─────────────────────────────────────────────────────────┐
 │                    DATA ACCESS LAYER                     │
 │    apiFetchClient (client)   apiFetchServer (server)     │
 │    Service modules  →  API calls with JWT auth           │
 └─────────────────────────────────────────────────────────┘
                            │
                            ▼
 ┌─────────────────────────────────────────────────────────┐
 │                  BACKEND API (NestJS)                    │
 └─────────────────────────────────────────────────────────┘
```

### Key Architectural Decisions

- **Server Components by Default**: Pages are React Server Components (RSC) for optimal performance. Interactive elements use the `"use client"` directive.
- **TanStack Query**: All API data fetching and mutations go through TanStack Query (v5) for caching, deduplication, and optimistic updates.
- **JWT Authentication**: Access tokens stored in HTTP-only cookies. The middleware (`proxy.js`) handles token refresh and route protection globally.
- **Role-Based Route Protection**: The auth wrapper reads JWT claims and restricts access based on user role — unauthorized routes redirect to the appropriate dashboard.
- **Unidirectional Data Flow**: Forms use react-hook-form with Zod schemas. Mutations flow through TanStack Query, and UI state updates reactively.

---

## Key Features

### 1. JWT Authentication & Silent Token Refresh

The client implements automatic token rotation via a custom fetch interceptor:

- **Automatic Header Injection**: All API requests include the JWT Bearer token from cookies.
- **Silent Refresh**: On 401 responses, the interceptor pauses the request, calls `/auth/refresh-token`, saves the new credentials, and retries seamlessly.
- **Middleware Protection**: The Next.js middleware (`proxy.js`) runs before every request, refreshing tokens and redirecting unauthenticated or unauthorized users.

### 2. Role-Based Dashboards

Four distinct dashboard experiences served from the `(dashboard)` route group:

| Role      | Route Prefix | Key Pages                                                           |
| --------- | ------------ | ------------------------------------------------------------------- |
| Patient   | `/patient`   | Dashboard, Appointments, Book Appointment, Medical Records, Profile |
| Doctor    | `/doctor`    | Dashboard, Appointments, Availability, Working Hours                |
| Secretary | `/secretary` | Dashboard, Appointments, Doctors, Patients, Specialities            |
| Manager   | `/manager`   | Dashboard, Patients, Users (Doctors/Managers/Secretaries)           |

### 3. Dynamic Appointment Booking

Patients can browse doctors by speciality and book appointments through a multi-step flow:

1. Select a speciality → view available doctors
2. Select a doctor → view available date slots (7-day carousel)
3. Select a time slot → review and confirm booking

### 4. Doctor Schedule Management

Doctors and secretaries can manage weekly schedules using templates:

- **Templates**: Reusable weekly patterns (e.g., "Monday 9 AM–5 PM") saved for quick application.
- **Slots**: Granular time slots with start/end times, created from templates or manually.
- **Week View**: Visual grid of the current week with slot editing and deletion.

### 5. Dark Mode

Fully implemented dark mode via `next-themes` and Tailwind v4's `@custom-variant dark`. The design system remaps CSS custom properties when `.dark` is active — no `dark:` prefixes needed in components.

### 6. Design System & shadcn/ui

All UI primitives are built on shadcn/ui (Radix Nova style) with a comprehensive design system documented in `DESIGN_SYSTEM.md`:

- Custom color palette (light + dark) via CSS custom properties
- Unified typography, spacing, shadows, and border radius tokens
- Reusable `cn()` utility combining `clsx` + `tailwind-merge`

---

## App Screens & Workflows

_Screenshots to be added._

### 1. Authentication & Onboarding

|     Login      |     Signup     | Forgot Password |
| :------------: | :------------: | :-------------: |
| `[screenshot]` | `[screenshot]` | `[screenshot]`  |

### 2. Patient Workflow

| Browse Doctors & Book Appointment | Medical Records | Scans & Reports |
| :-------------------------------: | :-------------: | :-------------: |
|          `[screenshot]`           | `[screenshot]`  | `[screenshot]`  |

| Appointments List |    Profile     |
| :---------------: | :------------: |
|  `[screenshot]`   | `[screenshot]` |

### 3. Doctor Workflow

| Dashboard & Queue | Appointment Details | Voice Reports  |
| :---------------: | :-----------------: | :------------: |
|  `[screenshot]`   |   `[screenshot]`    | `[screenshot]` |

| Schedule Management | Working Hours  |
| :-----------------: | :------------: |
|   `[screenshot]`    | `[screenshot]` |

### 4. Secretary Workflow

|   Dashboard    | Schedule Templates | Slot Management |
| :------------: | :----------------: | :-------------: |
| `[screenshot]` |   `[screenshot]`   | `[screenshot]`  |

| Appointments Queue | Patient Registration |
| :----------------: | :------------------: |
|   `[screenshot]`   |    `[screenshot]`    |

### 5. Manager Workflow

|   Dashboard    | User Approvals | User Management |
| :------------: | :------------: | :-------------: |
| `[screenshot]` | `[screenshot]` | `[screenshot]`  |

---

## Project Structure

```
frontend-next/
├── src/
│   ├── app/                          # Next.js App Router pages
│   │   ├── (dashboard)/              # Authenticated dashboard route group
│   │   │   ├── auth-wrapper.js       # JWT-based auth context provider
│   │   │   ├── layout.js             # Dashboard shell (sidebar + header)
│   │   │   ├── doctor/               # Doctor pages
│   │   │   ├── manager/              # Manager pages
│   │   │   ├── patient/              # Patient pages
│   │   │   ├── secretary/            # Secretary pages
│   │   │   └── profile/              # Profile page
│   │   ├── auth/                     # Public auth pages (login, signup, forgot-password)
│   │   ├── layout.js                 # Root layout (providers, fonts)
│   │   └── page.js                   # Landing page
│   │
│   ├── components/                   # React components
│   │   ├── ui/                       # Reusable design system components (60+)
│   │   ├── shadcn/                   # shadcn/ui primitives (Radix-based)
│   │   ├── sidebar/                  # Sidebar navigation components
│   │   ├── auth/                     # Authentication page components
│   │   ├── landing/                  # Landing page sections
│   │   ├── doctor/                   # Doctor-specific components
│   │   ├── patient/                  # Patient-specific components
│   │   ├── secretary/                # Secretary-specific components
│   │   ├── manager/                  # Manager-specific components
│   │   ├── appointments/             # Appointment booking components
│   │   ├── schedule/                 # Schedule management components
│   │   ├── scans/                    # Scan & report upload components
│   │   ├── diagnosis/                # Diagnosis form components
│   │   └── voice-reports/            # Voice report components
│   │
│   ├── services/                     # API service layers
│   │   ├── client/                   # Client-side API calls
│   │   └── server/                   # Server-side API calls (RSC)
│   │
│   ├── hooks/                        # Custom React hooks
│   ├── contexts/                     # React Context providers (Auth, Sidebar, DoctorInfo)
│   ├── lib/                          # Utilities & helpers
│   │   ├── api/                      # API fetch clients (client + server)
│   │   ├── providers/                # React provider wrappers
│   │   ├── utils/                    # Date, string, token helpers
│   │   └── zod/                      # Zod validation schemas
│   ├── constants/                    # App-wide constants
│   └── assets/                       # Static images
│
├── public/                           # Public assets (logos, favicon)
├── docker/                           # Dockerfile for containerized deployment
├── DESIGN_SYSTEM.md                  # Design tokens documentation
├── jsconfig.json                     # Path alias configuration (@/*)
├── next.config.mjs                   # Next.js configuration
├── postcss.config.mjs                # PostCSS with Tailwind v4
├── eslint.config.mjs                 # ESLint v9 flat config
└── .env.local                        # Environment variables (gitignored)
```

---

## Getting Started

### Prerequisites

- **Node.js** 20+ (LTS recommended)
- **npm** 10+

### Environment Variables

Create a `.env.local` file in the project root:

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8000
MOBILE_APP_URL=https://appdistribution.firebase.dev/i/e838b61f94d0a94c
```

| Variable                   | Description                                    |
| -------------------------- | ---------------------------------------------- |
| `NEXT_PUBLIC_API_BASE_URL` | Backend API base URL (NestJS server)           |
| `MOBILE_APP_URL`           | Deep link URL for the mobile app download page |

### Installation & Development

```bash
# Install dependencies
npm install

# Start development server (with Turbopack)
npm run dev

# Open http://localhost:3000
```

---

## Available Scripts

| Script  | Command      | Description                             |
| ------- | ------------ | --------------------------------------- |
| `dev`   | `next dev`   | Start development server with Turbopack |
| `build` | `next build` | Production build (standalone output)    |
| `start` | `next start` | Start production server                 |
| `lint`  | `eslint`     | Run ESLint across the codebase          |

---

## Docker Deployment

```bash
# Build the Docker image
docker build -f docker/Dockerfile -t medai-frontend .

# Run the container
docker run -p 3000:3000 medai-frontend
```

The Dockerfile uses a multi-stage build with `node:24-alpine`, runs `npm ci`, produces a standalone output, and serves the app with the Next.js server.

---

## Design System

See [`DESIGN_SYSTEM.md`](./DESIGN_SYSTEM.md) for comprehensive documentation of:

- Color palette (light & dark modes)
- Typography tokens
- Spacing & sizing scale
- Shadow & border radius tokens
- Tailwind v4 usage conventions
- Stitch AI prompt snippet for screen generation
