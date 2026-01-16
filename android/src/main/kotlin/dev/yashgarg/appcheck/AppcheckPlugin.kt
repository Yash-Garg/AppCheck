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
import io.flutter.plugin.common.StandardMethodCodec
import kotlin.collections.*
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.P
import android.os.Build.VERSION_CODES.TIRAMISU

/** AppcheckPlugin */
class AppcheckPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // Use background task queue to prevent blocking the UI thread
        val taskQueue = flutterPluginBinding.binaryMessenger.makeBackgroundTaskQueue()
        channel = MethodChannel(
            flutterPluginBinding.binaryMessenger,
            "dev.yashgarg/appcheck",
            StandardMethodCodec.INSTANCE,
            taskQueue
        )
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
            "getInstalledApps" -> {
                val includeIcon = call.argument<Boolean>("includeIcon") ?: true
                val includeSystemApps = call.argument<Boolean>("includeSystemApps") ?: true
                result.success(getInstalledApps(includeIcon, includeSystemApps))
            }
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

    private fun getInstalledApps(includeIcon: Boolean, includeSystemApps: Boolean): MutableList<Map<String, Any>> {
        val packageManager: PackageManager = context.packageManager
        val packages = if (SDK_INT >= TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }
        val installedApps: MutableList<Map<String, Any>> = ArrayList(packages.size)
        for (pkg in packages) {
            val appInfo = pkg.applicationInfo
            val isSystemApp = appInfo != null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            if (!includeSystemApps && isSystemApp) continue
            
            val map = convertPackageInfoToJson(pkg, includeIcon)
            installedApps.add(map)
        }
        return installedApps
    }
    
    private fun getAppPackageInfo(uri: String): PackageInfo? {
        val pm = context.packageManager
        try {
            return if (SDK_INT >= TIRAMISU) {
                pm.getPackageInfo(uri, PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            }
        } catch (e: NameNotFoundException) {
            e.message?.let { Log.e("getAppPackageInfo ($uri)", it) }
        }
        return null
    }

    private fun convertPackageInfoToJson(info: PackageInfo, includeIcon: Boolean = true): Map<String, Any> {
        val app: MutableMap<String, Any> = HashMap()

        val appInfo = info.applicationInfo
        if (appInfo != null) {
            app["app_name"] = appInfo.loadLabel(context.packageManager).toString()
            if (includeIcon) {
                app["icon"] = DrawableUtil.drawableToByteArray(appInfo.loadIcon(context.packageManager))
            }
            app["system_app"] = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } else {
            app["app_name"] = "N/A"
            app["system_app"] = false
        }
    
        app["package_name"] = info.packageName
        app["version_name"] = info.versionName.toString()
        app["version_code"] = getVersionCode(info).toInt()
        
        return app
    }

    @Suppress("DEPRECATION")
    private fun getVersionCode(packageInfo: PackageInfo): Long {
        return if (SDK_INT < P) packageInfo.versionCode.toLong()
        else packageInfo.longVersionCode
    }

    private fun isAppEnabled(packageName: String, result: Result) {
        val appStatus: Boolean
        try {
            val appInfo: ApplicationInfo = if (SDK_INT >= TIRAMISU) {
                context.packageManager.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getApplicationInfo(packageName, 0)
            }
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
