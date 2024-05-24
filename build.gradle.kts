import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        //classpath(libs.com.google.dagger.hilt.android.plugin)
        classpath(libs.org.jacoco.core)
        classpath(libs.com.google.firebase.perf.plugin)
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.google.dagger.hilt.android) apply false
    alias(libs.plugins.com.google.gms.services) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.com.google.perf) apply false
    kotlin("plugin.serialization") version "1.9.0" apply false


    //alias(libs.plugins.com.google.firebase.crashlytics) apply false
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

val apiKey: String = localProperties.getProperty("GEMINI_API_KEY") ?: ""

subprojects {
    // Pass the API_KEY to all subprojects
    extra["GEMINI_API_KEY"] = apiKey
}
