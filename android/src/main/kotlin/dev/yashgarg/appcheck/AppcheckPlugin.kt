package dev.yashgarg.appcheck

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.annotation.TargetApi;
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** AppcheckPlugin */
class AppcheckPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.yashgarg/appcheck")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        val uriSchema: String
        when (call.method) {
            "checkAvailability" -> {
                uriSchema = call.argument("uri").toString()
                checkAvailability(uriSchema, result)
            }
            "getInstalledApps" -> result.success(installedApps)
            "isAppEnabled" -> {
                uriSchema = call.argument("uri").toString()
                isAppEnabled(uriSchema, result)
            }
            "launchApp" -> {
                uriSchema = call.argument("uri").toString()
                launchApp(uriSchema, result)
            }
            else -> result.notImplemented()
        }
    }

    private fun checkAvailability(uri: String, result: Result) {
        val info = getAppPackageInfo(uri)
        if (info != null) {
            result.success(convertPackageInfoToJson(info))
            return
        }
        result.error("", "App not found $uri", null)
    }

    private val installedApps: List<Map<String, Any>>
        private get() {
            val packageManager: PackageManager = registrar.context().getPackageManager()
            val apps = packageManager.getInstalledPackages(0)
            val installedApps: MutableList<Map<String, Any>> = ArrayList(apps.size)
            val systemAppMask =
                ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
            for (pInfo in apps) {
                if (pInfo.applicationInfo.flags and systemAppMask != 0) {
                    continue
                }
                val map = convertPackageInfoToJson(pInfo)
                installedApps.add(map)
            }
            return installedApps
        }

    private fun getAppPackageInfo(uri: String): PackageInfo? {
        val ctx = activity.applicationContext
        val pm = ctx.packageManager
        try {
            return pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return null
    }

    private fun convertPackageInfoToJson(info: PackageInfo): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["app_name"] =
            info.applicationInfo.loadLabel(registrar.context().getPackageManager()).toString()
        map["package_name"] = info.packageName
        map["version_code"] = info.versionCode.toString()
        map["version_name"] = info.versionName
        return map
    }

    private fun isAppEnabled(packageName: String, result: Result) {
        var appStatus = false
        try {
            val ai: ApplicationInfo =
                registrar.context().getPackageManager().getApplicationInfo(packageName, 0)
            if (ai != null) {
                appStatus = ai.enabled
            }
        } catch (e: PackageManager.NameNotFoundException) {
            result.error("", e.message + " " + packageName, e)
            return
        }
        result.success(appStatus)
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    private fun launchApp(packageName: String, result: Result) {
        val info = getAppPackageInfo(packageName)
        if (info != null) {
            val launchIntent: Intent =
                registrar.context().getPackageManager().getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                registrar.context().startActivity(launchIntent)
                result.success(null)
                return
            }
        }
        result.error("", "App not found $packageName", null)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
