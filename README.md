# Flutter AppCheck Plugin

[![Pub](https://img.shields.io/pub/v/appcheck.svg)](https://pub.dartlang.org/packages/appcheck)

A Flutter plugin that allows you to check if an app is installed/enabled, launch an app and get the list of installed apps.

This plugin is inspired by the plugin [Discontinued AppAvailability Plugin](https://pub.dev/packages/flutter_appavailability).

#### NOTE - Everything has been mostly rewritten in Kotlin and updated to latest null-safety standards.

## Installation

First, add `appcheck` as a [dependency in your pubspec.yaml file](https://flutter.io/using-packages/).

## Methods available

- `checkAvailability(String uri)`
- `getInstalledApps()` (only for **Android**)
- `isAppEnabled(String uri)` (only for **Android**)
- `launchApp(String uri)`

Check out [Example](https://github.com/Yash-Garg/appcheck/blob/develop/example/lib/main.dart).

## Screenshots

<p align="center">
  <img width="48.2%" src="https://raw.githubusercontent.com/Yash-Garg/AppCheck/develop/images/screen-android.png">
  <img width="47%" src="https://raw.githubusercontent.com/Yash-Garg/AppCheck/develop/images/screen-ios.png">
</p>
