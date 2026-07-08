# MedAI — Stitch Context

> This file is used as context when prompting Stitch to generate new page screens.
> It preserves the project layout, design system tokens, component patterns, and navigation structure.

---

## 1. Project Overview

**MedAI** is a medical platform that helps patients find suitable doctors with AI assistance.
It is a **Next.js 15 (App Router)** application using **Tailwind CSS v4** with CSS-variable–based design tokens.

- **Stitch Project ID:** `7926849047500910416`
- **Stitch Project Title:** "MedAI Sign Up - Patient View"
- **Device Type:** Desktop (1280×1024 primary)
- **Design System Asset (recommended):** `assets/c40ecc61ecf940ecb8a72af3d543c787` — "MedAI Design System"

### Roles

| Role      | Portal Route Prefix | Dashboard Entry |
|-----------|---------------------|-----------------|
| Patient   | `/patient`          | `/patient`      |
| Doctor    | `/doctor`           | `/doctor`       |

---

## 2. Design System — Color Tokens

The design system uses CSS custom properties defined via Tailwind's `@theme` directive.
Dark mode is toggled via the `.dark` class on `<html>` — **no `dark:` prefixes needed in components**.

### Surfaces (theme-aware)

| Token            | Light     | Dark      | Usage                            |
|------------------|-----------|-----------|----------------------------------|
| `surface`        | `#FFFFFF` | `#0F172A` | Sidebar, header, cards           |
| `surface-bg`     | `#F5F7FA` | `#111821` | Main page / content background   |
| `surface-overlay`| `#F1F5F9` | `#334155` | Input backgrounds, hover states  |

### Borders (theme-aware)

| Token    | Light     | Dark      |
|----------|-----------|-----------|
| `border` | `#E2E8F0` | `#334155` |

### Text (theme-aware)

| Token        | Light     | Dark      | Usage                      |
|--------------|-----------|-----------|----------------------------|
| `text-base`  | `#2D3748` | `#F1F5F9` | Primary body text          |
| `text-muted` | `#718096` | `#94A3B8` | Secondary / supporting     |
| `text-subtle`| `#A0AEC0` | `#64748B` | Placeholders, hints, disabled |

### Brand & Status (constant — same in both modes)

| Token           | Value        | Usage                           |
|-----------------|--------------|----------------------------------|
| `primary`       | `#4A90E2`    | Buttons, links, active states    |
| `primary-hover` | `#3A7BD5`    | Hover state for primary          |
| `success`       | `#48BB78`    | Confirmations, active badges     |
| `warning`       | `#F6AD55`    | Alerts, pending states           |
| `danger`        | `#E53E3E`    | Errors, destructive actions      |
| `danger-muted`  | `#E53E3E1A`  | Danger hover bg (10% opacity)    |

### Shadows

| Token        | Value                              | Usage               |
|--------------|------------------------------------|----------------------|
| `shadow-sm`  | `0 1px 3px rgba(0,0,0,0.08)`      | Subtle lift          |
| `shadow-md`  | `0 4px 6px rgba(0,0,0,0.1)`       | Cards                |
| `shadow-lg`  | `0 10px 24px rgba(0,0,0,0.12)`    | Modals, dropdowns    |
| `shadow-glow`| `0 0 20px rgba(74,144,226,0.2)`   | Primary focus glow   |

### Border Radius

| Token        | Value    | Usage                        |
|--------------|----------|------------------------------|
| `radius-sm`  | `6px`    | Small elements, badges       |
| `radius-md`  | `8px`    | Cards, modals, buttons       |
| `radius-lg`  | `12px`   | Large containers             |
| `radius-full`| `9999px` | Pills, avatars, chips        |

---

## 3. Typography

- **Font Family:** `Inter`, `system-ui`, `sans-serif`
- **Font Smoothing:** `-webkit-font-smoothing: antialiased`
- **Base Font Size:** `clamp(14px, 2vw, 16px)` (fluid, responsive)

### Type Scale (used in code)

| Level | Class / Size                           | Weight     |
|-------|----------------------------------------|------------|
| XL    | `text-2xl sm:text-3xl`                 | `extrabold`|
| LG    | `text-xl sm:text-2xl`                  | `bold`     |
| SM    | `text-base sm:text-lg`                 | `bold`     |
| XS    | `text-base`                            | `semibold` |
| Body  | `text-sm` / `text-base`               | `normal`   |
| Label | `text-[10px]` tracking-wider uppercase | `bold`     |

