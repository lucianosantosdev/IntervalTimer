plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "dev.lucianosantos.intervaltimer"
    compileSdk = Versions.COMPILE_SDK

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
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.wear:wear:1.2.0")
    implementation("com.google.android.gms:play-services-wearable:18.0.0")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.percentlayout:percentlayout:1.0.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("io.github.ShawnLin013:number-picker:2.4.13")

    implementation(project(":core"))
}