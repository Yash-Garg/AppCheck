// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "appcheck",
    platforms: [
        .iOS("9.0")
    ],
    products: [
        .library(name: "appcheck", targets: ["appcheck"])
    ],
    dependencies: [],
    targets: [
        .target(
            name: "appcheck",
            dependencies: [],
            resources: []
        )
    ]
)
