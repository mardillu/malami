package com.mardillu.malami.ui.courses.course_modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 25/05/2024 at 12:06 pm
 * @author mardillu
 */
@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val courseRepository: CoursesRepository,
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {

    fun updateModuleCompletedStatusById(courseId: String, sectionId: String, moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            delay(5000)
            courseRepository.updateModuleCompletedStatusById(courseId, sectionId, moduleId, completed)
        }
    }
}