# Inmuslim

This repo contains the official source code for [Inmuslim App for Android](https://play.google.com/store/apps/details?id=tj.rsdevteam.inmuslim).

### Install

If you're just looking to install Inmuslim Android, you can find it on [Google Play](https://play.google.com/store/apps/details?id=tj.rsdevteam.inmuslim).

![PlayBadge](https://PlayBadges.pavi2410.me/badge/full?id=tj.rsdevteam.inmuslim)

### Compilation Guide

You will require Android Studio Giraffe (2022.3.1) and Android SDK 33

1. Download the Telegram source code from https://github.com/rustamsafarovrs/InmuslimAndroid ( git clone https://github.com/rustamsafarovrs/InmuslimAndroid.git ).
2. Copy your release.keystore into app/config.
3. Fill out RELEASE_KEY_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_STORE_PASSWORD in local.properties to access your release.keystore.
4. Open the project in the Studio (note that it should be opened, NOT imported).
5. You are ready to compile project.