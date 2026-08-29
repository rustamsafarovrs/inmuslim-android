# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**inmuslim-android** — Android app showing Islamic prayer times for a selected region, plus a tasbih (dhikr counter) feature with history. Push notifications via FCM, in-app review and in-app update.

- **App ID**: `tj.rsdevteam.inmuslim` (debug builds get `.beta` suffix and app name "Inmuslim Beta")
- **Version**: 1.4.0 (code 9) — set in `app/build.gradle.kts`
- **Min SDK 24 / compile & target SDK 37 / Java 21**
- **API base URL**: `https://rsdevteam.ru/inmuslim/api/` — a `buildConfigField` in `core/build.gradle.kts`, read via `BuildVars.BASE_URL`

---

## Build, Test, Lint

```bash
./gradlew assembleDebug                      # debug APK
./gradlew installDebug
./gradlew test                               # all unit tests, all modules
./gradlew :feature:tasbih:testDebugUnitTest  # one module's unit tests
./gradlew :feature:tasbih:testDebugUnitTest --tests '*TasbihDatesTest'         # one class
./gradlew :feature:tasbih:testDebugUnitTest --tests '*GetTasbihUseCaseTest.<method>'  # one test
./gradlew detekt                             # all modules
./gradlew :app:detekt --auto-correct         # formatting rules are auto-fixable
./gradlew lint                               # warningsAsErrors = true
./gradlew installGitHooks                    # pre-commit hook: detekt + lint
```

The pre-commit hook (`scripts/git-hooks/pre-commit.sh`) runs `detekt` **and** `lint` and blocks the commit on failure — run both locally before committing. CI (`.github/workflows/static-analysis.yml`) runs `./gradlew detekt` and `./gradlew lint` as two parallel jobs on every push and PR; `.github/workflows/test-action.yml` runs the unit tests.

**Signing**: put these in `local.properties`; keystores live in `config/`.

```
DEBUG_STORE_PASSWORD=...   DEBUG_KEY_ALIAS=...   DEBUG_KEY_PASSWORD=...
RELEASE_STORE_PASSWORD=... RELEASE_KEY_ALIAS=... RELEASE_KEY_PASSWORD=...
```

Debug builds are *not* release-shrunk; release enables minify + resource shrinking.

---

## Modules

```
:app             application — activities, navigation graph, home/region/settings screens,
                 Retrofit Api + network repositories, FCM service, AppModule
:core            Router/Screen, Resource, BaseState, TextRes, TitleValue, theme,
                 DateUtils/NumberFormatter, BuildVars (owns BASE_URL BuildConfig field)
:data            SharedPreferences wrapper (`Preferences`) + DataModule only
:analytics       AnalyticsTracker/AnalyticsEvent/AnalyticsScreen + FirebaseAnalyticsTracker;
                 `explicitApi()` is on, so every public declaration needs an explicit modifier
:res             ALL string/drawable/font resources — R class is `tj.rsdevteam.inmuslim.res.R`
:uicomponents    shared Compose widgets (namespace `tj.rstech.uicomponents`): Button,
                 ProgressIndicator, LargeTopAppBar, ErrorBottomSheet(+Config)
:feature:tasbih  self-contained feature: Room DB, repository, use cases, 4 screens
```

Dependency direction: `app` → everything; `feature:tasbih` → `analytics`, `core`, `data`, `res`, `uicomponents`; `uicomponents` → `core`, `res`; `core` → `res`; `analytics` → nothing in the project.

**Gotcha:** the `:data` module holds *only* `Preferences` and its Hilt module. The Retrofit `Api`, DTOs, mappers and network repositories still live under `app/src/main/java/.../data/`. Don't assume network code is in `:data`.

`build.gradle.kts` (root) applies detekt and the Compose stability analyzer to every Kotlin module automatically in an `afterEvaluate` block, and configures shared lint baseline/`warningsAsErrors`. New modules inherit this — don't re-declare the detekt plugin per module.

---

## Architecture

MVVM + unidirectional data flow, Compose UI throughout.

```
Composable  →  *UIEvent  →  ViewModel  →  UseCase (tasbih) / Repository  →  Api | Room | Preferences
     ↑                          │
  *ScreenState  ←───────────────┘   (VM → UI one-shot signals: *VMEvent Flow, e.g. LaunchVMEvent)
```

### State & events

- Each screen has an immutable `*ScreenState` data class; the ViewModel exposes it as `var state by mutableStateOf(...)` with `private set`. No `StateFlow` for screen state.
- Newer screens embed `BaseState` (`isLoading`, `error`, `isFormValid`, `isOffline`) as a `base` field and update it with `state.copy(base = state.base.copy(...))`.
- UI → VM goes through a `*UIEvent` sealed class handled by a single `fun handleEvent(event: ...)`. VM → UI one-shot signals use a `*VMEvent` Flow (see `LaunchViewModel`/`LaunchActivity`).
- Async results are wrapped in `Resource<T>` (`InProgress` / `Success` / `Error`). Repositories return `Flow<Resource<T>>` built with `flow { emit(Resource.InProgress()); ... }`.

### Screen composable pattern

Every screen is a **pair** of composables — a public no-arg one that wires up DI/navigation, and a private stateless one with default-valued lambdas so `@Preview` works:

```kotlin
@Composable
fun TasbihListScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihListViewModel = hiltViewModel()
    TasbihListScreen(
        state = viewModel.state,
        didClickBack = { router.navigateUp() },
        didSelectTasbih = { router.navigate(Screen.TasbihCalculator(it.id)) },
    ) { viewModel.handleEvent(it) }
}

@Composable
private fun TasbihListScreen(
    state: TasbihListScreenState,
    didClickBack: () -> Unit = {},
    eventHandler: (TasbihListUIEvent) -> Unit = {},
) { ... }
```

