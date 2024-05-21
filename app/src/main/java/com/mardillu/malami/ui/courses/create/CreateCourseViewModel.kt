package com.mardillu.malami.ui.courses.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 20/05/2024 at 10:52 pm
 * @author mardillu
 */
@HiltViewModel
class CreateCourseViewModel @Inject constructor(): ViewModel() {

    private val _createCourseState = MutableStateFlow<CreateCourseState>(CreateCourseState.Idle)
    val createCourseState: StateFlow<CreateCourseState> get() = _createCourseState

    fun createCourse(subject: String, learningGoals: String, priorKnowledge: String) {
        if (subject.isBlank() || learningGoals.isBlank() || priorKnowledge.isBlank()) {
            _createCourseState.value = CreateCourseState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _createCourseState.value = CreateCourseState.Loading
            try {
                // Simulate course creation
                kotlinx.coroutines.delay(2000)
                _createCourseState.value = CreateCourseState.Success
            } catch (e: Exception) {
                _createCourseState.value = CreateCourseState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class CreateCourseState {
    data object Idle : CreateCourseState()
    data object Loading : CreateCourseState()
    data object Success : CreateCourseState()
    data class Error(val message: String) : CreateCourseState()
}