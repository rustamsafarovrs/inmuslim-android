# Testing `:core`

How to test, in general: [`docs/architecture.md#testing`](../docs/architecture.md#testing).

```bash
./gradlew :core:testDebugUnitTest
```

## What is tested

JVM only (`src/test`):

| Test                  | Covers                                                                                    |
|-----------------------|-------------------------------------------------------------------------------------------|
| `DateUtilsTest`       | formatting a `Date`, a timestamp and an ISO string; `applyTimeZone` parsing as UTC; `null` for missing input; `formatDuration` |
| `NumberFormatterTest` | thousands grouping follows the default locale (`1,234,567` / `1.234.567`)                 |

Both depend on the JVM defaults, so they set them in `@Before` and restore them in `@After`.
`DateUtilsTest` uses `Locale.US` and `Asia/Dushanbe` (UTC+5, no daylight saving). Do the same in
any new test that formats dates or numbers. Otherwise the result depends on the machine running it.

## Test fixture: `MainDispatcherRule`

`src/testFixtures` ships a JUnit rule for ViewModel tests in other modules. It swaps
`Dispatchers.Main`, which `viewModelScope` uses, for an `UnconfinedTestDispatcher`, so a coroutine
launched by the ViewModel runs right away. It brings `junit` and `kotlinx-coroutines-test` with it
(`testFixturesApi`).

```kotlin
testImplementation(testFixtures(project(":core")))

@get:Rule
val mainDispatcherRule = MainDispatcherRule()
```

The module applies the Compose compiler plugin, which also runs on the test fixtures. That is why
`build.gradle.kts` puts the Compose runtime (`compose.ui`) on their classpath.

## Not tested

- **Unparseable input to `DateUtils`.** The `catch` branch calls `android.util.Log.e`, which throws
  "Method … not mocked" on the JVM. That branch can only be tested on a device, or after enabling
  `unitTests.isReturnDefaultValues`.
- `Router` needs a `NavHostController`. `TextRes` resolution needs a `Context`. `theme/` is
  Compose UI.
- `Resource`, `BaseState`, `TitleValue` and `Screen` are plain data with no logic.

There are no instrumented tests. The `androidTest` setup in `build.gradle.kts` is still there for
when they're needed.
