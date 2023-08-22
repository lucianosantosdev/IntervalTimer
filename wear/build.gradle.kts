plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "dev.lucianosantos.intervaltimer"
    compileSdk = Versions.WEAR_COMPILE_SDK

    defaultConfig {
        applicationId = "dev.lucianosantos.intervaltimer"
        minSdk = Versions.MIN_SDK
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.wear:wear:1.2.0")
    implementation("com.google.android.gms:play-services-wearable:18.0.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("io.github.ShawnLin013:number-picker:2.4.13")

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


    implementation(libs.horologist.composables)

    implementation(project(":core"))
    implementation(libs.androidx.compose.ui.tooling)
}