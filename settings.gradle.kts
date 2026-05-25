pluginManagement {
    // The plugin lives in its own single-project build inside store-screenshots.
    includeBuild("store-screenshots/plugin")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
// The library (consumed at test time via the plugin) lives in the store-screenshots root build.
includeBuild("store-screenshots")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("./build-logic/libs.versions.toml"))
        }
    }
}

rootProject.name = "IntervalTimer"
include("mobile")
include("wear")
include("core")