---

## 4. Layout & Spacing

| Token            | Value    | Usage                  |
|------------------|----------|------------------------|
| `sidebar-width`  | `280px`  | Fixed sidebar width    |
| `header-height`  | `76px`   | Dashboard header       |
| `gap-form`       | `1.5rem` | Form field spacing     |

### Dashboard Shell

```
┌───────────────────────────────────────────────────┐
│  Sidebar (280px)  │  Header (76px height)         │
│                   ├───────────────────────────────│
│  - Logo + Title   │  Main Content Area            │
│  - Nav Links      │  (bg-surface-bg, scrollable)  │
│  - Account        │                               │
│  - Logout         │  <Container py-8>             │
│                   │    {children}                  │
│                   │  </Container>                  │
└───────────────────────────────────────────────────┘
```

- Grid: `grid-cols-[var(--sidebar-width)_1fr]` on `lg:`, single column on mobile
- Sidebar slides in/out on mobile with overlay
- Main content: `bg-surface-bg overflow-auto`
- Content wrapped in `<Container className="py-8">`

---

## 5. Page & Route Structure

```
src/app/
├── layout.js              # RootLayout: Inter font, ThemeProvider, Toaster
├── page.js                # Landing page (/)
├── globals.css            # Design tokens (@theme + dark mode)
├── not-found.js           # 404 page
├── auth/
│   ├── login/             # /auth/login
│   └── signup/            # /auth/signup
└── (dashboard)/
    ├── layout.js          # DashboardLayout: Sidebar + Header + Main
    ├── error.js           # Error boundary
    ├── patient/
    │   ├── page.js                    # /patient (dashboard home)
    │   ├── appointments/page.js       # /patient/appointments
    │   ├── book-appointment/
    │   │   ├── page.js                # /patient/book-appointment (doctors list)
    │   │   └── [doctorId]/page.js     # /patient/book-appointment/:id (slots view)
    │   └── medical-records/page.js    # /patient/medical-records
    └── doctor/
        ├── page.js                    # /doctor (dashboard home)
        ├── availability/              # /doctor/availability (schedule slots)
        └── working-hours/             # /doctor/working-hours
```

---

## 6. Component Library

### Reusable UI Components (`src/components/ui/`)

| Component         | Description                                             |
|--------------------|---------------------------------------------------------|
| `Button`           | Primary, secondary, outline, ghost, danger, dangerGhost, green variants. Sizes: sm, md, lg. Supports `startIcon`, `endIcon`, `href` (renders as Link). |
| `Card`             | `bg-surface border-border rounded-xl border p-5 shadow-sm`. Groups content. |
| `Heading`          | Title + optional subtitle + optional action children. Sizes: xl, lg, sm, xs. Renders semantic tag. |
| `Badge`            | Pill badge. Colors: red, orange, blue, green, success, amber, warning, purple, slate, subtle. |
| `FormInput`        | Label + input + error message. `bg-surface-overlay` background, focus ring. Supports start/end icons. |
| `FormSelect`       | Styled select dropdown following the same input pattern. |
| `FormDatePicker`   | Date picker using Popover + Calendar (shadcn). |
| `SearchBar`        | Client component. Debounced URL-based search (300ms). |
| `Pagination`       | URL-based pagination with page numbers. |
| `EmptyState`       | Centered card with icon badge, heading, description, and optional CTA. |
| `ErrorState`       | Error display with retry button. |
| `Tabs`             | Tab navigation component. |
| `DashboardHeader`  | Search bar, dark mode toggle, notifications, user info. |
| `Container`        | Centered content wrapper with `px-6 lg:px-10`. |
| `DeleteDialog`     | Confirmation dialog for destructive actions (uses shadcn AlertDialog). |
| `FormDialog`       | Modal form wrapper (uses shadcn Dialog). |
| `FormSheet`        | Slide-in panel form (uses shadcn Sheet). |
| `SpinnerMini`      | Small loading spinner (inline). |
| `AnimateWrapper`   | Framer Motion page transition wrapper. |
| `Logo`             | MedAI logo component. |
| `DarkmodeToggler`  | Theme toggle button (Sun/Moon). |
| `StarRating`       | Star rating display/input. |
| `Timeline`         | Vertical timeline for medical history. |

