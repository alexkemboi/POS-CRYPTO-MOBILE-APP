plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pos.crypto_pay_kt"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.pos.crypto_pay_kt"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "AGENT_ID", "\"22c56aa7-2520-4ca1-9134-dc7aac0985a4\"")
        buildConfigField("String", "TERMINAL_ID", "\"TERM-6F8EBA\"")
        buildConfigField(
            "String",
            "MERCHANT_WALLET_ADDRESS",
            "\"0x24c6a00a9743694fb46e00efff2f5ade950f72e8\"",
        )
    }

    flavorDimensions += "environment"
    productFlavors {
        create("local") {
            dimension = "environment"
            applicationIdSuffix = ".local"
            versionNameSuffix = "-local"
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:3000/\"")
            buildConfigField("String", "SIGNALR_URL", "\"http://10.0.2.2:3000/txHub\"")
            resValue("string", "app_name", "Crypto Pay Local")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("String", "BASE_URL", "\"https://api.ikonexsystems.com/\"")
            buildConfigField("String", "SIGNALR_URL", "\"https://api.ikonexsystems.com/txHub\"")
            resValue("string", "app_name", "Crypto Pay Staging")
        }
        create("production") {
            dimension = "environment"
            buildConfigField("String", "BASE_URL", "\"https://api.ikonexsystems.com/\"")
            buildConfigField("String", "SIGNALR_URL", "\"https://api.ikonexsystems.com/txHub\"")
            resValue("string", "app_name", "Crypto Pay")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
        compose = true
        resValues = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.okhttp.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.signalr)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
