package com.mardillu.malami.ui.courses.course_modules

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.R
import com.mardillu.malami.data.model.course.Module
import com.mardillu.malami.ui.courses.list.CourseListViewModel
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.malami.ui.theme.Purple40
import com.mardillu.malami.ui.theme.Purple80
import com.mardillu.malami.ui.theme.PurpleGrey40

/**
 * Created on 20/05/2024 at 12:33 pm
 * @author mardillu
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleListScreen(
    navigation: AppNavigation,
    courseId: String,
    viewModel: CourseListViewModel
) {
    val courseList by viewModel.courseListState.collectAsState()
    val course = courseList.firstOrNull { it.id == courseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course?.title ?: "",
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                if (course != null) {
                    itemsIndexed(course.sections) {sectionIndex, section ->
                        ExpandableListItem(
                            section.title,
                            section.modules,
                            course.title,
                            courseId,
                            section.id,
                            sectionIndex,
                            navigation
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ExpandableListItem(
    title: String,
    modules: List<Module>,
    courseTitle: String,
    courseId: String,
    sectionId: String,
    sectionIndex: Int,
    navigation: AppNavigation
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        //border = BorderStroke(0.5.dp, color = Purple40)
    ) {
        Column(
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Purple80),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Add some space between the title and action text
                    TextButton(onClick = {  expanded = !expanded }) {
                        Text(
                            text = if (expanded) stringResource(R.string.hide) else stringResource(
                                R.string.show
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .then(
                                    if (expanded) Modifier.rotate(
                                        180f
                                    ) else Modifier
                                ),
                        )
                    }
                }

                if (expanded) {
                    modules.forEachIndexed {i, module ->
                        val moduleActive = if (module.completed || (sectionIndex ==0 && i == 0))
                            true
                        else {
                            i != 0 && modules[i-1].completed
                        }
                        ModuleListItem(module, moduleActive, onClick = {
                            if (moduleActive) {
                                navigation.goToModuleContent(courseId, module.id, sectionId)
                            }
                        })
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 0.dp, horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Purple80
                        )
                    }
                    //add the quiz module
                    ModuleListItem(
                        Module(
                            title = "Section Quiz",
                            shortDescription = "Take the quiz to complete this section and move on to the next",
                            id = "",
                            content = ""
                        ), modules[modules.size-1].completed, onClick = {
                            //navigation.gotoQuiz(courseTitle, "3")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModuleListItem(module: Module, isModuleActive: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = module.title,
                style = if (isModuleActive) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                fontWeight = if (isModuleActive) FontWeight.Bold else FontWeight.Normal,
            )
            Text(
                text = "5 Mins",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(onClick = { /* Handle listen action */ }) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Listen", tint = Color.Gray)
        }
        IconButton(onClick = { /* Handle module completion */ }) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (module.completed) Purple40 else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (module.completed) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

data class ModuleItem(
    val title: String,
    val timeToRead: String,
    val isCompleted: Boolean,
    val image: Painter
)