### Shadcn UI Primitives (`src/components/shadcn/`)

Used as low-level building blocks: `AlertDialog`, `Button`, `Calendar`, `Dialog`, `DropdownMenu`, `Popover`, `Sheet`.

### Feature Components

| Group               | Components                                                                  |
|----------------------|-----------------------------------------------------------------------------|
| **Sidebar**          | `Sidebar`, `SidebarNavItem`, `SidebarNavGroup`, `SidebarToggler`           |
| **Auth**             | `AuthLayout`, `LoginForm`, `LoginHero`, `SignupForm`, `SignupHero`, `RoleSelector`, `FormSection` |
| **Landing**          | `LandingHeader`, `Hero`, `About`, `Services`, `Doctors`, `Contact`, `Footer` |
| **Doctor/Schedule**  | `ScheduleGrid`, `ScheduleSlotsForm`, `ScheduleSlotsDayItem`, `WeekNavigator`, `ScheduleTemplateList`, `TemplateForm`, etc. |
| **Patient/Medical**  | `MedicalTabs`, `PersonalInfoTab`, `AllergiesTab`, `ChronicDiseasesTab`, `FamilyHistoryTab`, `SurgeriesTab`, `EmergencyContactsTab` |
| **Patient/Appts**    | `PatientAppointments`, `PatientAppointmentCard`, `CreateAppointmentForm`    |
| **Book Appointment** | `DoctorsList`, `DoctorCard`, `DoctorProfileCard`, `DayCarouselWrapper`, `DayCard`, `SlotsPanel`, `SlotButton` |

---

## 7. Sidebar Navigation

### Patient Sidebar

| Link              | Route                       | Icon          |
|-------------------|-----------------------------|---------------|
| Dashboard         | `/patient`                  | LayoutDashboard |
| Book Appointment  | `/patient/book-appointment` | CalendarPlus  |
| My Appointments   | `/patient/appointments`     | CalendarCheck |
| Medical Records   | `/patient/medical-records`  | FileText      |
| Messages          | `#` (placeholder)           | MessageCircle |

### Doctor Sidebar

| Link              | Route                       | Icon          |
|-------------------|-----------------------------|---------------|
| Dashboard         | `/doctor`                   | LayoutDashboard |
| Schedule (group)  |                             | CalendarCheck |
| → Appointments    | `/doctor/appointments`      | CalendarDays  |
| → Availability    | `/doctor/availability`      | Plus          |
| → Working Hours   | `/doctor/working-hours`     | Clock         |

### Account Section (both roles)

Profile, Settings, Logout

---

## 8. Data & Services Architecture

```
src/
├── services/
│   ├── client/           # Client-side API calls (use apiFetchClient)
│   │   ├── auth.js       # Login, signup, logout, token refresh
│   │   ├── patient.js    # Patient profile, medical records mutations
│   │   ├── appointment.js# Appointment booking/cancellation
│   │   └── schedule.js   # Schedule template CRUD
│   └── server/           # Server-side API calls (use apiFetchServer)
│       ├── doctors.js    # Get doctors list, doctor details
│       ├── patient.js    # Get patient data, medical records
│       ├── schedule.js   # Get schedules, slots, templates
│       └── appointments.js # Get appointments list
├── lib/
│   ├── api/
│   │   ├── apiFetchClient.js  # Client fetch with auto token refresh
│   │   └── apiFetchServer.js  # Server fetch with cookie-based auth
│   ├── session.js             # JWT session management
│   ├── actions.js             # Server actions
│   └── utils.js               # cn() utility (clsx + tailwind-merge)
├── contexts/
│   ├── AuthContext.js         # Current user context
│   ├── SidebarContext.js      # Sidebar open/close state
│   └── DoctorInfoContext.js   # Doctor info for booking flow
├── hooks/
│   ├── usePaginationNav.js    # URL-based pagination hook
│   └── schedule/              # Schedule-specific hooks
└── constants/
    ├── badgeColors.js         # Badge color theme map
    ├── roles.js               # User roles
    ├── pagination.js          # Pagination defaults
    ├── appointments.js        # Appointment status constants
    ├── patient.js             # Patient-related constants
    └── schedules.js           # Schedule day/time constants
```

