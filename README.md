# Inmuslim

This repo contains the official source code for [Inmuslim App for Android](https://play.google.com/store/apps/details?id=tj.rsdevteam.inmuslim).

![PlayBadge](https://PlayBadges.pavi2410.me/badge/full?id=tj.rsdevteam.inmuslim)

## Install

If you're just looking to install Inmuslim, you can find it on [Google Play](https://play.google.com/store/apps/details?id=tj.rsdevteam.inmuslim).

## Compilation Guide

Follow these steps to set up and compile the project on your local machine:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rustamsafarovrs/inmuslim-android.git
   cd inmuslim-android
   ```
2. **Install git hooks:**
   To ensure code quality, install the local pre-commit hooks that run `detekt` and `lint` before each commit:
   ```bash
   ./gradlew installGitHooks
   ```
3. **Configure signing:**
   - Copy your `release.keystore` into the `config/` directory.
   - Open `local.properties` (create it if it doesn't exist) and add your keystore credentials:
     ```properties
     RELEASE_KEY_PASSWORD=your_key_password
     RELEASE_KEY_ALIAS=your_key_alias
     RELEASE_STORE_PASSWORD=your_store_password
     ```
4. **Configure Firebase (Optional):**
   The repository already includes `app/google-services.json`. If you want to use your own Firebase project, replace that file with your own. Debug builds use the same file — they apply the `.beta` application ID suffix, so register `tj.rsdevteam.inmuslim.beta` in your Firebase project as well.
5. **Open in Android Studio:**
   - Use the **latest stable Android Studio** — the project builds with AGP 9.3.1 and Kotlin 2.3.21, which older releases cannot open.
   - Open the project from the root directory (choose **Open**, do NOT use **Import**).
6. **Build and Run:**
   - Wait for Gradle to sync.
   - Select the `app` configuration and run it on an emulator or a physical device.

## Technologies

### Core

- Kotlin
- Jetpack Compose (UI & navigation)
- MVVM architecture with Flow
- Hilt (DI)
- Kotlin Coroutines

### Networking

- Retrofit (REST API)
- OkHttp 3 (REST client)
- Moshi (JSON serialization)

### Local Persistence

- Room (tasbih dhikr counters and history)
- Shared Preferences (selected region, user id, FCM token, settings)

### Other

- Firebase Crashlytics (crashes, logging)
- Firebase Analytics (events, via the `:analytics` module)
- Firebase Cloud Messaging (push notifications)
- Play In-App Review and In-App Update
- detekt + Android Lint (static analysis)

### CI/CD

GitHub Actions runs on every push and pull request:

| Workflow                                    | Job      | Command                       |
|---------------------------------------------|----------|-------------------------------|
| `.github/workflows/static-analysis.yml`     | `detekt` | `./gradlew detekt`            |
| `.github/workflows/static-analysis.yml`     | `lint`   | `./gradlew lint`              |
| `.github/workflows/test-action.yml`         | `test`   | `./gradlew testDebugUnitTest` |

All three jobs run on JDK 21 and upload their reports as build artifacts. The `installGitHooks`
pre-commit hook runs `detekt` and `lint` locally so failures surface before they reach CI.

## Project Requirements

- JDK 21
- Android SDK 37 (min SDK 24)
- Latest stable Android Studio (for easy download - [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/))

## Contributing

Read our [Contributing Guide](CONTRIBUTING.md) to learn about reporting issues, contributing code, and more ways to contribute.

## Documentation

- [Pull Request Guidelines](docs/pull-request-guidelines.md) - branch naming and how to write good pull requests.
