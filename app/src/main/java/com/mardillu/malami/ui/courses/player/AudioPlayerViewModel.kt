package com.mardillu.malami.ui.courses.player

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.mardillu.malami.data.PreferencesManager
import com.mardillu.malami.data.model.course.Course
import com.mardillu.malami.data.model.course.ModuleAudio
import com.mardillu.malami.data.repository.AudioRepository
import com.mardillu.malami.data.repository.CoursesRepository
import com.mardillu.malami.utils.addAll
import com.mardillu.malami.work.TextToSpeechWorker
import com.mardillu.player_service.service.AudioPlayerServiceHandler
import com.mardillu.player_service.service.AudioPlayerState
import com.mardillu.player_service.service.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.truncate

@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val serviceHandler: AudioPlayerServiceHandler,
    private val preferencesManager: PreferencesManager,
    private val audioRepository: AudioRepository,
    private val coursesRepository: CoursesRepository,
    private val workManager: WorkManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _audioFiles = MutableStateFlow<List<ModuleAudio>>(emptyList())
    val audioFiles: StateFlow<List<ModuleAudio>> get() = _audioFiles

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses

    var duration by savedStateHandle.saveable { mutableStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableStateOf(0f) }
    var remainder by savedStateHandle.saveable { mutableStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var remainderString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(serviceHandler.isPlaying()) }

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState = _uiState.asStateFlow()

    private val _mediaItemState = MutableStateFlow(MediaItem.EMPTY)
    val mediaItemState = _mediaItemState.asStateFlow()

    private val _ttsApiKey = MutableStateFlow("")
    val ttsApiKey: StateFlow<String> get() = _ttsApiKey

    init {
        viewModelScope.launch {
            serviceHandler.audioPlayerState.collect { mediaState ->
                when (mediaState) {
                    is AudioPlayerState.Buffering -> calculateProgressValues(mediaState.progress)
                    AudioPlayerState.Initial -> _uiState.value = UIState.Initial
                    is AudioPlayerState.Playing -> isPlaying = mediaState.isPlaying
                    is AudioPlayerState.Progress -> calculateProgressValues(mediaState.progress)
                    is AudioPlayerState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UIState.Ready
                    }
                    is AudioPlayerState.MediaItemTransition -> {
                        updateModuleCompletedStatusById(
                            mediaState.mediaItem?.mediaMetadata?.extras?.getString("courseId") as String,
                            mediaState.mediaItem?.mediaMetadata?.extras?.getString("sectionId") as String,
                            mediaState.mediaItem?.mediaMetadata?.extras?.getString("moduleId") as String,
                            true
                        )
                        //Log.d("AudioPlayerViewModel", "MediaItemTransition, ${mediaState.mediaItem?.mediaMetadata?.title.toString()}")
                    }
                }
            }
        }
        viewModelScope.launch {
            serviceHandler.nowPlayingModule.collect {
                _mediaItemState.value = it
            }
        }
    }

    fun setTtsApiKey(apiKey: String) {
        _ttsApiKey.update { apiKey }
    }

    override fun onCleared() {
        viewModelScope.launch {
            serviceHandler.onPlayerEvent(PlayerEvent.Stop)
        }
    }

    private fun updateModuleCompletedStatusById(courseId: String, sectionId: String, moduleId: String, completed: Boolean) {
        viewModelScope.launch {
            coursesRepository.updateModuleCompletedStatusById(courseId, sectionId, moduleId, completed)
        }
    }

    fun onUIEvent(uiEvent: UIEvent) = viewModelScope.launch {
        when (uiEvent) {
            UIEvent.Backward -> serviceHandler.onPlayerEvent(PlayerEvent.Backward)
            UIEvent.Forward -> serviceHandler.onPlayerEvent(PlayerEvent.Forward)
            UIEvent.PlayPause -> serviceHandler.onPlayerEvent(PlayerEvent.PlayPause)
            is UIEvent.UpdateProgress -> {
                progress = uiEvent.newProgress
                serviceHandler.onPlayerEvent(
                    PlayerEvent.UpdateProgress(
                        uiEvent.newProgress
                    )
                )
            }
        }
    }

    private fun formatDuration(duration: Long): String {
        val minutes: Long = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds: Long = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS)
                - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun calculateProgressValues(currentProgress: Long) {
        progress = if (currentProgress > 0) (currentProgress.toFloat() / duration) else 0f
        remainder = 1 - progress
        progressString = formatDuration(currentProgress)
        remainderString = formatDuration(duration - currentProgress)
    }

    private fun loadData() {
        val mediaItemList = mutableListOf<MediaItem>()
        _audioFiles.value.sortedBy { it.sequence }.forEach {
            mediaItemList.add(
                MediaItem.Builder()
                    .setUri(it.audioUri)
                    .setMediaMetadata(MediaMetadata.Builder()
                        .setIsBrowsable(true)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS)
                        .setArtworkUri(Uri.parse("https://cdns-images.dzcdn.net/images/cover/1fddc1ab0535ee34189dc4c9f5f87bf9/264x264.jpg"))
                        .setAlbumTitle(it.courseTitle)
                        .setDisplayTitle(it.sectionTitle)
                        .setSubtitle(it.moduleTitle)
                        .setDescription(it.moduleDescription)
                        .setTitle(it.moduleTitle)
                        .setExtras(Bundle().apply {
                            putString("courseId", it.courseId)
                            putString("sectionId", it.sectionId)
                            putString("moduleId", it.moduleId)
                        })
                        .build()
                    ).build()
            )
        }

        serviceHandler.addMediaItemList(mediaItemList)
    }

    fun loadCourseAudios(courseId: String, outputDir: File) {
        viewModelScope.launch {
            val audios = preferencesManager.savedCourseAudios
            val courseContent = audios.filter { it.courseId == courseId }
            if (courseContent.isEmpty()) {
                convertTextsToSpeech(courseId)
                return@launch
            }

            _audioFiles.value = courseContent
            loadData()
        }
    }

    private fun getModuleContent(courses: List<Course>, courseId: String): List<ModuleAudio> {
        val moduleContents = mutableListOf<ModuleAudio>()
        val course = courses.find { it.id == courseId }
        return course?.let { course ->
            val courseTitle = course.title
            var sequence = 0
            course.sections.forEach { sctn ->
                val sectionTitle = sctn.title
                val sectionId = sctn.id
                sctn.modules.forEach { mdl ->
                    sequence += 1
                    val moduleTitle = mdl.title
                    val content = mdl.content
                    val description = mdl.shortDescription
                    val moduleId = mdl.id
                    val moduleContent = ModuleAudio(
                        course.id,
                        moduleId,
                        sectionId,
                        courseTitle,
                        sectionTitle,
                        moduleTitle,
                        description,
                        content,
                        sequence,
                        ""
                    )
                    moduleContents.add(moduleContent)
                }
            }
            moduleContents
        } ?: run {
            Log.d("AudioPlayerViewModel", "Course not found")
            emptyList()
        }
    }

    private fun convertTextsToSpeech(courseId: String) {
        viewModelScope.launch {
            val userCourses = coursesRepository.getCourses(true)
            userCourses.getOrNull()?.let { course ->
                val moduleContent = getModuleContent(course, courseId)
                moduleContent.forEach { content ->
                    val contentJson = Gson().toJson(content)
                    val inputData = workDataOf(
                        "content" to contentJson,
                        "key" to _ttsApiKey.value
                    )
                    val constraint = Constraints.Builder()
                        //.setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val workRequest = OneTimeWorkRequestBuilder<TextToSpeechWorker>()
                        .setInputData(inputData)
                        .setConstraints(constraint)
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build()
                    workManager.enqueueUniqueWork(
                        content.moduleId,
                        ExistingWorkPolicy.REPLACE,
                        workRequest,
                    )
                }
            }
        }
    }


    private fun convertTextToWav(courseId: String, outputDir: File) {
        viewModelScope.launch {
            val userCourses = coursesRepository.getCourses(true)
            userCourses.getOrNull()?.let { courses ->
                val course = courses.find { it.id == courseId }
                val moduleContents = mutableListOf<ModuleAudio>()
                course?.let { course ->
                    val courseTitle = course.title
                    var sequence = 0
                    course.sections.forEach { sctn ->
                        sequence += 1
                        val sectionTitle = sctn.title
                        val sectionId = sctn.id
                        sctn.modules.forEach { mdl ->
                            val moduleTitle = mdl.title
                            val content = mdl.content
                            val description = mdl.shortDescription
                            val moduleId = mdl.id
                            val moduleContent = ModuleAudio(
                                courseId,
                                moduleId,
                                sectionId,
                                courseTitle,
                                sectionTitle,
                                moduleTitle,
                                description,
                                content,
                                sequence,
                                ""
                            )
                            moduleContents.add(moduleContent)
                        }
                    }
                    val (audios, _) = audioRepository.convertTextToWavLocal(moduleContents, outputDir)
                    _audioFiles.value = audios
                    val updatedAudios = preferencesManager.savedCourseAudios.addAll(audios)
                    preferencesManager.savedCourseAudios = updatedAudios
                    loadData()
                } ?: run {
                    Log.d("AudioPlayerViewModel", "Course not found")
                }
            } ?: run {
                Log.d("getCourses", "Course not found 2")
            }
        }
    }
}

sealed class UIEvent {
    data object PlayPause : UIEvent()
    data object Backward : UIEvent()
    data object Forward : UIEvent()
    data class UpdateProgress(val newProgress: Float) : UIEvent()
}

sealed class UIState {
    data object Initial : UIState()
    data object Ready : UIState()
}