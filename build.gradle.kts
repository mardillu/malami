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

    //alias(libs.plugins.com.google.firebase.crashlytics) apply false
}