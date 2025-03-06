import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:appcheck/appcheck.dart';

void main() {
  runApp(const AppCheckExample());
}

class AppCheckExample extends StatefulWidget {
  const AppCheckExample({super.key});

  @override
  State<AppCheckExample> createState() => _AppCheckExampleState();
}

class _AppCheckExampleState extends State<AppCheckExample> {
  final appCheck = AppCheck();
  List<AppInfo> installedApps = [];

  final List<AppInfo> iOSApps = [
    AppInfo(appName: "Calendar", packageName: "calshow://"),
    AppInfo(appName: "Facebook", packageName: "fb://"),
    AppInfo(appName: "WhatsApp", packageName: "whatsapp://"),
  ];

  @override
  void initState() {
    super.initState();
    getApps();
  }

  Future<void> getApps() async {
    List<AppInfo>? apps = [];
    try {
      if (Platform.isAndroid) {
        apps = await appCheck.getInstalledApps();
        apps?.sort(
          (a, b) =>
              a.appName!.toLowerCase().compareTo(b.appName!.toLowerCase()),
        );

        const package = "com.google.android.apps.maps";
        final isAvailable = await appCheck.checkAvailability(package);
        debugPrint("$package available: $isAvailable");

        final isEnabled = await appCheck.isAppEnabled(package);
        debugPrint("$package is ${isEnabled ? 'enabled' : 'disabled'}");
      } else if (Platform.isIOS) {
        apps = iOSApps;

        final isAvailable = await appCheck.checkAvailability("calshow://");
        debugPrint("Calendar available: $isAvailable");
      }
    } catch (e) {
      debugPrint("Error fetching apps: $e");
    }

    if (mounted) {
      setState(() {
        installedApps = apps ?? [];
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(title: const Text('AppCheck Example')),
        body:
            installedApps.isNotEmpty
                ? ListView.builder(
                  itemCount: installedApps.length,
                  itemBuilder: (context, index) {
                    final app = installedApps[index];
                    return ListTile(
                      title: Text(app.appName ?? app.packageName),
                      subtitle: Text(
                        (app.isSystemApp ?? false) ? 'System App' : 'User App',
                      ),
                      trailing: IconButton(
                        icon: const Icon(Icons.open_in_new),
                        onPressed: () => _launchApp(app),
                      ),
                    );
                  },
                )
                : const Center(child: Text('No installed apps found!')),
      ),
    );
  }

  Future<void> _launchApp(AppInfo app) async {
    try {
      await appCheck.launchApp(app.packageName);
      debugPrint("${app.appName ?? app.packageName} launched!");
    } catch (e) {
      if (!mounted) return; // Ensure the widget is still in the tree

      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text("${app.appName ?? app.packageName} not found!")),
      );
      debugPrint("Error launching app: $e");
    }
  }
}
