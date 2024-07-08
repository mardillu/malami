package com.mardillu.malami.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.malami.utils.AppAlertDialog

/**
 * Created on 19/05/2024 at 7:35 pm
 * @author mardillu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navigation: AppNavigation,
    viewModel: OnboardingViewModel,
    onSubmit: (() -> Unit)? = null
    ) {

    val createCourseState by viewModel.onboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning style") },
                navigationIcon = {
                    if(onSubmit != null) {
                        IconButton(onClick = {
                            navigation.back()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.savePreferences()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = createCourseState != OnboardState.Loading
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
                                options = listOf(
                                    "Visual",
                                    "Auditory",
                                    "Kinesthetic",
                                    "Reading/Writing"
                                ),
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
            }
        }
    )

    when (createCourseState) {
        is OnboardState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is OnboardState.Success -> {
            onSubmit?.invoke()
        }

        is OnboardState.Error -> {
            AppAlertDialog(
                dialogText = (createCourseState as OnboardState.Error).message,
                onDismissRequest = {
                    viewModel.setOnboardStateIdle()
                },
                onConfirmation = {
                    viewModel.setOnboardStateIdle()
                },
                icon = Icons.Default.Info
            )
        }

        OnboardState.Idle -> {}
    }
}

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
                    .clickable { onOptionSelected(option) }
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