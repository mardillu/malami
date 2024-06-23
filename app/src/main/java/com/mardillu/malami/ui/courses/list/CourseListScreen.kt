package com.mardillu.malami.ui.courses.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mardillu.malami.R
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.ui.common.ui.EmptyState
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.simple_image_generator.drawTextAsImage

/**
 * Created on 20/05/2024 at 11:04 am
 * @author mardillu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(
    navigation: AppNavigation,
    viewModel: CourseListViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {
                        navigation.gotoCreateCourse()
                    }) {
                        Icon(Icons.Filled.Add, contentDescription = "New course")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navigation.gotoCreateCourse()
                },
                icon = { Icon(Icons.Filled.Edit, "New course button.") },
                text = { Text(text = "New Course") },
            )
        },
        content = {
            CourseListContent(it, navigation, viewModel)
        }
    )
}

@Composable
fun CourseListContent(
    paddingValues: PaddingValues,
    navigation: AppNavigation,
    viewModel: CourseListViewModel
) {
    val courseList by viewModel.courseListState.collectAsState()
    val ongoingCourseListState by viewModel.ongoingCourseListState.collectAsState()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var selectedViewType by rememberSaveable { mutableStateOf(viewModel.getCourseListViewStyle()) }

    if (showDialog) {
        OptionsDialog(
            selectedViewType = selectedViewType,
            onDismissRequest = { showDialog = false },
            onViewTypeSelected = {
                selectedViewType = it
                viewModel.setCourseListViewStyle(it)
            },
        )
    }

    if (courseList.isEmpty()) {
        EmptyState(
            title = "You don't have any courses yet",
            description = "Let's change that. Click the button below to create a new course",
            buttonText = "New course",
            onButtonClick = {
                navigation.gotoCreateCourse()
            }
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {

            if (ongoingCourseListState.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Ongoing Courses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ongoingCourseListState.forEach { course ->
                                CurrentCourseCard(course.first, course.second, navigation)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "All Courses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_view_list_24),
                            contentDescription = "Options"
                        )
                    }
                }
            }

            when (selectedViewType) {
                CourseListViewType.Grid, CourseListViewType.Comfortable -> {
                    items(
                        courseList.chunked(
                            if (selectedViewType == CourseListViewType.Grid) 2 else 1
                        )
                    ) { rowCourses ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowCourses.forEach { course ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    CourseListItem(course, navigation)
                                }
                            }
                        }
                    }
                }

                CourseListViewType.Compact -> {
                    items(courseList.chunked(1)) { rowCourses ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowCourses.forEach { course ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    CompactCourseListItem(course, navigation)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactCourseListItem(course: Course, navigation: AppNavigation,) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigation.goToModuleList(course.id) },
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = drawTextAsImage(
                    text = course.title,
                    isDarkTheme = isSystemInDarkTheme()
                ).asImageBitmap(),
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
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = course.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Listen",
                tint = Color.Gray
            )
        }
    }
}


@Composable
fun CourseListItem(course: Course, navigation: AppNavigation,) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navigation.goToModuleList(course.id)
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Image(
                bitmap = drawTextAsImage(
                    text = course.title,
                    isDarkTheme = isSystemInDarkTheme()
                ).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = course.shortDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CurrentCourseCard(course: Course, progress: Float, navigation: AppNavigation) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(180.dp)
            .height(100.dp)
            .clickable {
                navigation.goToModuleList(course.id)
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
               bitmap = drawTextAsImage(
                   text = course.title,
                   isDarkTheme = isSystemInDarkTheme()
               ).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = course.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = Color.Gray,
                )
            }
        }
    }
}

@Composable
fun OptionsDialog(
    selectedViewType: CourseListViewType,
    onDismissRequest: () -> Unit,
    onViewTypeSelected: (CourseListViewType) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Display Style", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                CourseListViewType.entries.forEach { viewType ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onViewTypeSelected(viewType); onDismissRequest() }
                    ) {
                        RadioButton(
                            selected = viewType == selectedViewType,
                            onClick = {
                                onViewTypeSelected(viewType)
                                onDismissRequest()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = viewType.name)
                    }
                }
            }
        }
    }
}

enum class CourseListViewType {
    Grid,
    Comfortable,
    Compact
}