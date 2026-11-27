# Testing `:analytics`

How to test, in general: [`docs/architecture.md#testing`](../docs/architecture.md#testing).

```bash
./gradlew :analytics:testDebugUnitTest
```

## What is tested

JVM only (`src/test`), in `AnalyticsEventTest`. It checks the whole catalogue, not one event at a time:

- every event name is snake_case, at most 40 characters, and never starts with `firebase_`, `google_` or `ga_`
- event names are unique
- every parameter key is snake_case and at most 40 characters
- a `null` or blank error is reported as `"unknown"`
- `TasbihAdded` sends only the name length, never the name
- `AnalyticsScreen` names are unique snake_case, and `AnalyticsProperty` names fit the 24-character limit

The test finds the events by reflection over the classes nested in `AnalyticsEvent`. It takes a
`data object` from its `INSTANCE` field and builds a `data class` with sample values. **A new event
is checked without touching the test.** Only an event whose constructor takes a type other than
`Boolean`, `Int`, `Long` or `String` needs a sample added to `sampleOf()`. Until then the test
fails with "Add a sample value for …".

## Test fixture: `FakeAnalyticsTracker`

`src/testFixtures` ships a recording `AnalyticsTracker` for the other modules' tests:

| Property         | Records                                                                  |
|------------------|--------------------------------------------------------------------------|
| `events`         | every `log(event)`, in order                                             |
| `screens`        | every `logScreenView(screen)`                                            |
| `userIds`        | every `setUserId(id)`: a list, so `setUserId(null)` differs from no call |
| `userProperties` | the latest value per property name; a `null` value is still a key       |

```kotlin
testImplementation(testFixtures(project(":analytics")))
androidTestImplementation(testFixtures(project(":analytics")))
```

`explicitApi()` covers the fixture too, so declarations there need `public`.

## Not tested

- `FirebaseAnalyticsTracker` talks to the Firebase SDK and builds a `Bundle`, so it can't run on the
  JVM. It holds no logic beyond the value-type mapping in `putParam`.

There are no instrumented tests. `build.gradle.kts` turns them off (`androidTest.enable = false`):
otherwise the test fixture gives the module a test APK that crashes on a device with nothing to run.
