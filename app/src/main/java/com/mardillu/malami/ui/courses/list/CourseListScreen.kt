package com.mardillu.malami.ui.courses.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.R
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.ui.auth.AuthViewModel
import com.mardillu.malami.ui.navigation.AppNavigation

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
                title = { Text("Travel Inspiration") },
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null
                    )
                },
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

    val items = listOf(
        TravelItem(
            painterResource(id = R.drawable.img),
            "Finding Serenity",
            "10 Tranquil Escapes for the Soul"
        ),
        TravelItem(
            painterResource(id = R.drawable.img),
            "List item title",
            "List item subtitle"
        ),
        TravelItem(
            painterResource(id = R.drawable.img),
            "The Art of Slow Travel",
            "Embrace the Journey, Not Just the Destination"
        )
    )

    val currentCourses = listOf(
        CurrentCourse(
            painterResource(id = R.drawable.img),
            "Course Title 1",
            0.7f
        ),
        CurrentCourse(
            painterResource(id = R.drawable.img),
            "Course Title 2",
            0.3f
        ),
        CurrentCourse(
            painterResource(id = R.drawable.img),
            "Currently Taking Course Title 3",
            0.5f
        )
    )

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(paddingValues)
    ) {
        item {
            Text(
                text = "Currently Taking",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                currentCourses.forEach { course ->
                    CurrentCourseCard(course)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Text(
                text = "All Courses",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(courseList) { course ->
            TravelListItem(course, navigation)
        }
    }
}

@Composable
fun TravelListItem(course: Course, navigation: AppNavigation,) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
            .clickable {
                navigation.goToModuleList("1")
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)),
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
fun CurrentCourseCard(course: CurrentCourse) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(200.dp)
            .height(150.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = course.image,
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
                    progress = { course.progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Green,
                    trackColor = Color.Gray,
                )
            }
        }
    }
}

data class TravelItem(
    val image: Painter,
    val title: String,
    val subtitle: String
)

data class CurrentCourse(
    val image: Painter,
    val title: String,
    val progress: Float
)