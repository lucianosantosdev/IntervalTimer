import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
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
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "dev.lucianosantos.intervaltimer"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.COMPILE_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "REVENUECAT_API", "\"goog_CuJPQCNRKOsoYstKVXcxVBgqkUJ\"")
        buildConfigField("String", "GOOGLE_ADS_ID", "\"ca-app-pub-1325449258005309/4736998612\"")
        manifestPlaceholders["admobApplicationId"] = "ca-app-pub-1325449258005309~1136664954"
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


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.bundles.lifecycle)

    implementation(libs.compose.activity)
    implementation(libs.navigation.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.google.services.ads)
    implementation(libs.billing.ktx)

    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.bundles.composeDebug)
    testImplementation(libs.bundles.tests)
    androidTestImplementation(libs.bundles.androidTests)

    // DI
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.core.coroutines)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.android)

    // Subscriptions
    implementation(libs.revenuecat)
    implementation(libs.revenuecat.ui)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(project(":core"))
    wearApp(project(":wear"))
}

