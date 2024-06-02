package com.mardillu.malami.ui.courses.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.model.course.QuizAttempt
import com.mardillu.malami.data.repository.CoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val preferencesManager: PreferencesManager
): ViewModel() {

    private val _ongoingCourseListState = MutableStateFlow<List<Pair<Course, Float>>>(emptyList())
    val ongoingCourseListState: StateFlow<List<Pair<Course, Float>>> get() = _ongoingCourseListState

    private val _userCourses = MutableStateFlow<List<Course>>(emptyList())
    val courseListState: StateFlow<List<Course>> get() = _userCourses

    init {
        viewModelScope.launch {
            courseRepository.userCoursesFlow.collect { courses ->
                updateOngoingCourseList(courses)
                _userCourses.update {
                    courses
                }
            }
        }
        courseRepository.startListeningForUserCourses()
    }

    private fun updateOngoingCourseList(courses: List<Course>) {
        val ongoingCourse = courses.filter {
            it.sections.any {
                it.modules.any {
                    it.completed
                }
            }
        }

        val ongoingCourseProgress: List<Pair<Course, Float>> = ongoingCourse.map {
            var totalModules = 0.0f
            var completeModules = 0.0f
            it.sections.forEach {
                totalModules += it.modules.size
                completeModules += it.modules.filter { it.completed }.size
            }
            val progress = completeModules/totalModules
           Pair(it, progress)
        }

        _ongoingCourseListState.update {
            ongoingCourseProgress
        }
    }

    override fun onCleared() {
        super.onCleared()
        courseRepository.stopListeningForUserCourses()
    }

    fun getCourseListViewStyle(): CourseListViewType {
        val style = preferencesManager.courseListViewStyle
        return when (style) {
            "grid" -> CourseListViewType.Grid
            "compact" -> CourseListViewType.Compact
            "comfortable" -> CourseListViewType.Comfortable
            else -> CourseListViewType.Grid
        }
    }

    fun setCourseListViewStyle(style: CourseListViewType) {
        when (style) {
            CourseListViewType.Grid -> preferencesManager.courseListViewStyle ="grid"
            CourseListViewType.Compact -> preferencesManager.courseListViewStyle = "compact"
            CourseListViewType.Comfortable -> preferencesManager.courseListViewStyle = "comfortable"
        }
    }

    fun isModuleActive(sectionIndex: Int, moduleIndex: Int, courseId: String, attempts: List<QuizAttempt>, prevSectionId: String): Boolean {
        val course = _userCourses.value.find { it.id == courseId }
        val section = course?.sections?.get(sectionIndex)
        val module = section?.modules?.get(moduleIndex)
        val isActive = module?.let {
            val moduleActive = if (module.completed || (sectionIndex == 0 && moduleIndex == 0))
                true
            else if (sectionIndex != 0 && moduleIndex == 0){
                attempts.any { it.sectionId == prevSectionId }
            } else {
                (section.modules[moduleIndex-1].completed)
            }
            moduleActive
        }

        return isActive ?: false
    }
}


sealed class CourseListState {
    data object Idle : CourseListState()
    data object Loading : CourseListState()
    data class Success<T>(val courses: T) : CourseListState()
    data class Error(val message: String) : CourseListState()
}