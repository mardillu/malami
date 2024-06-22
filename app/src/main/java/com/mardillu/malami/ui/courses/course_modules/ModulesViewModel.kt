package com.mardillu.malami.ui.courses.course_modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.data.model.course.QuizAttempt
import com.mardillu.malami.data.model.course.QuizAttempts
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import com.mardillu.malami.ui.courses.quiz.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 25/05/2024 at 12:06 pm
 * @author mardillu
 */
@HiltViewModel
class ModulesViewModel @Inject constructor(
    private val courseRepository: CoursesRepository,
    private val preferencesManager: PreferencesManager,
): ViewModel() {

    private val _quizAttemptsUiState = MutableStateFlow<List<QuizAttempt>>(emptyList())
    val quizAttemptsUiState: StateFlow<List<QuizAttempt>> get() = _quizAttemptsUiState

    init {
        getQuizAttempts()
    }

    fun updateModuleCompletedStatusById(courseId: String, sectionId: String, moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            delay(5000)
            courseRepository.updateModuleCompletedStatusById(courseId, sectionId, moduleId, completed)
        }
    }

    fun isQuizTaken(sectionId: String): Boolean {
        return _quizAttemptsUiState.value.any { it.sectionId == sectionId }
    }

    private fun getQuizAttempts() {
        viewModelScope.launch {
            val quizAttempts = courseRepository.getQuizAttempts()
            quizAttempts.getOrNull()?.let { attempts ->
                _quizAttemptsUiState.update {
                    attempts
                }
            } ?: run {
//                _quizUiState.update {
//                    UIState.Error("Failed to get courses")
//                }
            }
        }
    }

    fun moduleHasAudio(moduleId: String): Boolean {
        val audios = preferencesManager.savedCourseAudios
        return audios.any { it.moduleId == moduleId }
    }
}