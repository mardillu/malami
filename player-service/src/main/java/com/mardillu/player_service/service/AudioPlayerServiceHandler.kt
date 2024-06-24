package com.mardillu.player_service.service

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AudioPlayerServiceHandler @Inject constructor(
    private val player: ExoPlayer
) : Player.Listener {

    private val _audioPlayerState = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Initial)
    val audioPlayerState = _audioPlayerState.asStateFlow()

    private val _nowPlayingModule = MutableStateFlow(MediaItem.EMPTY)
    val nowPlayingModule = _nowPlayingModule.asStateFlow()

    private var job: Job? = null

    init {
        player.addListener(this)
        job = Job()
    }

    fun isPlaying() = player.isPlaying

    fun addMediaItem(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun addMediaItemList(mediaItemList: List<MediaItem>) {
        player.setMediaItems(mediaItemList)
        player.prepare()
    }

    suspend fun onPlayerEvent(playerEvent: PlayerEvent) {
        when (playerEvent) {
            PlayerEvent.Backward -> {
                player.seekToPreviousMediaItem()
            }
            PlayerEvent.Forward -> {
                player.seekToNextMediaItem()
            }
            PlayerEvent.PlayPause -> {
                if (player.isPlaying) {
                    player.pause()
                    stopProgressUpdate()
                } else {
                    player.play()
                    _audioPlayerState.value = AudioPlayerState.Playing(isPlaying = true)
                    startProgressUpdate()
                }
            }
            PlayerEvent.Stop -> stopProgressUpdate()
            is PlayerEvent.UpdateProgress -> player.seekTo((player.duration * playerEvent.newProgress).toLong())
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _audioPlayerState.value =
                AudioPlayerState.Buffering(player.currentPosition)
            ExoPlayer.STATE_READY -> {
                if (player.currentMediaItem != null) {
                    _nowPlayingModule.value = player.currentMediaItem!!
                }
                _audioPlayerState.value =
                    AudioPlayerState.Ready(player.duration)
            }
            ExoPlayer.STATE_ENDED -> {
                Log.d("AudioPlayerServiceHandler", "STATE_ENDED, ${player.mediaMetadata.title.toString()}")
                _audioPlayerState.value = AudioPlayerState.MediaItemTransition(player.currentMediaItem)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioPlayerState.value = AudioPlayerState.Playing(isPlaying = isPlaying)
        if (isPlaying) {
            GlobalScope.launch(Dispatchers.Main) {
                startProgressUpdate()
            }
        } else {
            stopProgressUpdate()
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (mediaItem != null) {
            _nowPlayingModule.value = mediaItem
            _audioPlayerState.value = AudioPlayerState.MediaItemTransition(mediaItem)
        }
    }

    private suspend fun startProgressUpdate() = job.run {
        while (true) {
            delay(500)
            if (player.currentMediaItem != null) {
                _nowPlayingModule.value = player.currentMediaItem!!
            }
            _audioPlayerState.value = AudioPlayerState.Progress(player.currentPosition)
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _audioPlayerState.value = AudioPlayerState.Playing(isPlaying = false)
    }
}

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()
    data object Backward : PlayerEvent()
    data object Forward : PlayerEvent()
    data object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class AudioPlayerState {
    data object Initial : AudioPlayerState()
    data class Ready(val duration: Long) : AudioPlayerState()
    data class Progress(val progress: Long) : AudioPlayerState()
    data class Buffering(val progress: Long) : AudioPlayerState()
    data class Playing(val isPlaying: Boolean) : AudioPlayerState()
    data class MediaItemTransition(val mediaItem: MediaItem?) : AudioPlayerState()

}