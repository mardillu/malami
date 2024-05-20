package com.mardillu.malami.ui.onboarding

/**
 * Created on 19/05/2024 at 1:23 pm
 * @author mardillu
 */
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mardillu.malami.data.model.UserPreferences
import com.mardillu.malami.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    var learningStyle by mutableStateOf("")
    var paceOfLearning by mutableStateOf("")
    var studyTime by mutableStateOf("")
    var studyEnvironment by mutableStateOf("")
    var deviceUsage by mutableStateOf("")
    var contentFormat by mutableStateOf("")
    var readingSpeed by mutableStateOf("")
    var difficultyLevel by mutableStateOf("")
    var topicsOfInterest by mutableStateOf("")
    var learningGoals by mutableStateOf("")
    var priorKnowledge by mutableStateOf("")
    var feedbackType by mutableStateOf("")
    var assessmentPreferences by mutableStateOf("")
    var specialRequirements by mutableStateOf("")

    var showSuccessToast by mutableStateOf(false)
    var showErrorDialog by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun savePreferences() {
        val userPreferences = UserPreferences(
            learningStyle = learningStyle,
            paceOfLearning = paceOfLearning,
            studyTime = studyTime,
            studyEnvironment = studyEnvironment,
            deviceUsage = deviceUsage,
            contentFormat = contentFormat,
            readingSpeed = readingSpeed,
            difficultyLevel = difficultyLevel,
            topicsOfInterest = topicsOfInterest,
            learningGoals = learningGoals,
            priorKnowledge = priorKnowledge,
            feedbackType = feedbackType,
            assessmentPreferences = assessmentPreferences,
            specialRequirements = specialRequirements
        )

        viewModelScope.launch {
            val result = preferencesRepository.saveUserPreferences(userPreferences)
            if (result.isSuccess) {
                showSuccessToast = true
            } else {
                errorMessage = result.exceptionOrNull()?.message.orEmpty()
                showErrorDialog = true
            }
        }
    }
}