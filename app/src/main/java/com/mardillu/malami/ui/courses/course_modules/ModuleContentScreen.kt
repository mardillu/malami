package com.mardillu.malami.ui.courses.course_modules

/**
 * Created on 20/05/2024 at 12:38 pm
 * @author mardillu
 */
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mardillu.malami.ui.navigation.AppNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleContentScreen(
    navigation: AppNavigation,
    moduleId: String,
    ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(moduleId) },
                navigationIcon = {
                    IconButton(onClick = { navigation.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            ModuleContent(moduleId, it)
        }
    )
}

@Composable
fun ModuleContent(moduleId: String, it: PaddingValues) {
    Column(modifier = Modifier.padding(it).padding(16.dp)) {
        Text(
            text = moduleId,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Module content goes here. This is where the detailed content of the module will be displayed.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
            onClick = { /* Handle listen action */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Listen to Module")
        }
    }
}