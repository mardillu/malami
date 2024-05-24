package com.mardillu.malami.ui.courses.create

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
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

    private fun createCourse(geminiAPIKey: String) {
        viewModelScope.launch {
            try {
                val newCourse = courseRepository.createCourse(userPrompt, geminiAPIKey)
                newCourse.getOrNull().let {
                    if (it?.text == null) {
                        _createCourseState.value = CreateCourseState.Error("Failed to create course")
                    } else {
                        val course = Json.decodeFromString<Course>(it.text!!)
                        saveCourse(course, it)
                    }
                }
            } catch (e: Exception) {
                _createCourseState.value = CreateCourseState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun saveCourse(course: Course, it: GenerateContentResponse) {
        viewModelScope.launch {
            val save = courseRepository.saveCourse(course)
            if (save.isSuccess) {
                _createCourseState.value = CreateCourseState.Success(it)
            } else {
                _createCourseState.value = CreateCourseState.Error("Failed to save course")
            }
        }
    }

    private fun createPrompt(subject: String, learningGoals: String, priorKnowledge: String, apiKey: String) {
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
                createCourse(apiKey)
            } ?: run {
                _createCourseState.value = CreateCourseState.Error("Failed to get user preferences: ${userPreference.exceptionOrNull()?.message}")
            }
        }
    }

    fun setCreateCourseStateIdle() {
        _createCourseState.value = CreateCourseState.Idle
    }

    fun onCreateCourseClicked(subject: String, learningGoals: String, priorKnowledge: String, apiKey: String) {
        _createCourseState.value = CreateCourseState.Loading
        if (subject.isBlank() || learningGoals.isBlank() || priorKnowledge.isBlank()) {
            _createCourseState.value = CreateCourseState.Error("All fields are required")
            return
        }
        viewModelScope.launch {
            val prompt = createPrompt(subject, learningGoals, priorKnowledge, apiKey)
        }
    }
}

sealed class CreateCourseState {
    data object Idle : CreateCourseState()
    data object Loading : CreateCourseState()
    data class Success(val course: GenerateContentResponse) : CreateCourseState()
    data class Error(val message: String) : CreateCourseState()
}