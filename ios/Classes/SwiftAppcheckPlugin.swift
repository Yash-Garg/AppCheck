import Flutter
import UIKit

public class SwiftAppcheckPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "dev.yashgarg/appcheck", binaryMessenger: registrar.messenger())
        let instance = SwiftAppcheckPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        var uriSchema = ""
        let arguments = call.arguments as? NSDictionary
        switch call.method {
        case "checkAvailability":
            uriSchema = (arguments!["uri"] as? String)!
            result(checkAvailability(uri: uriSchema))
            break
        case "launchApp":
            uriSchema = (arguments!["uri"] as? String)!
            launchApp(uri: uriSchema, result: result)
            break
        default:
            break
        }
    }

    public func checkAvailability (uri: String) -> Bool {
        let url = URL(string: uri)
        return UIApplication.shared.canOpenURL(url!)
    }

    public func launchApp (uri: String, result: @escaping FlutterResult) {
        let url = URL(string: uri)
        if (checkAvailability(uri: uri)) {
            if #available(iOS 10.0, *) {
                UIApplication.shared.open(url!)
            } else {
                UIApplication.shared.openURL(url!)
            }
            result(true)
        }
        result(false)
    }

}
