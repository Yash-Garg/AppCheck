#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint appcheck.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'appcheck'
  s.version          = '1.0.3'
  s.summary          = 'Flutter plugin that allows you to check if an app is installed/enabled, launch an app and get the list of installed apps.'
  s.description      = <<-DESC
  Flutter plugin that allows you to check if an app is installed/enabled, launch an app and get the list of installed apps.
                       DESC
  s.homepage         = 'http://github.com/Yash-Garg/Appcheck'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Your Company' => 'email@example.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.platform = :ios, '9.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
