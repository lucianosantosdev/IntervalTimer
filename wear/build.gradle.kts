import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val keystoreProperties = Properties().apply {
    val keystoreFile = rootProject.file("keystore.properties")
    if (keystoreFile.exists()) {
        keystoreFile.inputStream().use { fis ->
            load(fis)
        }
    }
}

android {
    namespace = "dev.lucianosantos.intervaltimer"
    compileSdk = Versions.WEAR_COMPILE_SDK

    defaultConfig {
        applicationId = "dev.lucianosantos.intervaltimer"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.WEAR_COMPILE_SDK

        configurations.all {
            resolutionStrategy {
                force("androidx.emoji2:emoji2-views-helper:1.2.0")
                force("androidx.emoji2:emoji2:1.2.0")
            }
        }
    }

    signingConfigs {
        create("release") {
            if (!keystoreProperties.isEmpty) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (!keystoreProperties.isEmpty) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.firebase)
    implementation(libs.material)

    implementation(libs.compose.activity)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.foundation)
    implementation(libs.wear.compose.tooling.preview)
    implementation(libs.wear.compose.navigation)
    implementation(libs.navigation.ui.ktx)

    implementation(libs.bundles.lifecycle)

    debugImplementation(libs.compose.ui.tooling)

    implementation(project(":core"))
    implementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.wear:wear-ongoing:1.0.0")
    // Includes LocusIdCompat and new Notification categories for Ongoing Activity.

    val core_version = "1.6.0"
    implementation("androidx.core:core:$core_version")
    implementation("androidx.core:core-ktx:$core_version")

    // To use RoleManagerCompat
    implementation("androidx.core:core-role:1.0.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}