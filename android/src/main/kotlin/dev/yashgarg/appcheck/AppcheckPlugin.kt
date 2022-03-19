package dev.yashgarg.appcheck

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.*
import kotlin.collections.*

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
        val uriSchema: String
        when (call.method) {
            "checkAvailability" -> {
                uriSchema = call.argument<String>("uri").toString()
                checkAvailability(uriSchema, result)
            }
            "getInstalledApps" -> result.success(installedApps)
            "isAppEnabled" -> {
                uriSchema = call.argument<String>("uri").toString()
                isAppEnabled(uriSchema, result)
            }
            "launchApp" -> {
                uriSchema = call.argument<String>("uri").toString()
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
        result.error("400", "App not found $uri", null)
    }

    private val installedApps: MutableList<Map<String, Any>>
        get() {
            val packageManager: PackageManager = context.packageManager
            val packages = packageManager.getInstalledPackages(0)
            val installedApps: MutableList<Map<String, Any>> = ArrayList(packages.size)
            for (pkg in packages) {
                val map = convertPackageInfoToJson(pkg)
                installedApps.add(map)
            }
            return installedApps
        }

    private fun getAppPackageInfo(uri: String): PackageInfo? {
        val pm = context.packageManager
        try {
            return pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        } catch (e: NameNotFoundException) {
            e.message?.let { Log.e("getAppPackageInfo ($uri)", it) }
        }
        return null
    }

    private fun convertPackageInfoToJson(info: PackageInfo): Map<String, Any> {
        val app: MutableMap<String, Any> = HashMap()

        app["app_name"] =
            info.applicationInfo.loadLabel(context.packageManager).toString()
        app["package_name"] = info.packageName
        app["version_name"] = info.versionName
        app["system_app"] = (info.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        return app
    }

    private fun isAppEnabled(packageName: String, result: Result) {
        val appStatus: Boolean
        try {
            val appInfo: ApplicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
            appStatus = appInfo.enabled
        } catch (e: NameNotFoundException) {
            result.error("400", "${e.message} $packageName", e)
            return
        }
        result.success(appStatus)
    }

    private fun launchApp(packageName: String, result: Result) {
        val info = getAppPackageInfo(packageName)
        if (info != null) {
            val launchIntent: Intent? =
                context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                result.success(null)
                return
            }
        }
        result.error("400", "App not found $packageName", null)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
