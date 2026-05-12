# MedAI Design System

A reference for all design tokens used across the MedAI frontend. Defined in `src/app/globals.css` via Tailwind's `@theme` directive.

---

## Colors

### Surfaces (theme-aware)

| Token | CSS Variable | Light | Dark |
|---|---|---|---|
| `surface` | `--color-surface` | `#FFFFFF` | `#0F172A` |
| `surface-bg` | `--color-surface-bg` | `#F5F7FA` | `#111821` |
| `surface-overlay` | `--color-surface-overlay` | `#F1F5F9` | `#334155` |

> `surface` → sidebar, header, cards
> `surface-bg` → main page / content background
> `surface-overlay` → input backgrounds, hover states

### Borders (theme-aware)

| Token | CSS Variable | Light | Dark |
|---|---|---|---|
| `border` | `--color-border` | `#E2E8F0` | `#334155` |

### Text (theme-aware)

| Token | CSS Variable | Light | Dark | Usage |
|---|---|---|---|---|
| `text-base` | `--color-text-base` | `#2D3748` | `#F1F5F9` | Primary body text |
| `text-muted` | `--color-text-muted` | `#718096` | `#94A3B8` | Secondary / supporting text |
| `text-subtle` | `--color-text-subtle` | `#A0AEC0` | `#64748B` | Placeholders, hints, disabled |

### Brand & Status (constant — same in both modes)

| Token | CSS Variable | Value | Usage |
|---|---|---|---|
| `primary` | `--color-primary` | `#4A90E2` | Buttons, links, active states |
| `primary-hover` | `--color-primary-hover` | `#3A7BD5` | Hover state for primary |
| `success` | `--color-success` | `#48BB78` | Confirmations, active badges |
| `warning` | `--color-warning` | `#F6AD55` | Alerts, pending states |
| `danger` | `--color-danger` | `#E53E3E` | Errors, destructive actions |
| `danger-muted` | `--color-danger-muted` | `#E53E3E1A` | Danger hover bg (10% opacity) |

---

## Typography

- **Font Family:** `Inter`, `system-ui`, `sans-serif`
- **Font Smoothing:** `-webkit-font-smoothing: antialiased`
- **Base Font Size:** `clamp(14px, 2vw, 16px)` (fluid, responsive)

---

## Spacing & Layout

| Token | CSS Variable | Value |
|---|---|---|
| Sidebar width | `--sidebar-width` | `240px` |
| Header height | `--header-height` | `76px` |
| Form gap | `--gap-form` | `1.5rem` |

---

## Border Radius

| Token | CSS Variable | Value | Usage |
|---|---|---|---|
| `radius-sm` | `--radius-sm` | `6px` | Small elements (badges, inputs) |
| `radius-md` | `--radius-md` | `8px` | Cards, modals, buttons |
| `radius-lg` | `--radius-lg` | `12px` | Large containers |
| `radius-full` | `--radius-full` | `9999px` | Pills, avatars, chips |

---

## Shadows

| Token | CSS Variable | Value | Usage |
|---|---|---|---|
| `shadow-sm` | `--shadow-sm` | `0 1px 3px rgba(0,0,0,0.08)` | Subtle lift |
| `shadow-md` | `--shadow-md` | `0 4px 6px rgba(0,0,0,0.1)` | Cards |
| `shadow-lg` | `--shadow-lg` | `0 10px 24px rgba(0,0,0,0.12)` | Modals, dropdowns |
| `shadow-glow` | `--shadow-glow` | `0 0 20px rgba(74,144,226,0.2)` | Primary focus glow |

---

## Dark Mode

Dark mode is toggled by adding the `.dark` class to the `<html>` or `<body>` element. No `dark:` Tailwind prefixes needed in components — the CSS variables automatically remap.

```js
// Toggle dark mode
document.documentElement.classList.toggle('dark')
```

---

## Transitions

```css
/* Applied on body — smooth theme switching */
background-color 0.3s ease;
color 0.3s ease;
```

---

## Tailwind Usage

All tokens are available as Tailwind utilities via the `@theme` directive:

```html
<!-- Colors -->
<div class="bg-surface text-text-base border border-border">...</div>
<button class="bg-primary hover:bg-primary-hover text-white">...</button>
<span class="text-text-muted">Supporting text</span>

<!-- Radius -->
<div class="rounded-[var(--radius-md)]">Card</div>
<span class="rounded-full">Pill Badge</span>

<!-- Shadows -->
<div class="shadow-[var(--shadow-md)]">Card with shadow</div>
```

---

## Stitch Prompt Snippet

When prompting Stitch to generate or edit screens, include this block:

```
Design System:
- Font: Inter
- Primary: #4A90E2 | Primary hover: #3A7BD5
- Surface (cards): #FFFFFF light / #0F172A dark
- Page bg: #F5F7FA light / #111821 dark
- Surface overlay (inputs/hover): #F1F5F9 light / #334155 dark
- Border: #E2E8F0 light / #334155 dark
- Text base: #2D3748 light / #F1F5F9 dark
- Text muted: #718096 light / #94A3B8 dark
- Text subtle: #A0AEC0 light / #64748B dark
- Success: #48BB78 | Warning: #F6AD55 | Danger: #E53E3E
- Border radius: 6px small, 8px cards/buttons, 12px large, 9999px pills
- Card shadow: 0 4px 6px rgba(0,0,0,0.1)
- Glow shadow: 0 0 20px rgba(74,144,226,0.2)
```
