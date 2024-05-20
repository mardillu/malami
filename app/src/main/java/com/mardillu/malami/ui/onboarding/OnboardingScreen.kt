package com.mardillu.malami.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.malami.utils.ShowErrorDialog
import com.mardillu.malami.utils.ShowToast

/**
 * Created on 19/05/2024 at 7:35 pm
 * @author mardillu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navigation: AppNavigation,
    viewModel: OnboardingViewModel,
    ) {
    val showSuccessToast by remember { mutableStateOf(viewModel.showSuccessToast) }
    val showErrorDialog by remember { mutableStateOf(viewModel.showErrorDialog) }
    val errorMessage by remember { mutableStateOf(viewModel.errorMessage) }

    if (showSuccessToast) {
        ShowToast("Preferences saved successfully")
        viewModel.showSuccessToast = false
    }

    if (showErrorDialog) {
        ShowErrorDialog(message = errorMessage) {
            viewModel.showErrorDialog = false
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Onboarding Questionnaire") }
            )
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Preferred Learning Style:")
                            RadioButtonGroup(
                                options = listOf("Visual", "Auditory", "Kinesthetic", "Reading/Writing"),
                                selectedOption = viewModel.learningStyle,
                                onOptionSelected = { viewModel.learningStyle = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Pace of Learning:")
                            RadioButtonGroup(
                                options = listOf("Fast", "Medium", "Slow"),
                                selectedOption = viewModel.paceOfLearning,
                                onOptionSelected = { viewModel.paceOfLearning = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Preferred Study Time:")
                            RadioButtonGroup(
                                options = listOf("Morning", "Afternoon", "Evening", "Night"),
                                selectedOption = viewModel.studyTime,
                                onOptionSelected = { viewModel.studyTime = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Preferred Study Environment:")
                            RadioButtonGroup(
                                options = listOf("Quiet", "Background Music", "Interactive"),
                                selectedOption = viewModel.studyEnvironment,
                                onOptionSelected = { viewModel.studyEnvironment = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Preferred Device for Study:")
                            RadioButtonGroup(
                                options = listOf("Desktop/Laptop", "Tablet", "Mobile Phone"),
                                selectedOption = viewModel.deviceUsage,
                                onOptionSelected = { viewModel.deviceUsage = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Preferred Content Format:")
                            RadioButtonGroup(
                                options = listOf("Text-based", "Multimedia", "Interactive"),
                                selectedOption = viewModel.contentFormat,
                                onOptionSelected = { viewModel.contentFormat = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Reading Speed:")
                            RadioButtonGroup(
                                options = listOf("Fast", "Medium", "Slow"),
                                selectedOption = viewModel.readingSpeed,
                                onOptionSelected = { viewModel.readingSpeed = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Difficulty Level of Reading Material:")
                            RadioButtonGroup(
                                options = listOf("Beginner", "Intermediate", "Advanced"),
                                selectedOption = viewModel.difficultyLevel,
                                onOptionSelected = { viewModel.difficultyLevel = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Topics of Interest:")
                            TextField(
                                value = viewModel.topicsOfInterest,
                                onValueChange = { viewModel.topicsOfInterest = it },
                                placeholder = { Text("Enter topics") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Learning Goals:")
                            TextField(
                                value = viewModel.learningGoals,
                                onValueChange = { viewModel.learningGoals = it },
                                placeholder = { Text("Enter goals") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Prior Knowledge:")
                            RadioButtonGroup(
                                options = listOf("Beginner", "Intermediate", "Advanced"),
                                selectedOption = viewModel.priorKnowledge,
                                onOptionSelected = { viewModel.priorKnowledge = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Preferred Feedback Type:")
                            RadioButtonGroup(
                                options = listOf("Immediate", "Delayed"),
                                selectedOption = viewModel.feedbackType,
                                onOptionSelected = { viewModel.feedbackType = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Assessment Preferences:")
                            RadioButtonGroup(
                                options = listOf("Multiple-choice quizzes", "Short-answer questions", "Practical assignments", "Projects"),
                                selectedOption = viewModel.assessmentPreferences,
                                onOptionSelected = { viewModel.assessmentPreferences = it }
                            )
                        }
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Special Requirements:")
                            TextField(
                                value = viewModel.specialRequirements,
                                onValueChange = { viewModel.specialRequirements = it },
                                placeholder = { Text("Enter special requirements") },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.savePreferences()
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    )
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(option)
            }
        }
    }
}