# Testing `:feature:tasbih`

How to test, in general: [`docs/architecture.md#testing`](../../docs/architecture.md#testing).
This module has every layer, so its tests are the reference for a new feature.

```bash
./gradlew :feature:tasbih:testDebugUnitTest              # JVM, runs in CI
./gradlew :feature:tasbih:testDebugUnitTest --tests '*TasbihListViewModelTest'
./gradlew :feature:tasbih:connectedDebugAndroidTest      # device or emulator required
```

## JVM tests (`src/test`)

| Test                                | Covers                                                                    |
|-------------------------------------|---------------------------------------------------------------------------|
| `TasbihDatesTest`                   | today's ISO key; today / yesterday across month and year boundaries       |
| `TasbihMappersTest`                 | history grouped by day, newest first; day totals; entries by count; Today / Yesterday / date labels |
| `GetTasbihUseCaseTest`              | name and today's count combined; either failing; in progress until both arrive |
| `TasbihListViewModelTest`           | loading, list, error cleared on reload, add dialog, add reports only the name length, add failure |
| `TasbihHistoryViewModelTest`        | opened event, loading, days, error                                        |
| `TasbihHistoryScreenStateTest`      | total, active days, distinct tasbihs                                      |
| `TasbihEntryHistoryScreenStateTest` | total, active days, best day                                              |

`GetTasbihUseCase` is the only use case with logic. The others pass straight through to the
repository and aren't tested on purpose.

### `FakeTasbihRepository`

`fakes/FakeTasbihRepository` answers with whatever flow a test assigns (`tasbihs`, `history`,
`entryHistory`, `addResult`, `todayCount`, `tasbihName`, `hapticEnabled`). Anything left unassigned
fails with "unused". `addTasbih` records names in `addedNames`.

To walk a screen through its states, hand it a `MutableStateFlow`:

```kotlin
private val tasbihs = MutableStateFlow<Resource<List<Tasbih>>>(Resource.InProgress())
private val repository = FakeTasbihRepository().also { it.tasbihs = tasbihs }

tasbihs.value = Resource.Error(error = error)      // the state updates right away under MainDispatcherRule
```

When a new test needs a call the fake doesn't answer yet, add a property here rather than writing
another fake. `increment` and `reset` are always "unused": the ViewModel that calls them is tested
on a device.

## Instrumented tests (`src/androidTest`)

| Test                              | Covers                                                                   |
|-----------------------------------|--------------------------------------------------------------------------|
| `TasbihDaoTest`                   | per-day counts, newest tasbih first, `count > 0` filters, history ordering, upsert, cascade delete |
| `TasbihRepositoryImplTest`        | add, name, today's count, increment, reset, unknown tasbih → `Error`, history labelled Today, haptics |
| `TasbihViewModelTest`             | loads name and count; every tap counted and saved; milestone every 33; reset only after confirming; haptics saved |
| `TasbihEntryHistoryViewModelTest` | days counted, newest first, zero days hidden; totals; live update after a new tap |

They run on a device for two reasons:

- `TasbihRepositoryImpl` needs a real `TasbihDatabase` for `withTransaction`. Every test builds an
  in-memory one.
- `TasbihViewModel` and `TasbihEntryHistoryViewModel` read their id with
  `SavedStateHandle.toRoute()`, which needs a real `Bundle`. They are tested against the real Room
  repository rather than a fake.

`testing/TestHelpers.kt` has the two helpers:

- `successData()`: the first non-`InProgress` value of a `Flow<Resource<T>>`, cast to `Success`
- `awaitUntil(read) { condition }`: polls for up to 5 s

### Things to know

- **The in-memory database starts empty.** The five default dhikr come from the callback in
  `DatabaseModule`, which tests don't install.
- **No `MainDispatcherRule` on a device.** The ViewModels run on the real main thread while Room
  writes on its own threads. Wait for the state with
  `awaitUntil({ viewModel.state }) { it.name.isNotEmpty() }` before sending events, and for a write
  with the test's `awaitSavedCount(n)`.
- **Wait for taps to be saved before resetting.** Each tap starts its own increment. A reset sent
  before they finish can run first, and a later increment then brings the count back above zero.
- **Teardown order:** cancel `viewModel.viewModelScope`, then `db.close()`.
- **`Preferences(context)` is the test APK's real `prefs` file.** `TasbihViewModelTest` sets
  haptics off in `setUp()` instead of relying on the default.
- **Names are camelCase**, not backtick sentences: D8 rejects spaces in method names below API 30.

## Not tested

- Composables: the four screens, the bottom sheets and `HistoryComponents`.
- The seed callback in `DatabaseModule`.
