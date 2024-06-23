import java.util.Properties

//plugins {
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.jetbrains.kotlin.android)
//    id("dagger.hilt.android.plugin")
//    alias(libs.plugins.com.kotlin.kapt)
//    alias(libs.plugins.com.google.gms.services)
//    alias(libs.plugins.jacoco)
//    alias(libs.plugins.com.google.firebase.perf)
//}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("jacoco")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.mardillu.malami"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mardillu.malami"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }
        val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY") ?: ""
        val cloudSpeechApiKey: String = localProperties.getProperty("CLOUD_SPEECH_API_KEY") ?: ""
        val loginWithGoogleWebClientId: String = localProperties.getProperty("LOGIN_WITH_GOOGLE_WEB_CLIENT_ID") ?: ""
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "GEMINI_API_KEY", geminiApiKey)
            buildConfigField("String", "CLOUD_SPEECH_API_KEY", cloudSpeechApiKey)
            buildConfigField("String", "LOGIN_WITH_GOOGLE_WEB_CLIENT_ID", loginWithGoogleWebClientId)
        }

        debug {
            buildConfigField("String", "GEMINI_API_KEY", geminiApiKey)
            buildConfigField("String", "CLOUD_SPEECH_API_KEY", cloudSpeechApiKey)
            buildConfigField("String", "LOGIN_WITH_GOOGLE_WEB_CLIENT_ID", loginWithGoogleWebClientId)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":player-service"))
    implementation(project(":auth-with-google"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform(libs.com.google.firebase.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    //implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.com.google.accompanist.navigation.animation)
    implementation(libs.org.jetbrains.kotlinx.coroutines.android)
    implementation(libs.com.google.firebase.auth)
    implementation(libs.com.google.firebase.firestore)
    implementation(libs.com.google.dagger.hilt.android)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.org.jetbrains.kotlinx.coroutines.core)
    implementation(libs.generativeai)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.markdown.core)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.common)
    implementation(libs.androidx.hilt.work)
    //implementation(libs.androidx.media)
    implementation(libs.converter.gson)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.media3.ui)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //implementation(libs.onetapcompose)




    kapt(libs.kapt)
    kapt(libs.hilt.kapt)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
apply(plugin = "dagger.hilt.android.plugin")