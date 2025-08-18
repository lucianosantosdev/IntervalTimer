import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
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
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.lucianosantos.intervaltimer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.compileSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "REVENUECAT_API", "\"goog_CuJPQCNRKOsoYstKVXcxVBgqkUJ\"")
        buildConfigField("String", "GOOGLE_BANNER_ADS_ID", "\"ca-app-pub-1325449258005309/4736998612\"")
        buildConfigField("String", "GOOGLE_BANNER_INTERESTIAL_ID", "\"ca-app-pub-1325449258005309/1615924871\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)

    implementation(libs.navigation.ui.ktx)
    implementation(libs.bundles.lifecycle)

    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.bundles.composeDebug)
    testImplementation(libs.bundles.tests)
    androidTestImplementation(libs.bundles.androidTests)

    // From compose plugin
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.google.ads)
    implementation(libs.billing.ktx)
    implementation(libs.compose.material.icons.extended)

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
}

