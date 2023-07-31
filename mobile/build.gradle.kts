plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "dev.lucianosantos.intervaltimer"
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "dev.lucianosantos.intervaltimer"
        minSdk = Versions.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.COMPOSE_COMPILER
    }

}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0-alpha02")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0-alpha02")
    implementation(libs.numberPicker)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.lifecycle)

    implementation(libs.compose.activity)
    implementation(libs.navigation.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)

    debugImplementation(libs.bundles.composeDebug)
    testImplementation(libs.bundles.tests)
    androidTestImplementation(libs.bundles.androidTests)

    implementation(project(":core"))
    wearApp(project(":wear"))
}
