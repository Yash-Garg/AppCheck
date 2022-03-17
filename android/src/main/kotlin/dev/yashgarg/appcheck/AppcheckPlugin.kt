package dev.yashgarg.appcheck

import android.content.Context
import kotlin.collections.*
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.content.pm.ApplicationInfo
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
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "dev.yashgarg/appcheck")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
//            "checkAvailability" -> {
//                uriSchema = call.argument("uri").toString()
//                checkAvailability(uriSchema, result)
//            }
            "getInstalledApps" -> result.success(installedApps)
//            "isAppEnabled" -> {
//                uriSchema = call.argument("uri").toString()
//                isAppEnabled(uriSchema, result)
//            }
//            "launchApp" -> {
//                uriSchema = call.argument("uri").toString()
//                launchApp(uriSchema, result)
//            }
            else -> result.notImplemented()
        }
    }

    private val installedApps: MutableList<Map<String, Any>>
        get() {
            val packageManager: PackageManager = context.packageManager
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

    private fun convertPackageInfoToJson(info: PackageInfo): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["app_name"] =
            info.applicationInfo.loadLabel(context.packageManager).toString()
        map["package_name"] = info.packageName
        map["version_code"] = info.versionCode.toString()
        map["version_name"] = info.versionName
        return map
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
