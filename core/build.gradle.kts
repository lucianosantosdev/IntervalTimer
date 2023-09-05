plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.dicedmelon.gradle.jacoco-android")
}

android {
    namespace = "dev.lucianosantos.intervaltimer.core"
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        minSdk = Versions.MIN_SDK
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = Versions.JAVA_VERSION
        targetCompatibility = Versions.JAVA_VERSION
    }

    kotlinOptions {
        jvmTarget = Versions.JVM_TARGET
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

jacoco {
    toolVersion = Versions.JACOCO_TOOL_VERSION
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.lifecycle)
    implementation(libs.compose.activity)

    testImplementation(libs.bundles.tests)
    androidTestImplementation(libs.bundles.androidTests)
}