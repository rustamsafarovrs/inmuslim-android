# CLAUDE.md

## Project Overview

**inmuslim-android** is an Android app that displays Islamic prayer times for a selected region. It supports push notifications, in-app review, and has a region selection onboarding flow.

- **App ID**: `tj.rsdevteam.inmuslim`
- **Version**: 1.1.0 (code 6)
- **Min SDK**: 24 / Target SDK: 36
- **API Base URL**: `https://rsdevteam.ru/inmuslim/api/`

---

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run detekt (code quality)
./gradlew detekt

# Install git pre-commit hooks
./gradlew installGitHooks
```

**Signing**: Copy signing credentials into `local.properties`:
```
DEBUG_STORE_PASSWORD=...
DEBUG_KEY_ALIAS=...
DEBUG_KEY_PASSWORD=...
RELEASE_STORE_PASSWORD=...
RELEASE_KEY_ALIAS=...
RELEASE_KEY_PASSWORD=...
```
Keystores are stored in `config/`.

---

## Architecture

**MVVM** with Jetpack Compose UI and reactive state via Coroutines/Flow.

### Layer Structure

```
UI Layer       →  Screen composables + ViewModels
Domain Layer   →  Repositories, Resource<T> wrapper
Data Layer     →  Retrofit API, SharedPreferences, DTOs + Mappers
```

### Key Patterns

- **State**: Each screen has an immutable `*ScreenState` data class; ViewModels expose it via `mutableStateOf()`
- **Events**: UI → ViewModel via `*UIEvent` sealed classes; ViewModel → UI via `*VMEvent` Flow/Channel
- **Async results**: `Resource<T>` sealed class with `InProgress`, `Success`, `Error` states
- **Navigation**: `Router` wraps `NavController`; exposed via `LocalRouter` CompositionLocal; routes defined in `Screen` sealed class
- **DI**: Hilt throughout; `@HiltViewModel` on ViewModels, `@HiltAndroidApp` on `App`
- **Formatting belongs in the UI layer**: ViewModels and domain models store raw data (e.g. `"04:00:10"` strings, minutes as `Int`). Composables read `LocalContext.current.is24HourFormat()` once and pass it to `TimeUtils.formatTime`/`TimeUtils.formatMinutes` at display time. Never pre-format data in a ViewModel or store a formatted copy of a domain model in state.

---

## Module Structure

```
inmuslim-android/
├── app/          # Main application module
├── core/         # Router, Screen definitions, Theme
└── res/          # Shared resources
```

### App Module Package Layout

```
tj.rsdevteam.inmuslim/
├── App.kt
├── ui/
│   ├── MainActivity.kt
│   ├── launch/     # Splash / onboarding (LaunchActivity)
│   ├── home/       # Prayer times screen
│   ├── region/     # Region selection screen
│   ├── settings/   # Settings screen
│   └── common/     # Shared composables, ErrorBottomSheet
├── data/
│   ├── api/        # Retrofit interface (Api.kt)
│   ├── models/     # DTOs, domain models, Resource.kt
│   ├── repositories/
│   ├── preferences/
│   └── exceptions/
├── di/modules/     # AppModule (Hilt)
├── services/       # MessagingService (FCM)
└── utils/          # TimeUtils, ContextExt, etc.
```

---

## Key Technologies

| Category | Library / Version |
|---|---|
| UI | Jetpack Compose BOM 2026.02.01 |
| Navigation | Navigation Compose 2.9.7 |
| DI | Dagger Hilt 2.59.2 |
| Networking | Retrofit 3.0.0, OkHttp 5.3.2 |
| JSON | Moshi 1.15.2, Kotlinx Serialization 1.10.0 |
| Firebase | Crashlytics, Analytics, FCM |
| Kotlin | 2.3.10, Coroutines, KSP 2.3.5 |
| Code Quality | Detekt 1.23.8 |

---

## API Endpoints

```
GET  /region/get-regions                  # List regions
GET  /timing/get-timing?regionId={id}     # Prayer times for region
POST /user/register-user                  # Register device
POST /messaging/update-messaging-id       # Update FCM token
```

---

## Code Style & Linting

Detekt is configured in `config/detekt/detekt.yml` with a baseline at `config/detekt/baseline.xml`.

Key rules:
- Max line length: **120 characters**
- Indentation: **4 spaces**
- `CyclomaticComplexity` threshold: 15
- `LongMethod` threshold: 60 lines
- `ReturnCount` max: 2
- `TODO`/`FIXME`/`STOPSHIP` comments are **forbidden**

CI runs `./gradlew detekt` on every push and PR (see `.github/workflows/detekt-action.yml`).

---

## Testing

Unit tests live in `app/src/test/`. Run with:

```bash
./gradlew test
```

`TimeUtilsTest.kt` covers prayer-time calculation logic (time-to-minutes conversion, 12/24-hour formatting, current prayer detection).

---

## Local Persistence

`Preferences.kt` wraps `SharedPreferences` and stores:
- `regionId` — selected region
- `userId` — device user ID
- `firebaseToken` — FCM token
- `reviewShown` — whether the in-app review prompt has been shown
