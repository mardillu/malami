package com.mardillu.malami.ui.courses.player

import android.content.Context
import android.content.Intent
import android.os.Build
import com.mardillu.malami.ui.service.AudioPlayerService

/**
 * Created on 08/06/2024 at 10:27 pm
 * @author mardillu
 */
class CommandHandler(private val context: Context) {

    fun sendCommand(command: PlayerCommand, audioList: List<String>) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            putStringArrayListExtra("AUDIO_LIST", ArrayList(audioList))
            when (command) {
                is PlayerCommand.Play -> action = AudioPlayerService.ACTION_PLAY
                is PlayerCommand.Pause -> action = AudioPlayerService.ACTION_PAUSE
                is PlayerCommand.Next -> action = AudioPlayerService.ACTION_NEXT
                is PlayerCommand.Previous -> action = AudioPlayerService.ACTION_PREVIOUS
                is PlayerCommand.PlaySpecific -> {
                    action = AudioPlayerService.ACTION_PLAY_SPECIFIC
                    putExtra(AudioPlayerService.EXTRA_INDEX, command.index)
                }
                is PlayerCommand.SeekTo -> {
                    action = AudioPlayerService.ACTION_SEEK_TO
                    putExtra(AudioPlayerService.EXTRA_SEEK_TO, command.position)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}