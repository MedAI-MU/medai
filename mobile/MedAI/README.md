# medAI Mobile Client

> **Kotlin Multiplatform & Compose Multiplatform Mobile Application**
> Delivering AI-powered clinical operations directly to the palms of patients, doctors, secretaries, and managers.

[![Platform Support](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg?style=for-the-badge&logo=android)](https://kotlinlang.org/docs/multiplatform.html)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.20-purple.svg?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform%201.9.0-teal.svg?style=for-the-badge&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)
[![Ktor](https://img.shields.io/badge/Ktor-3.3.2-orange.svg?style=for-the-badge&logo=ktor)](https://ktor.io/)
[![Koin](https://img.shields.io/badge/Koin-4.1.1-blue.svg?style=for-the-badge)](https://insert-koin.io/)

---

## Technical Overview

The **medAI Mobile Client** is a state-of-the-art mobile application built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. It shares 95%+ of its codebase between Android and iOS, including UI layouts, state management, business logic, networking, and data storage.

Designed for all four organizational roles—**Patients, Doctors, Secretaries, and Managers**—the application features dynamic role-based dashboards with secure workflows:

- **Patients**: Browse medical specialties, book calendar slots, view medical records/vitals, and track AI-driven lab report analysis.
- **Doctors**: Manage daily appointment queues, log diagnoses, and dictate Egyptian Arabic voice reports with native audio capture.
- **Secretaries**: Create/apply doctor weekly templates, manage appointment queues, register patients, and upload scans/reports up to 10MB.
- **Managers**: Approve pending doctor/secretary credentials, audit global platform registries, and monitor real-time hospital telemetry.

---

## Tech Stack

The architecture utilizes the industry's most advanced cross-platform KMP libraries:

- **UI Framework**: [Compose Multiplatform (CMP)](https://github.com/JetBrains/compose-multiplatform) for declarative UI rendering.
- **Dependency Injection**: [Koin](https://insert-koin.io/) for cross-platform, compile-safe dependency graphs.
- **Network Engine**: [Ktor Client 3.3.2](https://ktor.io/) with native transport engines:
  - `OkHttp` for Android (optimized pooling and connection reuse).
  - `Darwin` for iOS (integrates with Apple's native URLSession network stack).
- **Navigation Framework**: [Voyager](https://voyager.adriel.cafe/) implementing Screen-model scoping, tabbed workspaces, and animated transitions.
- **Local Storage**: [Jetpack DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore) for platform-agnostic, secure key-value storage.
- **Image Loader**: [Coil 3](https://coil-kt.github.io/coil/) equipped with Ktor Network pipelines for asynchronous, cached image loading.
- **Concurrency**: Kotlin Coroutines and Flows for event streams and async task handling.
- **On-Device Diagnostics**: [LiteRT (TensorFlow Lite)](https://ai.google.dev/edge/litert) for running client-side classification and inference on Android.

---

## Architecture Deep Dive

The mobile codebase strictly enforces **Clean Architecture** decoupled from platform concerns and utilizes the **MVI (Model-View-Intent)** presentation pattern.

### Layer Separation

```
 ┌────────────────────────────────────────────────────────┐
 │                   PRESENTATION LAYER                   │
 │ Compose UI (Screens) ➔ ViewModels (MviScreenModel)     │
 └──────────────────────────┬─────────────────────────────┘
                            │ (Uses Use Cases)
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │                      DOMAIN LAYER                      │
 │ Use Cases ➔ Repository Interfaces ➔ Domain Entities    │
 └──────────────────────────▲─────────────────────────────┘
                            │ (Implements Interfaces)
                            ▼
 ┌────────────────────────────────────────────────────────┐
 │                       DATA LAYER                       │
 │ Network Clients (Ktor) ➔ Local Storage (DataStore)     │
 │ DTO Models ➔ Mappers ➔ Repository Implementations      │
 └────────────────────────────────────────────────────────┘
```

1. **Domain Layer**: The core of the application, representing business logic. Contains domain entities, repository interfaces, and use cases (e.g., `UploadReportUseCase`, `PollVoiceReportStatusUseCase`). It has **zero dependencies** on external libraries or frameworks (except Koin for injection).
2. **Data Layer**: Responsible for retrieving and persisting data. Implements the repository interfaces defined in the domain layer. Communicates with the NestJS API via Ktor and handles local settings using Jetpack DataStore.
3. **Presentation Layer**: Handles user interactions. Screens are written in Compose and leverage Voyager's `ScreenModel` (ViewModel alternative) to manage state via the MVI pattern.

---

### MVI Pattern (Model-View-Intent)

Each screen operates on a strict unidirectional data flow governed by [MviScreenModel](file:///f:/AndroidStudioProjects/medai/mobile/MedAI/composeApp/src/commonMain/kotlin/org/example/project/core/presentation/mvi/MviScreenModel.kt):

- **State**: A single immutable class representing the screen's UI configuration at any given frame.
- **Event**: Explicit user actions (e.g., clicking a button, recording audio) dispatched to the `ScreenModel`.
- **Effect**: One-off side effects (e.g., navigation events, displaying a Snackbar alert) processed through a buffered Kotlin Coroutines Channel.

#### MVI Flow Diagram
```
        ┌──────────────────────────────────────────────┐
        │                                              │
        │                  Compose UI                  │
        │                                              │
        └──────┬────────────────────────────────▲──────┘
               │                                │
               │ Dispatches Event               │ Observes State
               │ (e.g. UploadScanClicked)       │ (e.g. isUploading = true)
               ▼                                │
        ┌───────────────────────────────────────┴──────┐
        │                                              │
        │                MviScreenModel                │
        │                                              │
        └──────┬───────────────────────────────────────┘
               │
               │ Triggers Use Cases & Emits Effect
               ▼ (e.g. ShowSnackbar("Upload Success"))
        ┌──────────────────────────────────────────────┐
        │              One-Shot Effects                │
        │              (Voyager / SnackBar)            │
        └──────────────────────────────────────────────┘
```

---

## Key Technical Features

### 1. Ktor Bearer Authentication & Silent Token Refresh

The network layer implements automated token rotation via the Ktor `Auth` plugin combined with a custom `HttpSend` interceptor:
- **Automatic Header Binding**: Injects JWT Bearer tokens to all requests destined for the backend.
- **Auto-Stripping for Security**: Excludes authorization headers when requesting assets from external hosts (e.g., Azure Blob Storage) to prevent credential leakage.
- **Silent Refresh Interception**: When a request fails with an unauthorized status code, Ktor halts outgoing calls, initiates an asynchronous POST request to `/auth/refresh-token` using a dedicated HTTP refresh client, saves the new session credentials, and retries the original request seamlessly.

```kotlin
install(Auth) {
    bearer {
        loadTokens {
            val accessToken = sessionManager.getUserToken()
            if (accessToken != null) BearerTokens(accessToken, "") else null
        }
        refreshTokens {
            // Triggered automatically on 401 Unauthorized
            val refreshResponse = refreshClient.post("auth/refresh-token")
            if (refreshResponse.status == HttpStatusCode.OK) {
                val newAccessToken = sessionManager.parseAccessTokenFromCookies()
                if (newAccessToken != null) {
                    sessionManager.updateUserToken(newAccessToken)
                    BearerTokens(newAccessToken, "")
                } else null
            } else {
                sessionManager.clearSession() // Session expired, force logout
                null
            }
        }
    }
}
```

---

### 2. Native Multiplatform Audio Recording

Medical voice transcription demands high-fidelity audio input. We developed an abstract `AudioRecorder` interface in `commonMain` to support platform-specific hardware acceleration:
- **Android Implementation**: Utilizes the native [Android MediaRecorder API](file:///f:/AndroidStudioProjects/medai/mobile/MedAI/composeApp/src/androidMain/kotlin/org/example/project/domain/audio/AndroidAudioRecorder.kt), capturing high-fidelity MPEG-4 AAC formats encoded at 128kbps with a 44.1kHz sampling rate.
- **iOS Implementation**: Targets Apple's [AVFoundation Framework](file:///f:/AndroidStudioProjects/medai/mobile/MedAI/composeApp/src/iosMain/kotlin/org/example/project/domain/audio/IosAudioRecorder.kt). Configures `AVAudioSessionCategoryPlayAndRecord` to enable dual recording/playback modes and outputs mono AAC via `AVAudioRecorder`.
- **Byte Stream Delivery**: Once the recording finishes, the platform code reads the cached audio file and converts it into a `ByteArray` for direct multipart Ktor upload.

---

### 3. Background AI Polling (Flow-based)

Because AI transcribing and medical scan analysis are computationally heavy, the mobile app uses an asynchronous polling flow:
- ViewModels dispatch a job that connects to the status stream.
- The use case returns a cold `Flow` that periodically pulls statuses from the API (`processing`, `completed`, `failed`) using a delay step.
- Once the status reaches `completed` or `failed`, the flow closes, updates the local MVI state, and alerts the user through single-shot effects.

```kotlin
fun startPolling(reportId: String) {
    pollingJob?.cancel()
    pollingJob = screenModelScope.launch {
        pollAnalysisStatusUseCase(reportId)
            .collectLatest { result ->
                result.onSuccess { report ->
                    setState { copy(pollingReport = report) }
                    if (report.analysisStatus == Status.COMPLETED || report.analysisStatus == Status.FAILED) {
                        pollingJob?.cancel()
                    }
                }
            }
    }
}
```

---

### 4. Multipart File Uploads & Client-Side size Validation

To prevent unnecessary bandwidth usage, particularly when uploading large laboratory PDFs or high-resolution X-ray scans, the mobile client implements a strict check on file sizes:
- **Maximum Limit**: Hard capped at **10MB** (`10 * 1024 * 1024` bytes) directly at the repository level.
- **Local Validation**: If the byte array exceeds the threshold, the client cancels the network call and emits an error event:
  ```kotlin
  if (fileBytes.size > 10 * 1024 * 1024) {
      throw IllegalArgumentException("File size exceeds the maximum limit of 10MB.")
  }
  ```
- **Stream Upload**: Utilizes `MultiPartFormDataContent` in Ktor, sending bytes along with dynamic mime-types (`application/pdf` or `image/png`).

---

### 5. Custom Unified Design System (`MedAITheme`)

The UI relies entirely on a custom design system located in `design_system/`:
- **Palette**: A high-end color scheme with support for dark mode.
- **Typography**: Custom typography tokens (`title`, `body`, `label`) mapped to Google Fonts.
- **Dimensions**: Unified padding and spacing tokens (`extraSmall`, `small`, `medium`, `large`, `extraLarge`) to enforce visual consistency across both Android and iOS devices.
- **Custom Components**: Pre-styled form widgets like custom buttons, dynamic input fields with error rendering, customized top bars, and tab bars.

---
## App Screens & Media

Below are the layout flow and design blueprints:

### 1. Authentication & Onboarding
| Patient Onboarding | Doctor/Staff Signup |
| :---: | :---: |
| <video src="https://github.com/user-attachments/assets/32d3a704-dbb0-4591-aa10-9d7071d2687a" width="250" autoplay loop muted controls></video> | <img src="https://github.com/user-attachments/assets/a1d8cff9-5ac6-4b4b-ad71-c1aa15792426" width="250" /> |

---

### 2. Patient Directory & Medical Records
| Patient Directory | Scan & Lab Report Repository |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/cbd9452a-7ab3-4a0d-bc2d-c082c5685581" width="250" /> | <img src="https://github.com/user-attachments/assets/5b8773b4-8f35-45a2-9776-ce28d7a3c763" width="250" /> |
| <img src="https://github.com/user-attachments/assets/47f61a93-26d6-449c-88a3-c1cdf7447e4a" width="250" /> | |

---

### 3. AI Voice Reports (Doctor Dictation)
| Voice Recorder UI | AI Audio Processing Status |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/a4cdeb00-3ba5-4095-bac2-92096f84a2f2" width="250" /> | <img src="https://github.com/user-attachments/assets/221d9825-adcc-427e-bfa6-90f4d0aeee66" width="250" /> |
| <img src="https://github.com/user-attachments/assets/f06248d4-01eb-4475-a60c-67a9470992a2" width="250" /> | |

---

### 4. Lab Report AI Analysis
| Lab Analysis Summary | Active AI Polling Status |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/f03712ba-9452-47dc-abb9-698c68d4966b" width="250" /> | <img src="https://github.com/user-attachments/assets/dbcb8d52-3e26-4cc3-8ac9-26fd7f02523d" width="250" /> |

---

### 5. Secretary Workflows
| Schedule Templates & Slots | Booking Approvals Queue |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/ba546be0-5abe-4bca-9c24-8d3f8281899b" width="250" /> | <img src="https://github.com/user-attachments/assets/8ca5b3b9-7bf7-4bc7-848c-d37f737e6015" width="250" /> |
| <img src="https://github.com/user-attachments/assets/18dff963-4106-4fd9-b232-56443d613a87" width="250" /> | |

---

### 6. Manager Workflows
| Doctor & Secretary Approvals | Global Hospital Telemetry |
| :---: | :---: |
| <img src="https://github.com/user-attachments/assets/c44eddd7-eeab-43fb-88a2-825b7f92364a" width="250" /> | <img src="https://github.com/user-attachments/assets/e7ea163e-a084-4f20-85f6-8fa51d0b0461" width="250" /> |

---

## Project Structure & Navigation Route

```
mobile/MedAI/composeApp/src/commonMain/kotlin/org/example/project/
├── core/
│   └── presentation/mvi/      # Base state machine (MviScreenModel)
│
├── data/
│   ├── remote/                # KtorClient configurations & auth interceptors
│   └── repository/            # API data synchronization logic
│
├── domain/
│   ├── audio/                 # Native audio recorder common interface
│   ├── model/                 # Plain domain models
│   └── usecase/               # Single-responsibility business blocks
│
├── presentation/              # Voyager screens & MVI controllers
│   ├── homeScreen/            # Role-based dashboards (Patient, Doctor, Secretary, Manager)
│   ├── appointmentScreen/     # Dynamic booking and voice reports
│   ├── reportAnalysis/        # Lab OCR and parsing status flow
│   ├── shared/                # Patient directory & medical charts
│   ├── secretary/             # Secretary-specific queues and workspaces
│   └── manager/               # Manager approval panels and stats charts
│
└── design_system/             # Theme configurations, dimensions, custom widgets
```

---

## Getting Started

Refer to the [Root README](file:///f:/AndroidStudioProjects/medai/README.md#2-compiling--launching-the-mobile-app) for instructions on setting up your local properties, starting dependencies, and compiling the mobile app onto an emulator or physical device.
