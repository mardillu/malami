package com.mardillu.malami.ui.courses.player

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.mardillu.malami.ui.service.AudioPlaybackProgress
import com.mardillu.malami.ui.service.AudioPlayerService

/**
 * Created on 08/06/2024 at 10:39 pm
 * @author mardillu
 */
@Composable
fun AudioPlayerScreen(
    audioIndex: String?,
    audioPlayerService: AudioPlayerService,
    viewModel: AudioPlayerViewModel
) {
    val isPlaying by audioPlayerService.isPlaying.collectAsState(initial = false)
    val progress by audioPlayerService.progress.collectAsState(initial = AudioPlaybackProgress())
    var isExpanded by rememberSaveable { mutableStateOf(true) }

//    val exoPlayer = remember {
//        ExoPlayer.Builder(context).build().apply {
//            val mediaItem = MediaItem.fromUri("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3")
//            addMediaItem(mediaItem)
//            prepare()
//            playWhenReady = true
//        }
//    }
//
//    DisposableEffect(exoPlayer) {
//        onDispose {
//            exoPlayer.release()
//        }
//    }

    // Example list of audios
    val audioList = listOf(
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
    )

    LaunchedEffect(Unit) {
        viewModel.setAudioList(audioList, audioIndex?.toIntOrNull() ?: 0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isExpanded) {
            MusicPlayerScreen(viewModel, audioPlayerService, isPlaying, progress)
        } else {
            MiniPlayer(viewModel) { isExpanded = true }
        }
    }
}

@Composable
fun PlayerScreen(viewModel: AudioPlayerViewModel) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentAudioIndex by viewModel.currentAudioIndex.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Song Name", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Text("Artist Name", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { viewModel.playPrevious() }) {
                Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
            }

            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White
                )
            }

            IconButton(onClick = { viewModel.playNext() }) {
                Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
            }
        }


        Slider(
            value = 0.5f,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
        // Slider and additional controls
    }
}

@Composable
fun MiniPlayer(viewModel: AudioPlayerViewModel, onExpand: () -> Unit) {
    val isPlaying by viewModel.isPlaying.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(8.dp)
            .clickable { onExpand() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.DarkGray, shape = RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("Song Name", color = Color.White)
            Text("Artist Name", color = Color.Gray, style = MaterialTheme.typography.titleSmall)
        }

        IconButton(onClick = { viewModel.togglePlayPause() }) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White
            )
        }

        IconButton(onClick = { /* Handle next */ }) {
            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
        }
    }
}


@Composable
fun MusicPlayerScreen(viewModel: AudioPlayerViewModel, audioPlayerService: AudioPlayerService, isPlaying: Boolean, progress: AudioPlaybackProgress) {
    //val isPlaying by viewModel.isPlaying.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.CenterHorizontally)
                .background(Color.Gray, RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Song Name",
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Artist Name",
            color = Color.Gray,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.playPrevious() }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier
                    .background(Color(0xFF7B61FF), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = { viewModel.playNext()}) {
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
            Text(text = progress.timeElapsed, color = Color.Gray, fontSize = 12.sp)
            Text(text = progress.timeRemaining, color = Color.Gray, fontSize = 12.sp)
        }
        Slider(
            value = progress.position.toFloat(),
            onValueChange = { viewModel.seekTo(it) },
            modifier = Modifier.fillMaxWidth(),
            valueRange = 0f..progress.duration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF7B61FF),
                activeTrackColor = Color(0xFF7B61FF),
                inactiveTrackColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        //BottomNavigation()
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
            Icon(imageVector = Icons.Default.PlaylistPlay, contentDescription = "Playlist", tint = Color.Gray)
        }
    }
}