---

## 9. Design Patterns for New Pages

When generating a new dashboard page screen in Stitch, follow these patterns:

### Page Layout Pattern

Every dashboard page should include:
1. **Page heading** at the top using `<Heading>` (xl size for page titles)
2. **Optional action buttons** next to the heading (e.g., "Add New", "Filter")
3. **Content area** with cards, lists, or grids
4. **Empty state** when no data exists
5. **Loading skeleton** for server-fetched data
6. **Error state** with retry button

### Visual Rules

- Cards: `bg-surface`, `border border-border`, `rounded-xl`, `p-5`, `shadow-sm`
- Inputs: `bg-surface-overlay`, `border-border`, `rounded-lg`, `focus:ring-primary/90`
- Buttons: `rounded-lg`, primary uses `bg-primary text-white hover:opacity-90`
- Pill badges: `rounded-full`, 10% opacity bg + high-contrast text
- Icon size: `18px` for sidebar, `16-20px` for UI actions
- Spacing: `gap-4` between cards, `py-8` for page padding
- Transitions: `transition-all` or `transition-colors duration-300`
- Active states: `active:scale-95` on buttons
- Hover cards: subtle shadow increase or background shift

### Color Usage

- **Never** use raw color values — always reference design tokens
- Use `text-text-base` for headings and primary text
- Use `text-text-muted` for descriptions and metadata
- Use `text-text-subtle` for hints, placeholders, timestamps
- Use `bg-primary` for CTAs, `bg-surface` for containers
- Use `border-border` for all separators

---

## 10. Stitch Prompt Template

When generating a new screen in Stitch, include this design context block:

```
Design System:
- Font: Inter
- Primary: #4A90E2 | Primary hover: #3A7BD5
- Surface (cards/sidebar/header): #FFFFFF light / #0F172A dark
- Page background: #F5F7FA light / #111821 dark
- Surface overlay (inputs/hover): #F1F5F9 light / #334155 dark
- Border: #E2E8F0 light / #334155 dark
- Text base: #2D3748 light / #F1F5F9 dark
- Text muted: #718096 light / #94A3B8 dark
- Text subtle: #A0AEC0 light / #64748B dark
- Success: #48BB78 | Warning: #F6AD55 | Danger: #E53E3E
- Border radius: 6px small, 8px cards/buttons, 12px large, 9999px pills
- Card shadow: 0 4px 6px rgba(0,0,0,0.1)
- Glow shadow: 0 0 20px rgba(74,144,226,0.2)

Layout:
- Dashboard shell with 280px left sidebar and 76px top header
- Main content area has light gray bg (#F5F7FA) and scrolls independently
- Content wrapped in centered container with px-6 lg:px-10, py-8
- Sidebar: white bg, right border, logo at top, nav links, account section, logout at bottom
- Header: search bar (left), dark mode toggle + notifications + user avatar (right)

Page Structure:
- Page heading (bold, large) with optional action buttons at top
- Content organized in rounded-xl cards with subtle shadow
- Forms use surface-overlay inputs with primary focus ring
- Status badges are fully rounded pills with semantic colors
- Empty states centered with icon, title, description, and CTA
- Icon library: Lucide React
```

### Use Design System Asset

When calling `generate_screen_from_text`, always set:
```
designSystem: "assets/c40ecc61ecf940ecb8a72af3d543c787"
```

This is the "MedAI Design System" which matches the actual implementation.

---

## 11. Stitch Design System Variants

| Asset ID                                  | Name                          | Mode  | Font    | Notes                    |
|-------------------------------------------|-------------------------------|-------|---------|--------------------------|
| `assets/c40ecc61ecf940ecb8a72af3d543c787` | MedAI Design System           | Light | Inter   | ✅ **Primary — matches code** |
| `assets/25aa47941af642d8a360cb3c4d923538` | Clinical Precision            | Light | Inter   | Alternative light theme  |
| `assets/2ab7803af02d4c908a15560a3d1e1c9f` | Clinical Precision (v2)       | Light | Inter   | Extended variant          |
| `assets/18b903a954864464aeb48602cbf133dc` | Clinical Intelligence Interface| Dark | Manrope | Dark mode specialized    |
