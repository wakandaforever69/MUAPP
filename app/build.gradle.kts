plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    compileSdk = 35
    namespace = "com.muapp.android"  // ✅ Fixed

    defaultConfig {
        applicationId = "com.muapp.android"  // ✅ Ensure this matches your package
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.ui)
    implementation(libs.androidx.material)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose.v275)
    implementation(libs.google.accompanist.systemuicontroller)
    implementation(libs.androidx.material.v160)
    implementation(libs.androidx.foundation.v160)
    implementation (libs.androidx.ui.v160)
    implementation("com.google.mlkit:barcode-scanning:17.2.0") // ML Kit for barcode scanning
    implementation("androidx.camera:camera-core:1.3.0")         // CameraX core
    implementation("androidx.camera:camera-camera2:1.3.0")     // CameraX Camera2 support
    implementation("androidx.camera:camera-lifecycle:1.3.0")   // Lifecycle-aware CameraX
    implementation("androidx.camera:camera-view:1.3.0")        // CameraX View
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    debugImplementation(libs.ui.tooling)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
