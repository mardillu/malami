package com.mardillu.malami.ui.courses.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.mardillu.malami.data.model.UserPreferences
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 24/05/2024 at 10:23 pm
 * @author mardillu
 */
@HiltViewModel
class CourseListViewModel @Inject constructor(
    private val courseRepository: CoursesRepository,
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {
    private val _courseListUIState = MutableStateFlow<CourseListState>(CourseListState.Idle)
    val courseListUIState: StateFlow<CourseListState> get() = _courseListUIState

    private val _courseListState = MutableStateFlow<List<Course>>(emptyList())
    val userCourses: StateFlow<List<Course>> get() = _courseListState

    private val _userCourses = MutableStateFlow<List<Course>>(emptyList())
    val courseListState: StateFlow<List<Course>> get() = _userCourses

    init {
        viewModelScope.launch {
            courseRepository.userCoursesFlow.collect { courses ->
                _userCourses.update {
                    courses
                }
            }
        }
        courseRepository.startListeningForUserCourses()
    }

    override fun onCleared() {
        super.onCleared()
        courseRepository.stopListeningForUserCourses()
    }

    fun updateModuleCompletedStatusById(courseId: String, sectionId: String, moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            delay(2 * 60 * 1000)
            courseRepository.updateModuleCompletedStatusById(courseId, sectionId, moduleId, completed)
        }
    }
}


sealed class CourseListState {
    data object Idle : CourseListState()
    data object Loading : CourseListState()
    data class Success(val courses: List<Course>) : CourseListState()
    data class Error(val message: String) : CourseListState()
}