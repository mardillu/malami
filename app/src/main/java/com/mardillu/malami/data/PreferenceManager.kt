package com.mardillu.malami.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Created on 20/05/2024 at 9:38 pm
 * @author mardillu
 */
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "malami_app_prefs"
        private const val KEY_IS_LOGGED_IN = "is.logged.in"
        private const val KEY_IS_LEARNING_STYLE_SET = "is.learning.style.set"
        private const val KEY_IS_FIRST_TIME_LOGIN = "is.first.time.login"
    }

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit { putBoolean(KEY_IS_LOGGED_IN, value) }

    var isLearningStyleSet: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LEARNING_STYLE_SET, false)
        set(value) = sharedPreferences.edit { putBoolean(KEY_IS_LEARNING_STYLE_SET, value) }

    var isFirstTimeLogin: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_FIRST_TIME_LOGIN, true)
        set(value) = sharedPreferences.edit { putBoolean(KEY_IS_FIRST_TIME_LOGIN, value) }

    fun clear() {
        sharedPreferences.edit { clear() }
    }
}