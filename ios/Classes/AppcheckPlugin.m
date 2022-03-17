#import "AppcheckPlugin.h"
#if __has_include(<appcheck/appcheck-Swift.h>)
#import <appcheck/appcheck-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "appcheck-Swift.h"
#endif

@implementation AppcheckPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftAppcheckPlugin registerWithRegistrar:registrar];
}
@end
