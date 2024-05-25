package com.mardillu.malami.ui.courses.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.mardillu.malami.data.model.UserPreferences
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
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

    private var userPrompt by mutableStateOf("")

    fun onCreateCourseClicked(subject: String, learningGoals: String, priorKnowledge: String, apiKey: String) {
        if (subject.isBlank() || learningGoals.isBlank() || priorKnowledge.isBlank()) {
            _createCourseState.value = CreateCourseState.Error("All fields are required")
            return
        }
        _createCourseState.value = CreateCourseState.Loading
        createPrompt(subject, learningGoals, priorKnowledge, apiKey)
    }

    private fun createPrompt(subject: String, learningGoals: String, priorKnowledge: String, apiKey: String) {
        viewModelScope.launch {
            val userPreferenceResult = preferencesRepository.getUserPreferences()
            userPreferenceResult.getOrNull()?.let { userPreference ->
                val prompt = buildPrompt(subject, learningGoals, priorKnowledge, userPreference)
                userPrompt = prompt
                createCourse(apiKey)
            } ?: run {
                _createCourseState.value = CreateCourseState.Error("Failed to get user preferences.")
            }
        }
    }

    private fun buildPrompt(subject: String, learningGoals: String, priorKnowledge: String, userPreference: UserPreferences): String {
        return """
            Create a complete course for a person with the following requirements and learning styles: 
            [What do you want to learn?: $subject; learning goals: $learningGoals; Prior Knowledge: $priorKnowledge; 
            Preferred Learning Style: ${userPreference.learningStyle}; Pace of Learning: ${userPreference.paceOfLearning}; 
            Preferred Study Time: ${userPreference.studyTime}; Reading Speed: ${userPreference.readingSpeed}; Difficulty Level of Reading Material: ${userPreference.difficultyLevel}; 
            Special Requirements: ${userPreference.specialRequirements}]. The course should be divided into sections, 
            and each section into modules whose content can be as long as possible. Create the full content 
            for each of the modules for the person to read, (not bullet point guides). 
            Your response should be in json, the json keys should be in camel case and the json should exactly match this json structure:
            {
                title: title,
                shortDescription: short description,
                courseOutline: *course outline in markdown*,
                learningSchedule: {
                    time: 5:30 PM,
                    frequency: daily or weekly,
                    day: day of week, or null of daily
                },
                sections:[
                    {
                        title: title,
                        shortDescription: short description,
                        modules: [
                            {
                                title: title,
                                shortDescription: short description,
                                content: module content in markdown,
                                timeToRead: estimated reading time (eg. 5 mins),
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
    }

    private fun createCourse(geminiAPIKey: String) {
        viewModelScope.launch {
            runCatching {
                val newCourse = courseRepository.createCourse(userPrompt, geminiAPIKey)
                newCourse.getOrThrow().let {
                    if (it.text == null) {
                        throw IllegalStateException("Failed to create course")
                    } else {
                        val course = Json.decodeFromString<Course>(it.text!!)
                        saveCourse(course, it)
                    }
                }
            }.onFailure {
                _createCourseState.value = CreateCourseState.Error(it.message ?: "Unknown error")
            }
        }
    }

    private fun saveCourse(course: Course, response: GenerateContentResponse) {
        viewModelScope.launch {
            runCatching {
                val saveResult = courseRepository.saveCourse(course)
                if (saveResult.isSuccess) {
                    _createCourseState.value = CreateCourseState.Success(response)
                } else {
                    throw IllegalStateException("Failed to save course")
                }
            }.onFailure {
                _createCourseState.value = CreateCourseState.Error(it.message ?: "Unknown error")
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
