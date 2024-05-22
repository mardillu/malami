package com.mardillu.malami.ui.courses.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import com.mardillu.malami.ui.onboarding.OnboardState
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
class CreateCourseViewModel @Inject constructor(
    private val courseRepository: CoursesRepository,
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {

    private val _createCourseState = MutableStateFlow<CreateCourseState>(CreateCourseState.Idle)
    val createCourseState: StateFlow<CreateCourseState> get() = _createCourseState

    var userPrompt by mutableStateOf("")

    fun createCourse(subject: String, learningGoals: String, priorKnowledge: String, geminiAPIKey: String) {
        if (subject.isBlank() || learningGoals.isBlank() || priorKnowledge.isBlank() || userPrompt.isBlank()) {
            _createCourseState.value = CreateCourseState.Error("All fields are required")
            return
        }

        viewModelScope.launch {
            _createCourseState.value = CreateCourseState.Loading
            try {
                val course = courseRepository.createCourse(userPrompt, geminiAPIKey)
                course.getOrNull().let {
                    if (it?.text == null) {
                        _createCourseState.value = CreateCourseState.Error("Failed to create course")
                    } else {
                        _createCourseState.value = CreateCourseState.Success(it)
                    }
                }
            } catch (e: Exception) {
                _createCourseState.value = CreateCourseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createPrompt(subject: String, learningGoals: String, priorKnowledge: String) {
        viewModelScope.launch {
            val userPreference = preferencesRepository.getUserPreferences()
            userPreference.getOrNull()?.let {
                val prompt = """
                        Create a complete course for a person with the following requirements and learning styles: 
                        [What do you want to learn?: $subject; learning goals: $learningGoals; Prior Knowledge: $priorKnowledge; 
                        Preferred Learning Style: ${it.learningStyle}; Pace of Learning: ${it.paceOfLearning}; 
                        Preferred Study Time: ${it.studyTime}; Reading Speed: ${it.readingSpeed}; Difficulty Level of Reading Material: ${it.difficultyLevel}; 
                        Special Requirements: ${it.specialRequirements}]. The course should be divided into sections, 
                        and each section into modules whose content can be as long as possible. Create the full content 
                        for each of the modules for the person to read, (not bullet point guides). 
                        Your response should be in json and should exactly match this json structure:
                        {
                            title: title,
                            short_description: short description,
                            course_outline: *course outline in markdown*,
                            learning_schedule: {
                                time: 5:30 PM,
                                frequency: daily or weekly,
                                day: day of week, or null of daily
                            },
                            sections:[
                                {
                                    title: title,
                                    short_description: short description,
                                    modules: [
                                        {
                                            title: title,
                                            short_description: short description,
                                            content: module content in markdown,
                                        }
                                    ]
                                    quiz: [ // 5 or more questions
                                        {
                                            question: question,
                                            options: [exactly 4 options],
                                            answer: correct answer
                                        }
                                    ]
                                },
                                ...
                            ]
                        }
                """.trimIndent()

                userPrompt = prompt
            } ?: run {
                _createCourseState.value = CreateCourseState.Error("Failed to get user preferences: ${userPreference.exceptionOrNull()?.message}")
            }
        }
    }

    fun setCreateCourseStateIdle() {
        _createCourseState.value = CreateCourseState.Idle
    }
}

sealed class CreateCourseState {
    data object Idle : CreateCourseState()
    data object Loading : CreateCourseState()
    data class Success(val course: GenerateContentResponse) : CreateCourseState()
    data class Error(val message: String) : CreateCourseState()
}