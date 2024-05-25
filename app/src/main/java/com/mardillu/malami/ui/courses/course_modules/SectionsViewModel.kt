package com.mardillu.malami.ui.courses.course_modules

import androidx.lifecycle.ViewModel
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created on 25/05/2024 at 12:06 pm
 * @author mardillu
 */
@HiltViewModel
class SectionsViewModel @Inject constructor(
    private val courseRepository: CoursesRepository,
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {

}