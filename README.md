# Inmuslim

This repo contains the official source code for [Inmuslim App for Android](https://play.google.com/store/apps/details?id=tj.rsdevteam.inmuslim).

![PlayBadge](https://PlayBadges.pavi2410.me/badge/full?id=tj.rsdevteam.inmuslim)

## Install

If you're just looking to install Inmuslim, you can find it on [Google Play](https://play.google.com/store/apps/details?id=tj.rsdevteam.inmuslim).

## Compilation Guide

Follow these steps to set up and compile the project on your local machine:

1. **Clone the repository:**
   ```bash
   git clone https://github.com/rustamsafarovrs/InmuslimAndroid.git
   cd InmuslimAndroid
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
   The repository already includes `google-services.json`. If you want to use your own Firebase project, replace `app/google-services.json` and `app/src/debug/google-services.json` with your own files.
5. **Open in Android Studio:**
   - Use **Android Studio Giraffe (2022.3.1)** or newer.
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

- Shared Preferences

### Other

- Firebase Crashlytics (crashes, logging)

### CI/CD

_Soon..._

## Project Requirements

- Java 17
- Android Studio Giraffe (2022.3.1) (for easy download - [JetBrains Toolbox](https://www.jetbrains.com/toolbox-app/))

## Contributing

Read our [Contributing Guide](CONTRIBUTING.md) to learn about reporting issues, contributing code, and more ways to contribute.

## Documentation

- [Pull Request Guidelines](docs/pull-request-guidelines.md) - branch naming and how to write good pull requests.
