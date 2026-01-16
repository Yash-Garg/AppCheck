import 'dart:io';
import 'package:appcheck/src/app_info.dart';
import 'package:flutter/services.dart';

/// Main class of the plugin.
class AppCheck {
  final MethodChannel _channel;

  AppCheck({MethodChannel? channel})
    : _channel = channel ?? const MethodChannel('dev.yashgarg/appcheck');

  /// Check if an app is available with the given [uri] scheme.
  ///
  /// Returns [AppInfo] containing info about the App or throws a [PlatformException]
  /// if the app isn't found.
  ///
  Future<AppInfo?> checkAvailability(String uri) async {
    final args = <String, dynamic>{'uri': uri};

    if (Platform.isAndroid) {
      Map<dynamic, dynamic> app = await _channel.invokeMethod(
        "checkAvailability",
        args,
      );

      return AppInfo.fromMap(app);
    } else if (Platform.isIOS) {
      bool appAvailable = await _channel.invokeMethod(
        "checkAvailability",
        args,
      );

      if (!appAvailable) {
        throw PlatformException(code: "", message: "App not found $uri");
      }

      return AppInfo(packageName: uri);
    }

    return null;
  }

  /// Check if an app is installed with the given [uri] scheme.
  ///
  /// Returns true if the app is installed and false if the app is not installed.
  Future<bool> isAppInstalled(String uri) async {
    try {
      await checkAvailability(uri);
      return true;
    } catch (_) {
      return false;
    }
  }

  /// Only for **Android**.
  ///
  /// Get the list of all installed apps, where
  /// each app has a form like [checkAvailability()].
  ///
  /// [includeIcon] - Whether to include app icons (default: true).
  /// Set to false for faster performance when icons aren't needed.
  ///
  /// [includeSystemApps] - Whether to include system apps (default: true).
  /// Set to false to only get user-installed apps.
  ///
  /// Returns a list of [AppInfo] containing all installed apps data, else returns [null]
  Future<List<AppInfo>?> getInstalledApps({
    bool includeIcon = true,
    bool includeSystemApps = true,
  }) async {
    final args = <String, dynamic>{
      'includeIcon': includeIcon,
      'includeSystemApps': includeSystemApps,
    };
    List<dynamic>? apps = await _channel.invokeMethod("getInstalledApps", args);
    if (apps != null) {
      List<AppInfo> list = [];
      for (var app in apps) {
        if (app is Map) {
          list.add(AppInfo.fromMap(app));
        }
      }

      return list;
    }
    return null;
  }

  /// Only for **Android**.
  ///
  /// Check if the app is enabled or not with the given [uri] scheme.
  ///
  /// If the app isn't found, then a [PlatformException] is thrown.
  Future<bool> isAppEnabled(String uri) async {
    Map<String, dynamic> args = <String, dynamic>{'uri': uri};
    return await _channel.invokeMethod("isAppEnabled", args);
  }

  /// Launch an app with the given [uri] scheme if it exists.
  ///
  /// If the app isn't found, then a [PlatformException] is thrown.
  Future<void> launchApp(String uri) async {
    Map<String, dynamic> args = <String, dynamic>{'uri': uri};
    if (Platform.isAndroid) {
      await _channel.invokeMethod("launchApp", args);
    } else if (Platform.isIOS) {
      bool appAvailable = await _channel.invokeMethod("launchApp", args);
      if (!appAvailable) {
        throw PlatformException(code: "", message: "App not found $uri");
      }
    }
  }
}
