package com.mardillu.malami.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.mardillu.malami.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale
import javax.inject.Inject

/**
 * Created on 08/06/2024 at 10:29 pm
 * @author mardillu
 */
@AndroidEntryPoint
class AudioPlayerService1: Service() {
    @Inject
    lateinit var player: ExoPlayer

    //private lateinit var mediaSession: MediaSession
    private val handler = Handler(Looper.getMainLooper())
    val NOTIFICATION_ID = 100
    val CHANNEL_ID = "audio_playback_channel"
    val EXPANDED = 1
    val COLLAPSED = 2
    private var NOTIFICATION_EXPAND_STATE = COLLAPSED
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private val _progress = MutableStateFlow(AudioPlaybackProgress())
    val progress: StateFlow<AudioPlaybackProgress> get() = _progress

    private val binder = AudioPlayerBinder()

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
//        mediaSession = MediaSession.Builder(this, player).apply {
//            setSessionActivity(createContentIntent())
//        }.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    _isPlaying.update { true }
                } else {
                    _isPlaying.update { false }
                }
            }
        })

        startForeground(NOTIFICATION_ID, createNotification())
        updateNotificationProgress()
    }

    private fun createContentIntent(): PendingIntent {
        val intent = packageManager?.getLaunchIntentForPackage(packageName)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    @OptIn(UnstableApi::class)
    private fun createNotification(): Notification {
        val playPauseAction = if (player.isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            )

        } else {
            NotificationCompat.Action(
                R.drawable.ic_play_arrow,
                "Play",
                createPendingIntent(ACTION_PLAY)
            )
        }

        //val mediaSessionToken = mediaSession.sessionCompatToken
        val expandedView = RemoteViews(packageName, R.layout.notification_expanded)

        expandedView.setImageViewIcon(R.id.notification_play_pause,
            if (player.isPlaying) Icon.createWithResource(this, R.drawable.ic_pause)
            else
                Icon.createWithResource(this, R.drawable.ic_play_arrow)
        )
        if (NOTIFICATION_EXPAND_STATE == COLLAPSED) {
            expandedView.setImageViewIcon(R.id.toggle_progress_visibility,  Icon.createWithResource(this, R.drawable.baseline_keyboard_arrow_down_24))
            expandedView.setViewVisibility(R.id.progress_layout, View.GONE)
            expandedView.setViewVisibility(R.id.notification_actions_expanded, View.GONE)
            expandedView.setViewVisibility(R.id.notification_actions, View.VISIBLE)
            expandedView.setOnClickPendingIntent(R.id.toggle_progress_visibility,
                createPendingIntent(ACTION_EXPAND))
        } else {
            expandedView.setImageViewIcon(R.id.toggle_progress_visibility,  Icon.createWithResource(this, R.drawable.baseline_keyboard_arrow_up_24))
            expandedView.setViewVisibility(R.id.progress_layout, View.VISIBLE)
            expandedView.setViewVisibility(R.id.notification_actions_expanded, View.VISIBLE)
            expandedView.setViewVisibility(R.id.notification_actions, View.GONE)
            expandedView.setOnClickPendingIntent(R.id.toggle_progress_visibility,
                createPendingIntent(ACTION_COLLAPSE))
        }
        expandedView.setTextViewText(R.id.notification_title, "Playing audio")
        expandedView.setTextViewText(R.id.notification_text, "Audio is playing in the background")
        expandedView.setImageViewResource(R.id.notification_large_icon, R.drawable.ic_music_note)

        expandedView.setOnClickPendingIntent(R.id.notification_skip_previous, createPendingIntent(ACTION_PREVIOUS))
        expandedView.setOnClickPendingIntent(R.id.notification_play_pause, createPendingIntent(if (player.isPlaying) ACTION_PAUSE else ACTION_PLAY))
        expandedView.setOnClickPendingIntent(R.id.notification_skip_next, createPendingIntent(ACTION_NEXT))

        expandedView.setOnClickPendingIntent(R.id.expanded_notification_skip_next, createPendingIntent(ACTION_PREVIOUS))
        expandedView.setOnClickPendingIntent(R.id.expanded_notification_play_pause, createPendingIntent(if (player.isPlaying) ACTION_PAUSE else ACTION_PLAY))
        expandedView.setOnClickPendingIntent(R.id.expanded_notification_skip_next, createPendingIntent(ACTION_NEXT))

        val duration = player.duration
        val position = player.currentPosition
        if (duration > 0) {
            expandedView.setTextViewText(R.id.time_elapsed, formatDuration(position))
            expandedView.setTextViewText(R.id.time_remaining, formatDuration(duration - position))
            expandedView.setProgressBar(R.id.notification_progress, duration.toInt(), position.toInt(), false)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Playing audio")
//            .setContentText("Audio is playing in the background")
//            .setSubText("Subtitle or additional info")
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
//            .setCustomContentView(collapsedView)
            .setCustomBigContentView(expandedView)
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(NotificationCompat.Action(R.drawable.ic_skip_previous, "Previous", createPendingIntent(ACTION_PREVIOUS)))
            .addAction(playPauseAction)
            .addAction(NotificationCompat.Action(R.drawable.ic_skip_next, "Next", createPendingIntent(ACTION_NEXT)))
//            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mediaSessionToken)
//                .setShowActionsInCompactView(0, 1, 2))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setSilent(true)
//            .setTicker("ticker")
//            .setDefaults(Notification.DEFAULT_ALL)

        return builder.build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MediaActionReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.getStringArrayListExtra("AUDIO_LIST")?.let { audioList ->
            loadAndPlayAudioList(audioList)
        }
        when (intent?.action) {
            ACTION_PLAY -> player.play()
            ACTION_PAUSE -> {
                player.pause()
            }
            ACTION_NEXT -> player.seekToNextMediaItem()
            ACTION_PREVIOUS -> player.seekToPreviousMediaItem()
            ACTION_EXPAND -> NOTIFICATION_EXPAND_STATE = EXPANDED
            ACTION_COLLAPSE -> NOTIFICATION_EXPAND_STATE = COLLAPSED
            ACTION_SEEK_TO -> {
                val seekTo = intent.getLongExtra(EXTRA_SEEK_TO, 0L)
                player.seekTo(seekTo)
            }
            ACTION_PLAY_SPECIFIC -> {
                val index = intent.getIntExtra(EXTRA_INDEX, -1)
                if (index != -1) player.seekTo(index, 0)
                player.play()
            }
        }
        startForeground(NOTIFICATION_ID, createNotification())
        return START_STICKY
    }

    private fun loadAndPlayAudioList(audioList: List<String>) {
        player.clearMediaItems()
        audioList.forEach { audioUri ->
            val mediaItem = MediaItem.fromUri(audioUri)
            player.addMediaItem(mediaItem)
        }

        player.prepare()
        player.playWhenReady = true
        _isPlaying.update { true }
    }

//    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
//        return mediaSession
//    }

    override fun onDestroy() {
       // mediaSession.release()
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    /**
     * This method is called when the system determines that the service is no longer used and is being removed.
     * It checks the player's state and if the player is not ready to play or there are no items in the media queue, it stops the service.
     *
     * @param rootIntent The original root Intent that was used to launch the task that is being removed.
     */
    override fun onTaskRemoved(rootIntent: Intent?) {
        //val player = mediaSession.player
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    //convert milliseconds to minutes and seconds
    private fun formatDuration(duration: Long): String {
        val minutes = duration / 1000 / 60
        val seconds = duration / 1000 % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun updateNotificationProgress() {
        handler.postDelayed({
            startForeground(NOTIFICATION_ID, createNotification())
            val playbackProgress = AudioPlaybackProgress(
                player.duration,
                player.currentPosition,
                formatDuration(player.currentPosition),
                formatDuration(player.duration - player.currentPosition)
            )
            _progress.update { playbackProgress }
            updateNotificationProgress()
        }, 700)
    }

    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
        const val ACTION_PLAY_SPECIFIC = "action_play_specific"
        const val EXTRA_INDEX = "extra_index"
        const val ACTION_EXPAND = "action_expand"
        const val ACTION_COLLAPSE = "action_collapse"
        const val EXTRA_SEEK_TO = "extra_seek_to"
        const val ACTION_SEEK_TO = "action_seek_to"
    }
//    Intent(this, AudioPlayerService::class.java).also { intent ->
//        startService(intent)
//    }
    inner class AudioPlayerBinder : Binder() {
        fun getService(): AudioPlayerService1 = this@AudioPlayerService1
    }
}

data class AudioPlaybackProgress(
    val duration: Long = 0L,
    val position: Long = 0L,
    val timeElapsed: String = "0:00",
    val timeRemaining: String = "0:00"
)