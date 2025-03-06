import 'dart:io';

import 'package:flutter/services.dart';

///
/// App info model class for returning data
///
/// [packageName] is the package name of the app (ios: bundle id, android: package name)
///
/// [appName] is the name of the app (if available)
///
/// [versionName] is the version name of the app (if available)
///
/// [isSystemApp] is a boolean value indicating if the app is a system app (if available)
class AppInfo {
  final String packageName;
  final String? appName;
  final String? versionName;
  final bool? isSystemApp;
  final int? versionCode;
  final Uint8List? icon;

  AppInfo({
    required this.packageName,
    this.appName,
    this.versionName,
    this.isSystemApp,
    this.versionCode,
    this.icon,
  });

  static AppInfo fromMap(Map<dynamic, dynamic> map) {
    return AppInfo(
      appName: map["app_name"],
      packageName: map["package_name"],
      versionName: map["version_name"],
      isSystemApp: map["system_app"],
      versionCode: map["version_code"],
      icon: map["icon"],
    );
  }

  @override
  String toString() {
    if (Platform.isAndroid) {
      return 'App - $appName, Package - $packageName, Version - $versionName, System App - $isSystemApp, Icon - $icon, VersionCode - $versionCode';
    } else if (Platform.isIOS) {
      return 'App - $packageName';
    } else {
      throw PlatformException(code: "404", message: "Platform not supported");
    }
  }
}
