# Architecture

How a screen is put together, from the composable down to the data source, and how the
lower layers are tested. The `:feature:tasbih` module is the reference implementation:
it has every layer described here.

- [Overview](#overview)
- [Composable](#composable)
- [UIEvent](#uievent)
- [ViewModel](#viewmodel)
- [VMEvent](#vmevent)
- [UseCase](#usecase)
- [Repository](#repository)
- [Adding a screen](#adding-a-screen)
- [Testing](#testing)

---

## Overview

```
                *UIEvent                  invoke()                  call
  Composable ─────────────▶  ViewModel ─────────────▶  UseCase ─────────────▶  Repository ──▶ Room / Api / Preferences
             ◀─────────────            ◀─────────────          ◀─────────────
              *ScreenState            Flow<Resource<T>>       Flow<Resource<T>>
             ◀ ─ ─ ─ ─ ─ ─
              *VMEvent (one-shot)
```

| Layer      | Lives in                     | Knows about                         | Never knows about              |
|------------|------------------------------|-------------------------------------|--------------------------------|
| Composable | `ui/<screen>/*Screen.kt`     | `*ScreenState`, `*UIEvent`, `Router` | use cases, repositories        |
| ViewModel  | `ui/<screen>/*ViewModel.kt`  | use cases, `AnalyticsTracker`       | Room, Retrofit, `Context`      |
| UseCase    | `domain/usecases/`           | repository **interfaces**           | Android, UI state              |
| Repository | `data/repositories/`         | DAO, `Api`, `Preferences`, mappers  | ViewModels, UI state           |

Data flows one way: the UI sends events up, the ViewModel writes new state, Compose redraws.

> The screens in `:app` (home, region, settings, launch) predate the use case layer and call
> repositories straight from the ViewModel. New features follow the `:feature:tasbih` shape.

---

## Composable

Every screen is a pair: a **public** composable that wires up Hilt and navigation, and a
**private** stateless one that only draws `state` and reports back through lambdas.

```kotlin
@Composable
fun TasbihScreen() {
    val router = LocalRouter.current
    val viewModel: TasbihViewModel = hiltViewModel()
    val tasbihId = router.toRoute<Screen.TasbihCalculator>().tasbihId
    TasbihScreen(
        state = viewModel.state,
        handleEvent = viewModel::handleEvent,
        didClickBack = router::navigateUp,
        didClickEntryHistory = { router.navigate(Screen.TasbihEntryHistory(tasbihId)) },
    )
}

@Composable
private fun TasbihScreen(
    state: TasbihScreenState,
    handleEvent: (TasbihUIEvent) -> Unit,
    didClickBack: () -> Unit,
    didClickEntryHistory: () -> Unit,
) { ... }
```

Rules:

- The private composable never touches a ViewModel, so `@Preview` can render it with a
  hand-built state.
- **Plain navigation goes straight to `Router`** through a `didClickX` / `didSelectX` lambda.
  It doesn't pass through the ViewModel unless the ViewModel has to decide something first
  (then it's a [VMEvent](#vmevent)).
- Formatting happens here, not in the ViewModel: read `LocalContext.current.is24HourFormat()`
  once and pass it to `TimeUtils`; counts go through `NumberFormatter.format`.
- Strings come from `tj.rsdevteam.inmuslim.res.R`.

---

## UIEvent

Everything the user does that the ViewModel should know about is a case of one sealed class
per screen.

```kotlin
sealed class TasbihUIEvent {
    object DidTap : TasbihUIEvent()
    object DidClickReset : TasbihUIEvent()
    object DidConfirmReset : TasbihUIEvent()
    object DidDismissResetConfirm : TasbihUIEvent()
    object DidToggleHaptic : TasbihUIEvent()
}

sealed class TasbihListUIEvent {
    object DidClickShowAddDialog : TasbihListUIEvent()
    object DidDismissAddDialog : TasbihListUIEvent()
    data class DidClickAdd(val name: String) : TasbihListUIEvent()   // payload → data class
}
```

- Name cases after what happened, in the past tense: `DidClickX`, `DidDismissX`, `DidToggleX`.
  The event describes the user's action; the ViewModel decides what it means.
- `object` for no payload, `data class` when the event carries input.
- The ViewModel has exactly one public entry point, `handleEvent`, with an exhaustive `when`.
  Adding a case without handling it is a compile error.

```kotlin
fun handleEvent(event: TasbihUIEvent) {
    when (event) {
        is TasbihUIEvent.DidTap -> handleTap()
        is TasbihUIEvent.DidClickReset -> state = state.copy(showResetConfirm = true)
        is TasbihUIEvent.DidDismissResetConfirm -> state = state.copy(showResetConfirm = false)
        is TasbihUIEvent.DidConfirmReset -> handleReset()
        is TasbihUIEvent.DidToggleHaptic -> handleToggleHaptic()
    }
}
```

---

## ViewModel

```kotlin
@HiltViewModel
class TasbihListViewModel @Inject constructor(
    private val analytics: AnalyticsTracker,
    private val observeTasbihsUseCase: ObserveTasbihsUseCase,
    private val addTasbihUseCase: AddTasbihUseCase,
) : ViewModel() {

    var state by mutableStateOf(TasbihListScreenState())
        private set

    init {
        observeTasbihs()
    }

    private fun observeTasbihs() {
        viewModelScope.launch {
            observeTasbihsUseCase().collect { rs ->
                state = when (rs) {
                    is Resource.InProgress -> state.copy(base = state.base.copy(isLoading = true))
                    is Resource.Success -> state.copy(
                        tasbihs = rs.data,
                        base = state.base.copy(isLoading = false, error = null),
                    )
                    is Resource.Error -> state.copy(
                        base = state.base.copy(isLoading = false, error = rs.error),
                    )
                }
            }
        }
    }

    fun handleEvent(event: TasbihListUIEvent) { ... }
}
```

**State**

- One immutable `*ScreenState` data class, exposed as `var state by mutableStateOf(...)` with
  `private set`. No `StateFlow`.
- Change it only with `state = state.copy(...)`.
- Loading / error / offline / form-valid flags live in an embedded `base: BaseState` and are
  updated with `state.copy(base = state.base.copy(...))`.
- Anything that must still be on screen after a rotation — an open dialog, a bottom sheet — is
  a field in state (`showResetConfirm`, `showAddDialog`), not a VMEvent.

**Dependencies**

- Inject **use cases**, never repositories or DAOs directly.
- Route arguments come from `SavedStateHandle`:
  `savedStateHandle.toRoute<Screen.TasbihCalculator>().tasbihId`.
- Analytics is logged from `init` or `handleEvent` through `AnalyticsTracker`. Screen views are
  tracked centrally in `MainActivity`, not here.

**Writes**

Repository flows are cold. A write does nothing until it is collected, so fire-and-forget
writes still need an empty `collect {}`:

```kotlin
private fun handleTap() {
    val count = state.count + 1
    state = state.copy(count = count)                          // optimistic: UI updates instantly
    viewModelScope.launch { incrementTasbihUseCase(tasbihId).collect {} }
}
```

---

## VMEvent

A VMEvent is a **one-shot effect** that the ViewModel triggers and the UI performs exactly once:
navigate after a save succeeds, finish an activity, open a system screen.

| Use                          | For                                                        |
|------------------------------|------------------------------------------------------------|
| `*ScreenState` field         | Anything that should still be shown after recomposition or rotation (dialogs, sheets, errors). |
| `didClickX` lambda → `Router` | Navigation the user triggers directly with a click.        |
| `*VMEvent`                   | An effect the ViewModel decides on, usually after async work. |

**Declare** a sealed class next to the screen:

```kotlin
sealed class LaunchVMEvent {
    data class OpenMain(val openOnboarding: Boolean) : LaunchVMEvent()
}
```

**Emit** through a `Channel` exposed as a `Flow`:

```kotlin
private val _event = Channel<LaunchVMEvent>()
val event = _event.receiveAsFlow()

private fun checkRegion() {
    viewModelScope.launch {
        val openOnboarding = regionRepository.getRegionId() <= 0
        _event.send(LaunchVMEvent.OpenMain(openOnboarding))
    }
}
```

Use `Channel`, not `SharedFlow`: a channel holds the event until the UI starts collecting
(e.g. after a rotation) and hands each event to one collector once. A `SharedFlow` without
replay drops events that arrive while nobody is collecting.

**Collect** only while the UI is at least `STARTED`. In an activity (`LaunchActivity`):

```kotlin
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.event.collect { event ->
            when (event) {
                is LaunchVMEvent.OpenMain -> {
                    openMain(event.openOnboarding)
                    finish()
                }
            }
        }
    }
}
```

In a screen composable, collect in the public composable so the private one stays stateless.
No screen does this yet; when one does, use this shape:

```kotlin
val lifecycleOwner = LocalLifecycleOwner.current   // androidx.lifecycle.compose
LaunchedEffect(viewModel, lifecycleOwner) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.event.collect { event ->
            when (event) {
                is XVMEvent.Close -> router.navigateUp()
            }
        }
    }
}
```

---

## UseCase

One class per action the feature can perform. It sits between the ViewModel and the
repository so ViewModels depend on a small, named operation instead of a whole repository.

```kotlin
class ResetTasbihUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(tasbihId: Long): Flow<Resource<Unit>> {
        return repository.reset(tasbihId)
    }
}
```

- Name: `<Verb><Noun>UseCase` — `Get…`, `Observe…` (long-lived flow), `Add…`, `Increment…`,
  `Reset…`, `Is…Enabled`, `Set…Enabled`.
- Expose a single `operator fun invoke(...)` so call sites read `resetTasbihUseCase(id)`.
- Constructor-inject the repository **interface**. No Hilt module needed.
- Return `Flow<Resource<T>>` for anything async. Synchronous preference reads/writes may return
  a plain value (`IsHapticEnabledUseCase(): Boolean`).
- No Android types, no UI state.

Most use cases are a one-line pass-through, and that's fine. Logic that combines several
repository calls belongs here, not in the ViewModel:

```kotlin
class GetTasbihUseCase @Inject constructor(
    private val repository: TasbihRepository,
) {

    operator fun invoke(tasbihId: Long): Flow<Resource<Tasbih>> {
        return combine(
            repository.getTasbihName(tasbihId),
            repository.getTodayCount(tasbihId),
        ) { name, count -> merge(tasbihId, name, count) }
    }

    private fun merge(tasbihId: Long, name: Resource<String>, count: Resource<Int>): Resource<Tasbih> {
        return when {
            name is Resource.Error -> Resource.Error(error = name.error)
            count is Resource.Error -> Resource.Error(error = count.error)
            name is Resource.Success && count is Resource.Success ->
                Resource.Success(Tasbih(tasbihId, name.data, count.data))
            else -> Resource.InProgress()
        }
    }
}
```

---

## Repository

The only layer that knows where data comes from. It returns domain models
(`data/models/`), never Room entities or DTOs.

**Interface + implementation**, bound in the feature's `di/RepositoryModule`:

```kotlin
interface TasbihRepository {
    fun observeTasbihs(): Flow<Resource<List<Tasbih>>>
    fun increment(tasbihId: Long): Flow<Resource<Unit>>
    fun isHapticEnabled(): Boolean
    // ...
}

@Singleton
class TasbihRepositoryImpl @Inject constructor(
    private val dao: TasbihDao,
    private val database: TasbihDatabase,
    private val preferences: Preferences,
) : TasbihRepository { ... }

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTasbih(impl: TasbihRepositoryImpl): TasbihRepository
}
```

The interface is what makes use cases testable with a fake.

### The `Resource` contract

Every repository flow emits `Resource.InProgress` first, then `Resource.Success(data)` or
`Resource.Error(error = throwable)`. Errors are emitted, never thrown out of the flow.

There are three shapes, depending on the source:

**One-shot read or write (Room / Preferences)**

```kotlin
override fun increment(tasbihId: Long): Flow<Resource<Unit>> = flow {
    emit(Resource.InProgress())
    database.withTransaction {                     // read-modify-write → one transaction
        val date = currentIsoDate()
        val current = dao.getRecord(tasbihId, date)
        dao.upsertRecord(
            TasbihRecordEntity(
                id = current?.id ?: 0,
                tasbihId = tasbihId,
                count = (current?.count ?: 0) + 1,
                date = date,
            ),
        )
    }
    emit(Resource.Success(Unit))
}.catch { emit(Resource.Error(error = it)) }
```

**Observing a Room query**

```kotlin
override fun observeTasbihs(): Flow<Resource<List<Tasbih>>> {
    return dao.observeAllWithCountFor(currentIsoDate())
        .map { entities -> Resource.Success(entities.map { toTasbih(it) }) as Resource<List<Tasbih>> }
        .onStart { emit(Resource.InProgress()) }
        .catch { emit(Resource.Error(error = it)) }
}
```

The `as Resource<…>` widens the type so `onStart` / `catch` can emit the other subclasses.

**Network (Retrofit, `:app`)**

`Api` methods return `Result<DTO>`. A call succeeds only when the HTTP call did **and** the
body's `result` is `0`; everything else goes through `ErrorHandler`, which turns it into
`ApiException`, `ConnectionTimeoutException` or `UnknownException`.

```kotlin
fun getRegions(): Flow<Resource<List<Region>>> = flow {
    emit(Resource.InProgress())
    val result = api.getRegions()
    if (result.isSuccess && result.getOrNull()?.result == 0) {
        emit(Resource.Success(result.getOrThrow().regions.map { it.toRegion() }))
    } else {
        emit(errorHandler.getError(result))
    }
}
```

### Mapping

Entity → model and DTO → model conversion lives in its own file of pure functions
(`TasbihMappers.kt`, `Mappers.kt`), not inline in the repository. That keeps it testable on the
JVM without Room or Retrofit. If a mapper depends on "now", take it as a parameter
(`toDayHistory(now: Date)`) so tests can pin the date.

---

## Adding a screen

1. `@Serializable` entry in `core/router/Screen.kt` + `composable<Screen.X>` in `MainActivity`.
2. `AnalyticsScreen` entry + branch in `NavDestination.toAnalyticsScreen()`.
3. `XScreenState`, `XUIEvent` (and `XVMEvent` if needed) next to the screen.
4. `XViewModel` injecting use cases; public + private `XScreen` composables.
5. Use cases in `domain/usecases/`; new repository methods on the interface and the impl.
6. Strings in `values/`, `values-en/`, `values-ru/` of `:res`.
7. Tests for every use case with logic and every new mapper (see below).

---

## Testing

### Where each layer is tested

Each module with tests has a `TESTING.md` listing what its tests cover and the gotchas:
[`app`](../app/TESTING.md), [`core`](../core/TESTING.md), [`data`](../data/TESTING.md),
[`analytics`](../analytics/TESTING.md), [`feature/tasbih`](../feature/tasbih/TESTING.md).

| What                                          | Kind                                    | Source set            | Runs in CI           |
|-----------------------------------------------|-----------------------------------------|-----------------------|----------------------|
| Mappers, date helpers, screen-state totals    | JVM unit                                | `src/test`            | yes                  |
| Use cases                                     | JVM unit + fake repo                    | `src/test`            | yes                  |
| ViewModels                                    | JVM unit + fakes, `MainDispatcherRule`  | `src/test`            | yes                  |
| Network repositories                          | JVM unit, `FakeApi` + `inMemoryPreferences()` | `app/src/test`  | yes                  |
| ViewModels that read their route from `SavedStateHandle` | Instrumented, in-memory Room | `src/androidTest`     | no — run on a device |
| DAO and Room-backed repository                | Instrumented, in-memory Room            | `src/androidTest`     | no — run on a device |
| `DefaultLocaleInitializer`, `toAnalyticsScreen()` | Instrumented                        | `app/src/androidTest` | no — run on a device |

CI (`test-action.yml`) runs `./gradlew testDebugUnitTest` only. AGP creates unit tests for the
debug build type only, so `./gradlew test` runs the same set. Instrumented tests need a device or
emulator; `connectedDebugAndroidTest` installs the APKs and uninstalls them afterwards.

```bash
./gradlew testDebugUnitTest                                       # every JVM test
./gradlew :feature:tasbih:testDebugUnitTest --tests '*GetTasbihUseCaseTest'
./gradlew connectedDebugAndroidTest                               # every instrumented test, device required
```

### Tooling

**JUnit 4** plus **`kotlinx-coroutines-test`** — no mocking library, no Turbine, no Robolectric.
That shapes how tests are written:

- **Fakes, not mocks.** A fake implements the interface and answers with whatever the test
  assigns; anything left unassigned throws `error("unused")`, so an unexpected call fails loudly.
- **Shared fakes are test fixtures** of the module that owns the type, so every module uses the
  same one. Add them with `testImplementation(testFixtures(project(":analytics")))`:

  | Fake                     | Module       | What it does                                                          |
  |--------------------------|--------------|-----------------------------------------------------------------------|
  | `FakeAnalyticsTracker`   | `:analytics` | records `events`, `screens`, `userIds` and `userProperties`           |
  | `inMemoryPreferences()`  | `:data`      | a real `Preferences` over an in-memory `SharedPreferences`, no `Context` |
  | `MainDispatcherRule`     | `:core`      | swaps `Dispatchers.Main` for an unconfined test dispatcher            |

  Kotlin in test fixtures needs `android.experimental.enableTestFixturesKotlinSupport=true` in
  `gradle.properties`. A fake only one module needs stays in its `src/test/.../fakes/`
  (`FakeApi`, `FakeTasbihRepository`).
- **`runBlocking`** to run suspend code, and terminal operators to read the flow:
  `.last()` for the final emission of a one-shot flow, `.first { it !is Resource.InProgress }`
  for a long-lived Room flow (which never completes), `.toList()` to assert the whole sequence.
- **Names**: backtick sentences in JVM tests (`` `reports a failing name lookup as an error` ``).
  In `androidTest` use camelCase — D8 rejects method names with spaces below API 30, and
  `minSdk` is 24.

### Testing a use case

Pass-through use cases don't need tests. Test the ones that contain logic: combining sources,
validation, branching.

From `GetTasbihUseCaseTest`:

```kotlin
class GetTasbihUseCaseTest {

    private fun useCase(name: Resource<String>, count: Resource<Int>) = GetTasbihUseCase(
        FakeTasbihRepository().apply {
            tasbihName = flowOf(name)
            todayCount = flowOf(count)
        },
    )

    @Test
    fun `combines the name and today count into one tasbih`() = runBlocking {
        val result = useCase(Resource.Success("SubhanAllah"), Resource.Success(33)).invoke(7L).last()

        assertTrue(result is Resource.Success)
        assertEquals(Tasbih(7L, "SubhanAllah", 33), (result as Resource.Success).data)
    }

    @Test
    fun `reports a failing name lookup as an error`() = runBlocking {
        val error = IllegalStateException("boom")

        val result = useCase(Resource.Error(error = error), Resource.Success(33)).invoke(7L).last()

        assertTrue(result is Resource.Error)
        assertEquals(error, (result as Resource.Error).error)
    }

    @Test
    fun `stays in progress until both parts arrive`() = runBlocking {
        val result = useCase(Resource.Success("SubhanAllah"), Resource.InProgress()).invoke(7L).last()

        assertTrue(result is Resource.InProgress)
    }
}
```

Cover each branch: success, each source failing, and the in-progress state. When a test needs a
repository call `FakeTasbihRepository` doesn't answer yet, add a property to it rather than
writing another fake.

### Testing a ViewModel

`viewModelScope` runs on `Dispatchers.Main`, so add `MainDispatcherRule`. Its unconfined
dispatcher runs launched coroutines eagerly: the state is up to date as soon as the constructor or
`handleEvent` returns, no `advanceUntilIdle()` needed. Hand the fake a `MutableStateFlow` to step
the screen through `InProgress` → `Success` / `Error`. From `TasbihListViewModelTest`:

```kotlin
class TasbihListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val analytics = FakeAnalyticsTracker()
    private val tasbihs = MutableStateFlow<Resource<List<Tasbih>>>(Resource.InProgress())
    private val repository = FakeTasbihRepository().also { it.tasbihs = tasbihs }

    private fun viewModel() = TasbihListViewModel(
        analytics,
        ObserveTasbihsUseCase(repository),
        AddTasbihUseCase(repository),
    )

    @Test
    fun `shows an error when loading fails and clears it once the tasbihs arrive`() {
        val viewModel = viewModel()
        val error = IllegalStateException("boom")

        tasbihs.value = Resource.Error(error = error)
        assertEquals(error, viewModel.state.base.error)

        tasbihs.value = Resource.Success(emptyList())
        assertNull(viewModel.state.base.error)
    }
}
```

- The `:app` ViewModels take concrete repositories: build real ones from `FakeApi`,
  `inMemoryPreferences()` and `ErrorHandler()`. To see a loading state, `api.holdResponses()`
  keeps every call suspended until `api.releaseResponses()`.
- A `Channel`-backed `*VMEvent` flow is read with `runTest { viewModel.event.first() }`.
- **`SavedStateHandle.toRoute()` needs a real `Bundle`**, so a ViewModel that reads its route
  (`TasbihViewModel`, `TasbihEntryHistoryViewModel`) is tested in `androidTest` against an
  in-memory Room repository. There it runs on the real main thread while Room writes on its own
  executor, so wait with `awaitUntil({ viewModel.state }) { it.name.isNotEmpty() }` before
  asserting, and cancel `viewModelScope` in `@After` before closing the database.

### Testing a repository

A repository has two kinds of logic, tested differently.

**1. Mapping — JVM unit test.** Everything that turns entities/DTOs into models is a pure
function, so test it directly. From `TasbihMappersTest`:

```kotlin
/** 28 April 2026, so "2026-04-28" is today and "2026-04-27" is yesterday. */
private val now: Date = Calendar.getInstance().apply { set(2026, 3, 28, 12, 0, 0) }.time

@Test
fun `sums the day total across tasbihs`() {
    val days = listOf(
        record(1, "SubhanAllah", 99, "2026-04-27"),
        record(2, "Alhamdulillah", 33, "2026-04-27"),
    ).toDayHistory(now)

    assertEquals(1, days.size)
    assertEquals(132, days.first().total)
}
```

**2. Data access — instrumented test.** `TasbihRepositoryImpl` needs a real `TasbihDatabase`
(for `withTransaction`), which can't be built on the plain JVM, so these tests go in
`src/androidTest` and use an in-memory database. `TasbihDaoTest` covers the SQL itself (per-day
counts, ordering, the `count > 0` filters, cascade delete) the same way:

```kotlin
@RunWith(AndroidJUnit4::class)
class TasbihRepositoryImplTest {

    private lateinit var db: TasbihDatabase
    private lateinit var repository: TasbihRepository

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, TasbihDatabase::class.java).build()
        repository = TasbihRepositoryImpl(db.tasbihDao(), db, Preferences(context))
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun incrementAddsToTodayCount() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()

        repository.increment(id).last()
        repository.increment(id).last()

        assertEquals(2, repository.getTodayCount(id).successData())
    }

    @Test
    fun resetZeroesTodayCount() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()
        repository.increment(id).last()

        repository.reset(id).last()

        assertEquals(0, repository.getTodayCount(id).successData())
    }

    @Test
    fun observeTasbihsEmitsNewlyAddedTasbih() = runBlocking {
        val id = repository.addTasbih("SubhanAllah").successData()

        val tasbihs = repository.observeTasbihs().successData()

        assertEquals(listOf(id), tasbihs.map { it.id })
    }

    private suspend fun <T : Any> Flow<Resource<T>>.successData(): T =
        (first { it !is Resource.InProgress } as Resource.Success).data
}
```

- The in-memory database starts **empty**: the five default dhikr are seeded by the callback in
  `DatabaseModule`, which the test builder doesn't install.
- `Preferences(context)` writes to the real `prefs` file of the test app. A test that changes a
  preference (e.g. haptics) should set the value it expects up front rather than rely on the
  default.

**Network repositories run on the JVM.** `Api` is an interface and `inMemoryPreferences()`
stands in for `Preferences`, so build the real repository with `FakeApi` and the real
`ErrorHandler` (it's a plain class). From `RegionRepositoryTest`:

```kotlin
class RegionRepositoryTest {

    private val api = FakeApi()
    private val preferences = inMemoryPreferences()
    private val repository = RegionRepository(api, preferences, ErrorHandler())

    @Test
    fun `maps the regions`() = runBlocking {
        api.regions = Result.success(GetRegionsDTO(result = 0, msg = "", regions = listOf(RegionDTO(1, "Dushanbe"))))

        val rs = repository.getRegions().last()

        assertEquals(listOf(Region(1, "Dushanbe")), (rs as Resource.Success).data)
    }

    @Test
    fun `reports no connection when the host is unreachable`() = runBlocking {
        api.regions = Result.failure(UnknownHostException())

        val rs = repository.getRegions().last()

        assertTrue((rs as Resource.Error).error is ConnectionTimeoutException)
    }
}
```

What to cover for a network call: success with `result == 0`, a body with `result != 0`, an
`UnknownHostException` / `IOException` (→ `ConnectionTimeoutException`) and an `HttpException`
(→ `ApiException`).

Not reachable from tests yet:

- `UserRepository.updateMessagingId()` calls `FirebaseMessaging.getInstance()` directly, so it
  can't run until token fetching is moved behind an interface. `HomeViewModelTest` therefore
  starts from a registered install that already has a push token.
- Release-only branches such as `needRegister()` returning `true`: `BuildVars.BUILD_TYPE` is fixed
  at compile time and unit tests only exist for debug.