Navigation callbacks are named `didClickX` / `didSelectX`; UI events are named `DidClickX` / `DidDismissX`.

### Navigation

Type-safe Navigation Compose. Routes are `@Serializable` objects/data classes in `core/router/Screen.kt`; the graph lives in `MainActivity.Navigation()` using `composable<Screen.X>`. Navigation goes through `Router` (`navigate`, `navigateUp`, `navigateAsRoot`, `navigateWithReplace`, `popUpToMain`, `toRoute<T>()`), obtained from `LocalRouter.current` — never inject or pass a `NavController`. Adding a screen means: new `Screen` entry + `composable<>` registration in `MainActivity`.

`LaunchActivity` is the launcher; it decides via `LaunchViewModel` whether onboarding is needed and starts `MainActivity` with the `Const.OPEN_ONBOARDING` extra, which selects `Screen.Regions` vs `Screen.Main` as start destination.

### DI

Hilt everywhere. `@HiltViewModel` ViewModels, `hiltViewModel()` in composables, `@AndroidEntryPoint` activities, `@HiltAndroidApp` on `App`. Modules are per-concern and per-module: `app/di/modules/AppModule` (Moshi, OkHttp, Retrofit, `Api`, `ErrorHandler`), `data/di/DataModule` (`Preferences`), `feature/tasbih/di/DatabaseModule` (Room) and `RepositoryModule` (`@Binds` interface → impl).

### Networking

Retrofit + Moshi with `ResultCallAdapterFactory`, so `Api` methods return `Result<DTO>`. Repositories check `result.isSuccess && result.getOrNull()?.result == 0`, map the DTO via an extension in `Mappers.kt`, and otherwise delegate to `ErrorHandler.getError(result)`, which converts to `ApiException` / `ConnectionTimeoutException` / `UnknownException`. Logging interceptor is BODY only when `BuildVars.BUILD_TYPE == BuildType.TEST`.

```
GET  /region/get-regions
GET  /timing/get-timing?regionId={id}
POST /user/register-user
POST /messaging/update-messaging-id
```

`UserRepository.needRegister()` returns false in TEST builds — device registration is deliberately disabled for debug.

### Analytics

`AnalyticsTracker` (`:analytics`) is the only way anything is reported, and `AnalyticsEvent` is a
sealed catalogue of every event — adding a case there is how you add an event, so the taxonomy stays
in one reviewable file. Inject `AnalyticsTracker` into the ViewModel and log from `handleEvent` or
`init`; screen views are *not* logged per screen, `MainActivity.TrackScreenViews` reports them
centrally off the nav back stack, so a new screen needs a new `AnalyticsScreen` entry and a branch in
`NavDestination.toAnalyticsScreen()`. `FirebaseAnalyticsTracker` is the only class touching the
Firebase SDK. Event names must be snake_case, ≤ 40 chars and never prefixed `firebase_`/`google_`/`ga_`.

Identity is separate from events: `setUserId` and `setUserProperty(AnalyticsProperty.REGION_ID, …)`
are re-applied by `LaunchViewModel.identify()` on every cold start, and pushed again the moment they
change (`HomeViewModel` after a successful registration, `RegionViewModel` on a confirmed region).

### Tasbih feature

Adds a layer the rest of the app doesn't have: `domain/usecases/*UseCase` classes with `operator fun invoke(...)`, sitting between ViewModels and `TasbihRepository` (interface + `TasbihRepositoryImpl`). Persistence is Room (`tasbih_db`, version 1, `exportSchema = false`), seeded with five default dhikr strings in a `RoomDatabase.Callback.onCreate`. Bumping the schema requires a version bump + migration there. Dates are stored as ISO `yyyy-MM-dd` strings; `TasbihDates.kt` resolves today/yesterday into a `RelativeDay`.

---

## Conventions

**Formatting belongs in the UI layer.** ViewModels and domain models keep raw data (`"04:00:10"` strings, minutes as `Int`, ISO dates). Composables read `LocalContext.current.is24HourFormat()` once and pass it to `TimeUtils.formatTime` / `TimeUtils.formatMinutes` at display time; counts go through `NumberFormatter.format`. Never pre-format in a ViewModel or store a formatted copy of a domain model in state.

**Strings** all live in `:res` (`values/` is Tajik — the default — plus `values-en/` and `values-ru/`), named `<scope>_<kind>_<name>`: `base_*`, `common_*`, `tasbih_*` with kinds `title`, `action`, `description`, `prayer`, `other`. Add a new string to all three locale files. For text that must travel through non-Composable layers, use the `TextRes` sealed class (`Raw` / `Res` / `ResParams`) and resolve it with `TextRes.resolve()` or `Context.getTextRes(...)`.

**Detekt** (`config/detekt/detekt.yml`, baseline `config/detekt/baseline.xml`): 120-char lines, 4-space indent, `CyclomaticComplexMethod` 15, `LongMethod` 60, `ReturnCount` 2, `LongParameterList` 6 (7 for constructors), `TooManyFunctions` 11, `MagicNumber` active, `ComplexCondition` 4. `FIXME:` and `STOPSHIP:` comments are forbidden. Prefer a scoped `@Suppress` with a clear reason over editing the baseline.

**Git**: base feature branches on `develop` and PR into `develop` — PRs to `master` are rejected. Branches are `feat/...` or `fix/...`. Update `CHANGELOG.md` under the current version heading for user-visible changes.

---

## Local Persistence

`Preferences` (`:data`) wraps a single `prefs` SharedPreferences file: `region_id`, `user_id`, `firebase_token`, `review_shown`, `haptic_enabled`. `-1L` is the "unset" sentinel for the two IDs.
