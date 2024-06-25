package com.mardillu.malami.ui.courses.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.malami.BuildConfig
import com.mardillu.simple_image_generator.drawTextAsImage

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
    val cacheDir = context.filesDir

    LaunchedEffect(true) {
        if(!viewModel.isPlaying) {
            viewModel.loadCourseAudios(courseId!!, cacheDir)
        }
        startService()
    }

    LaunchedEffect(Unit) {
        viewModel.setTtsApiKey(BuildConfig.CLOUD_SPEECH_API_KEY)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MusicPlayerScreen(viewModel)
    }
}

@Composable
fun MusicPlayerScreen(viewModel: AudioPlayerViewModel) {
    val newProgressValue = remember { mutableFloatStateOf(0f) }
    val useNewProgressValue = remember { mutableStateOf(false) }
    val mediaItemState = viewModel.mediaItemState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(0.dp))
        Text(
            text = "Playing from course",
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Text(
            text = mediaItemState.value.mediaMetadata.albumTitle?.toString() ?: "-",
            fontSize = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            bitmap = drawTextAsImage(
                text =  mediaItemState.value.mediaMetadata.title?.toString() ?: "-",
                isDarkTheme = isSystemInDarkTheme()
            ).asImageBitmap(),
            contentDescription = "Audio Art",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),

        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = mediaItemState.value.mediaMetadata.title?.toString() ?: "-",
            fontSize = 24.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = mediaItemState.value.mediaMetadata.displayTitle?.let {
                "Section: $it"
            } ?: "-",
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.onUIEvent(UIEvent.Backward) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { viewModel.onUIEvent(UIEvent.PlayPause) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                    .padding(16.dp)
                    .fillMaxWidth(fraction = 0.6f)
            ) {
                Icon(
                    imageVector = if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (viewModel.isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { viewModel.onUIEvent(UIEvent.Forward) },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = MaterialTheme.colorScheme.onSecondary,
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
            Text(text = viewModel.progressString, fontSize = 12.sp)
            Text(text = viewModel.remainderString, fontSize = 12.sp)
        }

        Slider(
            value = if (useNewProgressValue.value) newProgressValue.floatValue else viewModel.progress,
            onValueChange = { newValue ->
                useNewProgressValue.value = true
                newProgressValue.floatValue = newValue
                viewModel.onUIEvent(UIEvent.UpdateProgress(newProgress = newValue))
            },
            onValueChangeFinished = {
                useNewProgressValue.value = false
            },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CustomDragHandle(onClose: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onClose() }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Hide modal",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}