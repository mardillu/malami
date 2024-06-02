package com.mardillu.malami.ui.courses.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mardillu.malami.data.model.course.Quiz
import com.mardillu.malami.data.model.course.QuizAttempt
import com.mardillu.malami.data.repository.CoursesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created on 28/05/2024 at 5:18 pm
 * @author mardillu
 */
@HiltViewModel
class TakeQuizViewModel@Inject constructor(
    private val coursesRepository: CoursesRepository
) : ViewModel() {
    //var selectedAnswers by mutableStateOf<SnapshotStateMap<String, String>>(mutableStateMapOf())
    private val _selectedAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedAnswers: StateFlow<Map<String, String>> get() = _selectedAnswers
    protected val _quizUiState = MutableStateFlow<UIState>(UIState.Idle)
    val quizUiState: StateFlow<UIState> get() = _quizUiState
    protected val _quizAttempts = MutableStateFlow<List<QuizAttempt>>(emptyList())

    fun setAnswer(id: String, answer: String) {
        _selectedAnswers.update {
            it + (id to answer)
        }
    }

    private fun calculateScore(attemptNumber: Int, questions: List<Quiz>): Double {
        val score = questions.count { it.answer == _selectedAnswers.value[it.id] }
        return if (attemptNumber == 1) score.toDouble() else score * 0.8
    }

    private fun passedQuiz(obtainedPoints: Double, obtainablePoints: Int): Pair<Boolean, Double> {
        val fraction = (obtainedPoints / obtainablePoints)
        val passed = fraction >= 0.7
        return Pair(passed, fraction)
    }

    fun onSubmitQuizClicked(sectionId: String, courseId: String, questions: List<Quiz>) {
        val anyUnanswered = _selectedAnswers.value.size < questions.size
        if (anyUnanswered) {
            _quizUiState.value = UIState.Error("Answer all questions")
            return
        }
        _quizUiState.value = UIState.Loading
        getQuizAttempts(sectionId, courseId, questions)
    }

    fun getQuizAttempts(sectionId: String, courseId: String, questions: List<Quiz>) {
        viewModelScope.launch {
            val quizAttempts = coursesRepository.getQuizAttempts()
            quizAttempts.getOrNull()?.let { attempts ->
                saveAnswers(attempts, sectionId, courseId, questions)
            } ?: run {
                _quizUiState.update {
                    UIState.Error("Failed to get courses")
                }
            }
        }
    }

    fun saveAnswers(prevAttempts: List<QuizAttempt>, sectionId: String, courseId: String, questions: List<Quiz>) {
        viewModelScope.launch {
            _quizUiState.update {
                UIState.Loading
            }
            val score = calculateScore(prevAttempts.size + 1, questions)
            val scoreBreakdown = passedQuiz(score, questions.size)
            val quizAttempt = QuizAttempt(
                sectionId = sectionId,
                courseId = courseId,
                obtainablePoints = _selectedAnswers.value.size.toLong(),
                obtainedPoints = score,
                passed = scoreBreakdown.first,
                fraction = scoreBreakdown.second
            )

            runCatching {
                val saveResult = coursesRepository.saveQuizAttempt(quizAttempt)
                if (saveResult.isSuccess) {
                    _quizUiState.update {
                        UIState.Success(quizAttempt)
                    }
                } else {
                    throw IllegalStateException("Failed to save course")
                }
            }.onFailure {
                _quizUiState.value = UIState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun setUIStateIdle() {
        _quizUiState.update {
            UIState.Idle
        }
    }
}

sealed class UIState {
    data class Success<T>(val data: T?): UIState()
    data object Idle : UIState()
    data object Loading : UIState()
    data class Error(val message: String) : UIState()
}