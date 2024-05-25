package com.mardillu.malami.ui.courses.course_modules

/**
 * Created on 20/05/2024 at 12:38 pm
 * @author mardillu
 */
import android.widget.TextView
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mardillu.malami.data.model.course.Module
import com.mardillu.malami.ui.courses.list.CourseListViewModel
import com.mardillu.malami.ui.navigation.AppNavigation
import io.noties.markwon.Markwon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleContentScreen(
    navigation: AppNavigation,
    moduleId: String,
    sectionId: String,
    courseId: String,
    viewModel: CourseListViewModel
) {
    val courseList by viewModel.courseListState.collectAsState()
    val course = courseList.firstOrNull { it.id == courseId }
    val section = course?.sections?.firstOrNull { it.id == sectionId }
    val moduleIndex = section?.modules?.indexOfLast { it.id == moduleId }
    val module = section?.modules?.getOrNull(moduleIndex ?: 0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = module?.title ?: "",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigation.back() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = {
            if (course != null && section != null && module != null) {
                viewModel.updateModuleCompletedStatusById(courseId, sectionId, moduleId, true)
                ModuleContent(moduleIndex!!, module, it)
            }
        }
    )
}

@Composable
fun ModuleContent(moduleIndex: Int, module: Module, it: PaddingValues) {
    Column(modifier = Modifier.padding(it).padding(16.dp)) {
        Text(
            text = "Module $moduleIndex",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        MarkdownText(
            text = module.content,
            //style = MaterialTheme.typography.bodyMedium,
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

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }

    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                markwon.setMarkdown(this, text)
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}