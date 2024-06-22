package com.mardillu.malami.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.mardillu.malami.data.model.course.ModuleAudio

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
        private const val COURSE_LIST_VIEW_STYLE = "course_list_view_style"
        private const val SAVED_AUDIOS = "saved_audios"
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

    var courseListViewStyle: String
        get() = sharedPreferences.getString(COURSE_LIST_VIEW_STYLE, "grid") ?: "grid"
        set(value) = sharedPreferences.edit { putString(COURSE_LIST_VIEW_STYLE, value) }

    var savedCourseAudios: List<ModuleAudio>
        get() = getSharedPrefObject<List<ModuleAudio>>(SAVED_AUDIOS) ?: emptyList()
        set(value) = saveToSharedPref(SAVED_AUDIOS, value)

    fun clear() {
        sharedPreferences.edit { clear() }
    }

    private fun saveToSharedPref(key: String, objectValue: Any?) {
        val serializedObject = Gson().toJson(objectValue)
        sharedPreferences.edit { putString(key, serializedObject) }
    }

    private inline fun <reified T> getSharedPrefObject(
        preferenceKey: String
    ): T? {

        if (sharedPreferences.contains(preferenceKey)) {
            return try {
                val cache = sharedPreferences.getString(preferenceKey, null) ?: return null

                Gson().fromJson(cache, object : TypeToken<T>() {}.type)
            } catch (e: JsonSyntaxException) {
                null
            } catch (ex: Exception) {
                null
            }
        }

        return null
    }
}