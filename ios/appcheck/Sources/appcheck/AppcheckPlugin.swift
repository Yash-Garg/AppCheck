import Flutter
import UIKit

public class AppcheckPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "dev.yashgarg/appcheck", binaryMessenger: registrar.messenger())
        let instance = AppcheckPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = call.arguments as? [String: Any],
              let uriSchema = arguments["uri"] as? String else {
            result(FlutterError(code: "INVALID_ARGUMENT", message: "Missing uri argument", details: nil))
            return
        }

        switch call.method {
        case "checkAvailability":
            result(checkAvailability(uri: uriSchema))
        case "launchApp":
            launchApp(uri: uriSchema, result: result)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    public func checkAvailability(uri: String) -> Bool {
        guard let url = URL(string: uri) else {
            return false
        }
        return AppcheckPlugin.canOpen(url)
    }

    public func launchApp(uri: String, result: @escaping FlutterResult) {
        guard let url = URL(string: uri) else {
            result(false)
            return
        }

        // Per Apple's guidance, attempt to open the URL directly and handle
        // failure via the completion handler rather than pre-flighting with
        // `canOpenURL(_:)`.
        UIApplication.shared.open(url, options: [:]) { success in
            result(success)
        }
    }

    // `canOpenURL(_:)` was deprecated in iOS 27 with no direct replacement for
    // pre-flight availability checks (Apple's guidance to "attempt to open and
    // handle failure" doesn't apply when callers explicitly want to check
    // availability without launching, e.g. `checkAvailability`). The call is
    // isolated here, in a function that is itself marked deprecated, so the
    // compiler-level deprecation warning stays contained to this one spot.
    @available(iOS, deprecated: 27.0, message: "canOpenURL has no replacement for availability-only checks; see checkAvailability(uri:).")
    private static func canOpen(_ url: URL) -> Bool {
        UIApplication.shared.canOpenURL(url)
    }
}
