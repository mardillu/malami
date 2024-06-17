package com.mardillu.player_service.service.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.mardillu.player_service.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val NOTIFICATION_ID = 200
private const val NOTIFICATION_CHANNEL_NAME = "Audio Playback"
private const val NOTIFICATION_CHANNEL_ID = "audio_playback"

class AudioPlayerNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val player: ExoPlayer
) {

    private var notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    @UnstableApi
    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ) {
        buildNotification(mediaSession)
        startForegroundNotification(mediaSessionService)
    }

    @UnstableApi
    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(
                AudioPlayerNotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            .setSmallIconResourceId(R.drawable.baseline_headset_24)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseNextActionInCompactView(true)
                it.setUsePreviousActionInCompactView(true)
                it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                it.setUseRewindAction(false)
                it.setUseFastForwardAction(false)
                it.setPlayer(player)
            }
    }

    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSilent(true)
            .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
           notificationManager.createNotificationChannel(channel)
        }
    }
}