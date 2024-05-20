package com.mardillu.malami

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MalamiApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}