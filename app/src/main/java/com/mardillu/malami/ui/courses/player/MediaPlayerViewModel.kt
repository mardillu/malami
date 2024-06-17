package com.mardillu.malami.ui.courses.player

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Created on 08/06/2024 at 10:10 pm
 * @author mardillu
 */
@HiltViewModel
class MediaPlayerViewModel @Inject constructor(
    private val commandHandler: CommandHandler
):ViewModel() {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentAudioIndex = MutableStateFlow(0)
    val currentAudioIndex: StateFlow<Int> = _currentAudioIndex

    private val _audioList = MutableStateFlow(listOf<String>())
    val audioList: StateFlow<List<String>> = _audioList

    fun setAudioList(audios: List<String>, index: Int = 0) {
        _audioList.value = audios
        _currentAudioIndex.update { index }
        //commandHandler.sendCommand(PlayerCommand.PlaySpecific(index))
    }

    fun playAudio(index: Int) {
        _currentAudioIndex.value = index
        _isPlaying.value = true
        commandHandler.sendCommand(PlayerCommand.PlaySpecific(index), _audioList.value)
    }

    fun seekTo(position: Float) {
        _isPlaying.value = true
        commandHandler.sendCommand(PlayerCommand.SeekTo(position), _audioList.value)
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        val command = if (_isPlaying.value) PlayerCommand.Play else PlayerCommand.Pause
        commandHandler.sendCommand(command, _audioList.value)
    }

    fun playNext() {
        if (_currentAudioIndex.value < _audioList.value.size - 1) {
            _currentAudioIndex.value += 1
            _isPlaying.value = true
            commandHandler.sendCommand(PlayerCommand.Next, _audioList.value)
        }
    }

    fun playPrevious() {
        if (_currentAudioIndex.value > 0) {
            _currentAudioIndex.value -= 1
            _isPlaying.value = true
            commandHandler.sendCommand(PlayerCommand.Previous, _audioList.value)
        }
    }
}

sealed class PlayerCommand {
    data object Play : PlayerCommand()
    data object Pause : PlayerCommand()
    data object Next : PlayerCommand()
    data object Previous : PlayerCommand()
    data class SeekTo(val position: Float) : PlayerCommand()
    data class PlaySpecific(val index: Int) : PlayerCommand()
}
