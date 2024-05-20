package com.mardillu.malami.ui.courses.course_modules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.R
import com.mardillu.malami.ui.navigation.AppNavigation

/**
 * Created on 20/05/2024 at 12:33 pm
 * @author mardillu
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleListScreen(
    navigation: AppNavigation,
    courseId: String
) {
    val modules = listOf(
        ModuleItem(
            "Introduction",
            "5 mins",
            true,
            painterResource(id = R.drawable.img)
        ),
        ModuleItem(
            "Chapter 1: Basics",
            "15 mins",
            false,
            painterResource(id = R.drawable.img)
        ),
        ModuleItem(
            "Chapter 2: Advanced",
            "20 mins",
            false,
            painterResource(id = R.drawable.img)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(courseId) },
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
                items(modules) { module ->
                    ModuleListItem(module, onClick = {
                        navigation.goToModuleContent("1", "2")
                    })
                }
            }
        }
    )
}

@Composable
fun ModuleListItem(module: ModuleItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = module.image,
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = module.timeToRead,
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
                            if (module.isCompleted) Color.Green else Color.Gray,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (module.isCompleted) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                        )
                    }
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