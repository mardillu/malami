package com.mardillu.malami.ui.courses.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.ui.navigation.AppNavigation
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.mardillu.malami.BuildConfig
import com.mardillu.malami.ui.onboarding.OnboardState
import com.mardillu.malami.ui.onboarding.RadioButtonGroup
import com.mardillu.malami.utils.AppAlertDialog

/**
 * Created on 20/05/2024 at 10:47 pm
 * @author mardillu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(navigation: AppNavigation,
                       viewModel: CreateCourseViewModel
) {

    val focusManager = LocalFocusManager.current
    var subject by remember { mutableStateOf("") }
    var learningGoals by remember { mutableStateOf("") }
    var priorKnowledge by remember { mutableStateOf("") }

    val createCourseState by viewModel.createCourseState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Course") },
                navigationIcon = {
                    IconButton(onClick = { navigation.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
                        viewModel.onCreateCourseClicked(subject, priorKnowledge, learningGoals, BuildConfig.GEMINI_API_KEY)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Create Course")
                }
            }
        },
        content = {
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Text(
                        text = "What do you want to learn?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    TextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(
                            FocusDirection.Down) }),
                        colors = TextFieldDefaults.colors(
                            //backgroundColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Text(
                        text = "State your learning goals for this subject",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    TextField(
                        value = learningGoals,
                        onValueChange = { learningGoals = it },
                        label = { Text("Learning Goals") },
                        modifier = Modifier.fillMaxWidth()
                            .height(150.dp),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = TextFieldDefaults.colors(
                            //backgroundColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = false,
                        maxLines = 5
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = "Prior Knowledge:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
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
                            //Text("Prior Knowledge:")
                            RadioButtonGroup(
                                options = listOf("Beginner", "Intermediate", "Advanced"),
                                selectedOption = priorKnowledge,
                                onOptionSelected = { priorKnowledge = it }
                            )
                        }
                    }
                }
            }

            when (createCourseState) {
                CreateCourseState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CreateCourseState.Success -> {
                    Text(
                        text = "Course created successfully!",
                        color = Color.Green,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                is CreateCourseState.Error -> {
                    AppAlertDialog(
                        dialogText = "Error creating course: ${(createCourseState as CreateCourseState.Error).message}",
                        onDismissRequest = {
                            viewModel.setCreateCourseStateIdle()
                        },
                        onConfirmation = {
                            viewModel.setCreateCourseStateIdle()
                        },
                        icon = Icons.Default.Info
                    )
                }
                else -> {}
            }
        }
    )
}


