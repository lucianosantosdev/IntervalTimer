import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
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
            isMinifyEnabled = false
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
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.firebase)

    implementation(libs.material)
    implementation("com.google.android.gms:play-services-wearable:18.0.0")

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
}