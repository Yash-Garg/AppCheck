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
        return UIApplication.shared.canOpenURL(url)
    }

    public func launchApp(uri: String, result: @escaping FlutterResult) {
        guard let url = URL(string: uri), checkAvailability(uri: uri) else {
            result(false)
            return
        }

        if #available(iOS 10.0, *) {
            UIApplication.shared.open(url, options: [:]) { success in
                result(success)
            }
        } else {
            let success = UIApplication.shared.openURL(url)
            result(success)
        }
    }
}
