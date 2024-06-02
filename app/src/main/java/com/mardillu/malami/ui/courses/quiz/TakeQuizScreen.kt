package com.mardillu.malami.ui.courses.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mardillu.malami.data.model.course.QuizAttempt
import com.mardillu.malami.ui.courses.list.CourseListViewModel
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.malami.ui.onboarding.RadioButtonGroup
import com.mardillu.malami.utils.AppAlertDialog
import kotlinx.coroutines.flow.collectLatest

/**
 * Created on 28/05/2024 at 4:56 pm
 * @author mardillu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeQuizScreen(
    navigation: AppNavigation,
    viewModel: CourseListViewModel,
    quizViewModel: TakeQuizViewModel,
    courseId: String?,
    sectionId: String?,
) {

    val quizUiState by quizViewModel.quizUiState.collectAsState()
    val courseList by viewModel.courseListState.collectAsState()
    val selectedAnswers by quizViewModel.selectedAnswers.collectAsState()
    val course = courseList.find { it.id == courseId }
    val section = course?.sections?.find { it.id == sectionId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skill Check") }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                contentColor =  Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        quizViewModel.onSubmitQuizClicked(sectionId!!, courseId!!, section?.quiz?: emptyList())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = quizUiState != UIState.Loading
                ) {
                    Text("Submit")
                }
            }
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(section?.quiz?: emptyList()) { i, question ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(question.question)
                            RadioButtonGroup(
                                options = question.options,
                                selectedOption = selectedAnswers[question.id],
                                onOptionSelected = { quizViewModel.setAnswer(question.id, it) }
                            )
                        }
                    }
                }
            }
        }
    )

    when (quizUiState) {
        is UIState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UIState.Success<*> -> {
            val attempt = (quizUiState as UIState.Success<*>).data as QuizAttempt
            navigation.goToQuizResult(attempt.passed, attempt.obtainablePoints, attempt.obtainedPoints)
        }

        is UIState.Error -> {
            AppAlertDialog(
                dialogText = (quizUiState as UIState.Error).message,
                onDismissRequest = {
                    quizViewModel.setUIStateIdle()
                },
                onConfirmation = {
                    quizViewModel.setUIStateIdle()
                },
                icon = Icons.Default.Info
            )
        }

        UIState.Idle -> {}
    }
}