# Testing `:app`

How to test, in general: [`docs/architecture.md#testing`](../docs/architecture.md#testing).

```bash
./gradlew :app:testDebugUnitTest                  # JVM, runs in CI
./gradlew :app:testDebugUnitTest --tests '*HomeViewModelTest'
./gradlew :app:connectedDebugAndroidTest          # device or emulator required
```

## JVM tests (`src/test`)

| Package             | Test                    | Covers                                                                    |
|---------------------|-------------------------|---------------------------------------------------------------------------|
| `data.repositories` | `MappersTest`           | DTO → model mapping; each prayer time stays in its own field              |
|                     | `ErrorHandlerTest`      | unreachable host / IO → `ConnectionTimeoutException`, HTTP → `ApiException`, `MessageDTO` → its `msg`, anything else → `UnknownException` |
|                     | `RegionRepositoryTest`  | progress then result, mapping, rejected / offline responses, saved region |
|                     | `TimingRepositoryTest`  | requests the saved region's times, mapping, error mapping                 |
|                     | `UserRepositoryTest`    | registration saves the user id only on success; `needRegister()` in debug; review flag |
| `ui.home`           | `HomeViewModelTest`     | times + hero + statuses, first-load loader, error sheet and dismiss, refresh, review flag |
|                     | `PrayerScheduleTest`    | which prayer is current or next at a given second, countdown, progress, statuses |
|                     | `PrayerTest`            | `Timing.timeOf` reads each prayer's own field                             |
| `ui.launch`         | `LaunchViewModelTest`   | onboarding vs main, `AppLaunched`, user id and region identity (`null`, never `"-1"`) |
| `ui.region`         | `RegionViewModelTest`   | loading, error and retry, one region selected at a time, confirm saves and tags analytics |
| `ui.settings`       | `SettingsViewModelTest` | each click is reported                                                    |
| `utils`             | `TimeUtilsTest`         | parsing, 12/24-hour formatting, countdown                                 |

### Writing a ViewModel test

The ViewModels here take **concrete** repositories. Build real ones from fakes, so the repository
and its error mapping are tested along with the ViewModel:

```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()                  // :core test fixture

private val api = FakeApi()
private val preferences = inMemoryPreferences()                // :data test fixture
private val analytics = FakeAnalyticsTracker()                 // :analytics test fixture

private fun viewModel() = RegionViewModel(analytics, RegionRepository(api, preferences, ErrorHandler()))
```

`fakes/FakeApi` has one property per endpoint (`regions`, `timing`, `registerUser`,
`updateMessagingId`). Assign a `Result` before the call; an endpoint left unassigned fails with
"unused". It records `timingRequests` and `registerRequests`. To see a loading state, call
`holdResponses()` and every call stays suspended until `releaseResponses()`.

A `*VMEvent` sent through a `Channel` is read with `runTest { viewModel().event.first() }`.

### Things to know

- **`HomeViewModelTest` starts from a registered install with a push token.** A fresh install
  would make `HomeViewModel` call `UserRepository.updateMessagingId()` → `FirebaseMessaging`, or
  register with `Utils.getDeviceInfo()` → `Build.*`, and neither works on the JVM.
- **Unit tests only exist for debug**, where `BuildVars.BUILD_TYPE` is `TEST` and `needRegister()`
  is always `false`. The release branch can't be reached.
- **The hero uses the real clock.** `HomeViewModelTest` checks that a hero and all seven statuses
  exist. Which prayer is current at which second is `PrayerScheduleTest`'s job, which passes the
  time in.
- **Known bug:** a response with `result != 0` loses the server's `msg`. `ErrorHandler` only reads
  it from a `MessageDTO`, which no response type is, so the error ends up as `UnknownException`.
  The repository tests only check that an `Error` comes back, so fixing this won't break them.

## Instrumented tests (`src/androidTest`)

| Test                           | Covers                                                                         |
|--------------------------------|--------------------------------------------------------------------------------|
| `DefaultLocaleInitializerTest` | new install → `tg-TJ`; keeps a language restored from a backup; keeps an updated install's language; keeps a language chosen later. API 33+ only |
| `AnalyticsMappersTest`         | each `Screen` maps to its own `AnalyticsScreen`, and every `AnalyticsScreen` is reachable |

- `DefaultLocaleInitializerTest` clears the `prefs` file and the per-app language before and after
  each test, because `App.onCreate` has already run the initializer by the time a test starts.
- `AnalyticsMappersTest` builds its own navigation graph. **When you add a `Screen`, register it
  there too**, next to the new branch in `toAnalyticsScreen()`.
- `connectedDebugAndroidTest` installs the debug app (`tj.rsdevteam.inmuslim.beta`) and
  **uninstalls it afterwards**, along with its data on that device.

## Not tested

- Composables (home, region, settings screens and their components): there are no Compose UI tests.
- `MainActivity`, `LaunchActivity`, `MessagingService` and `InAppUpdateManager`, which wrap
  Android, Firebase or Play APIs.
- `UserRepository.updateMessagingId()`, until fetching the Firebase token sits behind an interface.
- The once-a-second tick in `HomeViewModel` (`DidStart` / `DidStop`).
