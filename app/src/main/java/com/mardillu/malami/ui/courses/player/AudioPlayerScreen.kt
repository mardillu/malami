package com.mardillu.malami.ui.courses.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Created on 08/06/2024 at 10:39 pm
 * @author mardillu
 */
@Composable
fun AudioPlayerScreen(
    courseId: String?,
    sectionId: String?,
    moduleId: String?,
    startService: () -> Unit,
    viewModel: AudioPlayerViewModel
) {
    val context = LocalContext.current
    val cacheDir = context.cacheDir

    LaunchedEffect(true) {
        viewModel.loadCourseAudios(courseId!!, cacheDir)
        startService()
    }
    var isExpanded by rememberSaveable { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isExpanded) {
            MusicPlayerScreen(viewModel)
        } else {
           /// MiniPlayer(viewModel) { isExpanded = true }
        }
    }
}


@Composable
fun MusicPlayerScreen(viewModel: AudioPlayerViewModel) {
    val newProgressValue = remember { mutableStateOf(0f) }
    val useNewProgressValue = remember { mutableStateOf(false) }
    val mediaItemState = viewModel.mediaItemState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = "Audio Art",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color.Gray, RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = mediaItemState.value.mediaMetadata.title?.toString() ?: "-",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = mediaItemState.value.mediaMetadata.displayTitle?.toString() ?: "-",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = mediaItemState.value.mediaMetadata.albumTitle?.toString() ?: "-",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.onUIEvent(UIEvent.Backward) }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = { viewModel.onUIEvent(UIEvent.PlayPause) },
                modifier = Modifier
                    .background(Color(0xFF7B61FF), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (viewModel.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = { viewModel.onUIEvent(UIEvent.Forward) }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = viewModel.progressString, color = Color.Gray, fontSize = 12.sp)
            Text(text = viewModel.remainderString, color = Color.Gray, fontSize = 12.sp)
        }
//        Slider(
//            value = progress.position.toFloat(),
//            onValueChange = { viewModel.seekTo(it) },
//            modifier = Modifier.fillMaxWidth(),
//            valueRange = 0f..progress.duration.toFloat(),
//            colors = SliderDefaults.colors(
//                thumbColor = Color(0xFF7B61FF),
//                activeTrackColor = Color(0xFF7B61FF),
//                inactiveTrackColor = Color.Gray
//            )
//        )

        Slider(
            value = if (useNewProgressValue.value) newProgressValue.value else viewModel.progress,
            onValueChange = { newValue ->
                useNewProgressValue.value = true
                newProgressValue.value = newValue
                viewModel.onUIEvent(UIEvent.UpdateProgress(newProgress = newValue))
            },
            onValueChangeFinished = {
                useNewProgressValue.value = false
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun BottomNavigation() {
    BottomAppBar(
        //containerColor = Color.Black,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        IconButton(onClick = { /* TODO: Add menu action */ }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.Gray)
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* TODO: Add fullscreen action */ }) {
            Icon(imageVector = Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.Gray)
        }
        IconButton(onClick = { /* TODO: Add shuffle action */ }) {
            Icon(imageVector = Icons.Default.Shuffle, contentDescription = "Shuffle", tint = Color.Gray)
        }
        IconButton(onClick = { /* TODO: Add favorite action */ }) {
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorite", tint = Color.Gray)
        }
        IconButton(onClick = { /* TODO: Add playlist action */ }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = "Playlist", tint = Color.Gray)
        }
    }
}