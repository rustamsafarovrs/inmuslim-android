# Testing `:data`

How to test, in general: [`docs/architecture.md#testing`](../docs/architecture.md#testing).

```bash
./gradlew :data:testDebugUnitTest
```

## What is tested

JVM only (`src/test`), in `PreferencesTest`:

- a new install is empty: both ids are `-1`, the token is `""`, and every flag is `false`
  (haptics included)
- `isEmpty` turns false after the first write, which `DefaultLocaleInitializer` relies on
- every value reads back as saved, and haptics can be turned back off

## Test fixture: `inMemoryPreferences()`

`Preferences` takes a `Context`, which can't be built on the JVM. `src/testFixtures` ships
`inMemoryPreferences()`, which returns a **real** `Preferences` over a map-backed
`SharedPreferences` (`InMemorySharedPreferences`). Any class that takes `Preferences` (the network
repositories, `TasbihRepositoryImpl`, the `:app` ViewModels) can then be tested in `src/test`.

```kotlin
testImplementation(testFixtures(project(":data")))

private val preferences = inMemoryPreferences().apply { saveRegionId(3) }
private val repository = RegionRepository(FakeApi(), preferences, ErrorHandler())
```

- Each call returns **new, empty storage**. Pass one instance to every repository that must see the
  same values.
- `Preferences` creates one `Editor` and keeps it for its whole life. So the fake's `Editor` writes
  its pending changes on every `apply()`/`commit()` and then clears them.
- JVM only. On a device, use `Preferences(context)`. It writes to the test APK's real `prefs`
  file, so set any value a test depends on first instead of assuming a default.

## Not tested

- `DataModule` is Hilt wiring.

There are no instrumented tests. `build.gradle.kts` turns them off (`androidTest.enable = false`):
otherwise the test fixture gives the module a test APK that crashes on a device with nothing to run.
