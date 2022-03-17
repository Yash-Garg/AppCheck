import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

/// App info model class for returning data
class AppInfo {
  final String? appName;
  final String packageName;
  final String? versionCode;
  final String? versionName;

  AppInfo({
    this.appName,
    required this.packageName,
    this.versionCode,
    this.versionName,
  });

  @override
  String toString() {
    if (Platform.isAndroid) {
      return 'App - $appName, Version - $versionCode';
    } else if (Platform.isIOS) {
      return 'App - $packageName';
    } else {
      throw UnimplementedError();
    }
  }
}

/// Main class of the plugin.
class AppCheck {
  static const MethodChannel _channel = MethodChannel('dev.yashgarg/appcheck');

  /// Check if an app is available with the given [uri] scheme.
  ///
  /// Returns [AppInfo] containing info about the App or throws a [PlatformException]
  /// if the app isn't found.
  ///
  static Future<AppInfo?> checkAvailability(String uri) async {
    final args = <String, dynamic>{};
    args.putIfAbsent('uri', () => uri);

    if (Platform.isAndroid) {
      Map<dynamic, dynamic> app = await _channel.invokeMethod(
        "checkAvailability",
        args,
      );

      return AppInfo(
        appName: app["app_name"],
        packageName: app["package_name"],
        versionCode: app["versionCode"],
        versionName: app["version_name"],
      );
    } else if (Platform.isIOS) {
      bool appAvailable =
          await _channel.invokeMethod("checkAvailability", args);
      if (!appAvailable) {
        throw PlatformException(code: "", message: "App not found $uri");
      }
      return AppInfo(packageName: uri);
    }

    return null;
  }

  /// Only for **Android**.
  ///
  /// Get the list of all installed apps, where
  /// each app has a form like [checkAvailability()].
  ///
  /// Returns a list of [AppInfo] containing all installed apps data, else returns [null]
  static Future<List<AppInfo>?> getInstalledApps() async {
    List<dynamic>? apps = await _channel.invokeMethod("getInstalledApps");
    if (apps != null) {
      List<AppInfo> list = [];
      for (var app in apps) {
        if (app is Map) {
          list.add(AppInfo(
            appName: app["app_name"],
            packageName: app["package_name"],
            versionCode: app["versionCode"],
            versionName: app["version_name"],
          ));
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
  static Future<bool> isAppEnabled(String uri) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent('uri', () => uri);
    return await _channel.invokeMethod("isAppEnabled", args);
  }

  /// Launch an app with the given [uri] scheme if it exists.
  ///
  /// If the app app isn't found, then a [PlatformException] is thrown.
  static Future<void> launchApp(String uri) async {
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent('uri', () => uri);
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
