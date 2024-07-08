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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _onboardState = MutableStateFlow<OnboardState>(OnboardState.Idle)
    val onboardState: StateFlow<OnboardState> get() = _onboardState

    var learningStyle by mutableStateOf("")
    var paceOfLearning by mutableStateOf("")
    var studyTime by mutableStateOf("")
    //var contentFormat by mutableStateOf("")
    var readingSpeed by mutableStateOf("")
    var difficultyLevel by mutableStateOf("")
    var feedbackType by mutableStateOf("")
    //var assessmentPreferences by mutableStateOf("")
    var specialRequirements by mutableStateOf("")

    init {
        getUserPreferences()
    }
    fun savePreferences() {
        _onboardState.value = OnboardState.Loading
        val userPreferences = UserPreferences(
            learningStyle = learningStyle,
            paceOfLearning = paceOfLearning,
            studyTime = studyTime,
            //contentFormat = contentFormat,
            readingSpeed = readingSpeed,
            difficultyLevel = difficultyLevel,
            feedbackType = feedbackType,
            //assessmentPreferences = assessmentPreferences,
            specialRequirements = specialRequirements
        )

        viewModelScope.launch {
            if (
                learningStyle.isEmpty() ||
                paceOfLearning.isEmpty() ||
                studyTime.isEmpty() ||
                //contentFormat.isEmpty() ||
                readingSpeed.isEmpty() ||
                difficultyLevel.isEmpty() ||
                feedbackType.isEmpty() ||
                //assessmentPreferences.isEmpty() ||
                specialRequirements.isEmpty()
            ) {
                _onboardState.value = OnboardState.Error("Please fill in all fields.")
                return@launch
            }
            val result = preferencesRepository.saveUserPreferences(userPreferences)
            if (result.isSuccess) {
                _onboardState.value = OnboardState.Success
            } else {
                _onboardState.value = OnboardState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun setOnboardStateIdle() {
        _onboardState.value = OnboardState.Idle
    }

    private fun getUserPreferences() {
        viewModelScope.launch {
            val userPreferenceResult = preferencesRepository.getUserPreferences()
            userPreferenceResult.getOrNull()?.let { userPreference ->
                learningStyle = userPreference.learningStyle
                paceOfLearning = userPreference.paceOfLearning
                studyTime = userPreference.studyTime
                //contentFormat = userPreference.contentFormat
                readingSpeed = userPreference.readingSpeed
                difficultyLevel = userPreference.difficultyLevel
                feedbackType = userPreference.feedbackType
                //assessmentPreferences = userPreference.assessmentPreferences
                specialRequirements = userPreference.specialRequirements
            } ?: run {
                //_onboardState.value = OnboardState.Error("Failed to get user preferences.")
            }
        }
    }
}


sealed class OnboardState {
    data object Success : OnboardState()
    data object Idle : OnboardState()
    data object Loading : OnboardState()
    data class Error(val message: String) : OnboardState()
}