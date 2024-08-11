package com.mardillu.malami.ui.courses.create

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.ai.client.generativeai.type.Candidate
import com.google.ai.client.generativeai.type.CodeExecutionResultPart
import com.google.ai.client.generativeai.type.ExecutableCodePart
import com.google.ai.client.generativeai.type.TextPart
import com.google.gson.Gson
import com.mardillu.malami.data.model.UserPreferences
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.model.course.MlCandidate
import com.mardillu.malami.data.model.course.MlGenerateContentResponse
import com.mardillu.malami.data.model.course.ModuleAudio
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.data.repository.PreferencesRepository
import com.mardillu.malami.network.NetworkResult
import com.mardillu.malami.ui.courses.quiz.UIState
import com.mardillu.malami.utils.add
import com.mardillu.malami.utils.addAll
import com.mardillu.malami.work.TextToSpeechWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
    private val workManager: WorkManager
): ViewModel() {

    private val _createCourseState = MutableStateFlow<CreateCourseState>(CreateCourseState.Idle)
    val createCourseState: StateFlow<CreateCourseState> get() = _createCourseState

    private var userPrompt by mutableStateOf("")

    private val _saveQuizUiState = MutableStateFlow<UIState>(UIState.Idle)
    val saveQuizUiState: StateFlow<UIState> get() = _saveQuizUiState

    private val _newCourseId = MutableStateFlow("")
    val newCourseId: StateFlow<String> get() = _newCourseId

    private val _moduleContents = MutableStateFlow<List<ModuleAudio>>(emptyList())
    val moduleContents: StateFlow<List<ModuleAudio>> get() = _moduleContents

    private val _ttsApiKey = MutableStateFlow("")
    val ttsApiKey: StateFlow<String> get() = _ttsApiKey

    fun setTtsApiKey(apiKey: String) {
        _ttsApiKey.update { apiKey }
    }

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
            Your response should be in json, you should escape any special characters like single and double quotes in the json, the json keys should be in camel case and the json should exactly match this json structure:
            {
                id: abc-123-def, //uuid
                title: title,
                shortDescription: short description,
                courseOutline: *course outline in markdown*,
                bannerImage: description of a suitable image for the course,
                learningSchedule: {
                    time: 5:30 PM,
                    frequency: daily or weekly,
                    day: day of week, or null of daily
                },
                sections:[
                    {
                        id: abc-123-def, //uuid
                        title: title,
                        shortDescription: short description,
                        aiExplainability: briefly explain how the choice of this section contents were made.
                        modules: [
                            {
                                id: abc-123-def, //uuid
                                title: title,
                                shortDescription: short description,
                                content: module content in markdown,
                                timeToRead: estimated reading time (eg. 5 mins),
                                bannerImage: description of a suitable image for the module
                            }
                        ]
                        quiz: [ // 5 or more questions
                            {
                                id: abc-123-def, //uuid
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
                val newCourseResult = courseRepository.createCourseCustom(userPrompt, geminiAPIKey)
                if (newCourseResult is NetworkResult.Success) {
                    newCourseResult.data?.body()?.let { responseBody ->
                        val text = getCandidateText(responseBody.candidates)
                        val course = Gson().fromJson(text, Course::class.java)
                        //val course = Json.decodeFromString<Course>(text)
                        _newCourseId.update { course.id }
                        getCourses(course, responseBody)
                    } ?: throw IllegalStateException("Response body is null")
                } else {
                    throw IllegalStateException("Failed to create course")
                }
            }.onFailure { exception ->
                _createCourseState.value = CreateCourseState.Error(exception.message ?: "Unknown error")
            }
        }
    }

    private fun getCandidateText(candidates: List<MlCandidate>): String{
        return candidates
            .first()
            .content
            .parts
            .filter { it is TextPart || it is ExecutableCodePart || it is CodeExecutionResultPart }
            .joinToString(" ") {
                when (it) {
                    is TextPart -> it.text
                    is ExecutableCodePart -> "\n```${it.language.lowercase()}\n${it.code}\n```"
                    is CodeExecutionResultPart -> "\n```\n${it.output}\n```"
                    else -> throw RuntimeException("unreachable")
                }
            }
    }

    private fun saveCourse(course: List<Course>, response: MlGenerateContentResponse) {
        viewModelScope.launch {
            runCatching {
                val saveResult = courseRepository.saveCourse(course)
                if (saveResult.isSuccess) {
                    convertTextsToSpeech(course)
                    _createCourseState.value = CreateCourseState.Success(response)
                } else {
                    throw IllegalStateException("Failed to save course")
                }
            }.onFailure {
                _createCourseState.value = CreateCourseState.Error(it.message ?: "Unknown error")
            }
        }
    }

    private fun getModuleContent(courses: List<Course>): List<ModuleAudio> {
        val moduleContents = mutableListOf<ModuleAudio>()
        val course = courses.find { it.id == _newCourseId.value }
        return course?.let { course ->
            val courseTitle = course.title
            var sequence = 0
            course.sections.forEach { sctn ->
                sequence
                val sectionTitle = sctn.title
                val sectionId = sctn.id
                sctn.modules.forEach { mdl ->
                    sequence += 1
                    val moduleTitle = mdl.title
                    val content = mdl.content
                    val description = mdl.shortDescription
                    val moduleId = mdl.id
                    val moduleContent = ModuleAudio(
                        _newCourseId.value,
                        moduleId,
                        sectionId,
                        courseTitle,
                        sectionTitle,
                        moduleTitle,
                        description,
                        content,
                        sequence,
                        ""
                    )
                    moduleContents.add(moduleContent)
                }
            }
            _moduleContents.update { moduleContents }
            moduleContents
        } ?: run {
            Log.d("AudioPlayerViewModel", "Course not found")
            emptyList()
        }
    }

    private fun convertTextsToSpeech(course: List<Course>) {
        val moduleContent = getModuleContent(course)
        moduleContent.forEach { content ->
            val contentJson = Gson().toJson(content)
            val inputData = workDataOf(
                "content" to contentJson,
                "key" to _ttsApiKey.value
            )
            val workRequest = OneTimeWorkRequestBuilder<TextToSpeechWorker>()
                .setInputData(inputData)
                .build()
            workManager.enqueueUniqueWork(content.moduleId, ExistingWorkPolicy.KEEP, workRequest,)
        }
    }

    fun setCreateCourseStateIdle() {
        _createCourseState.value = CreateCourseState.Idle
    }

    private fun getCourses(course: Course, response: MlGenerateContentResponse) {
        viewModelScope.launch {
            val userCourses = courseRepository.getCourses(false)
            userCourses.getOrNull()?.let { courses ->
                val newCourses = courses.add(course)
                saveCourse(newCourses, response)
            } ?: run {
                _createCourseState.update {
                    CreateCourseState.Error("Failed to get courses")
                }
            }
        }
    }
}

sealed class CreateCourseState {
    data object Idle : CreateCourseState()
    data object Loading : CreateCourseState()
    data class Success(val course: MlGenerateContentResponse) : CreateCourseState()
    data class Error(val message: String) : CreateCourseState()
}
