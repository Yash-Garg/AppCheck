## 1.8.0

### Breaking Changes

- Minimum Flutter version is now 3.44.0.
- Minimum Dart SDK version is now 3.12.0.
- Minimum iOS deployment target bumped from 9.0 to 12.0.

### Maintenance

- Migrated Android plugin to Flutter's built-in Kotlin (AGP 9 / KGP 2.0+). Removed explicit `kotlin-android` plugin, `kotlin-stdlib` dependency, and `kotlinOptions` block; replaced with the `kotlin { compilerOptions {} }` DSL.
- Updated iOS podspec version to match pubspec and bumped Swift version to 5.9.

## 1.7.0

### Performance
- **Fixed UI blocking**: `getInstalledApps()` no longer freezes the UI. Method channel handlers now run on a background thread using Flutter's `makeBackgroundTaskQueue()`.

### New Features
- Added `includeIcon` parameter to `getInstalledApps()` - set to `false` for significantly faster performance when icons aren't needed.
- Added `includeSystemApps` parameter to `getInstalledApps()` - set to `false` to only retrieve user-installed apps.

### Bug Fixes
- Fixed `versionCode` type mismatch that could cause issues on Android API 28+.

### Breaking Changes
- Minimum Dart SDK version is now 3.0.0
- Minimum Flutter version is now 3.10.0

### 1.6.0

- Updated Android dependencies
- Increased Android minSdk version to 24

### 1.5.4

- Support for Swift Package Manager on iOS.
- Misc. updates to `example` app.

## 1.5.3
- Added null check for `appInfo`.

## 1.5.2

- You need to now make an instance of the class to use the methods, instead of using static methods. Checkout the example in the README for more info.

## 1.3.0 - 1.5.0

- Minor code cleanup and dependency updates.
- Fixes for iOS app launches and README instructions.

## 1.0.6

- Lowered dart sdk constraint to >=2.12.0 from 2.17.0

## 1.0.5

- Remove QUERY_ALL_PACKAGES permission due to playstore policies (check README for more info)

## 1.0.4

- Fixed some lint warnings
- Updated Dart SDK min version to 2.17

## 1.0.3

- Fixed system app logic for Android
- Fixed opening URLs on earlier iOS versions

## 1.0.0 - 1.0.2

- Rewritten in Kotlin and with Null-Safety.
