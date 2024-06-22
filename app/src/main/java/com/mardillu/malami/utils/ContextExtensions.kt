package com.mardillu.malami.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.mardillu.malami.R

/**
 * Created on 17/06/2024 at 5:52 pm
 * @author mardillu
 */
fun Context.createNotification(channel: String= "default_channel", title: String?, smallMessage: String?, bigMessage: String?): Notification {
    val pendingIntent: PendingIntent?
    val mNotifyManager =
        getSystemService(NotificationManager::class.java)
    val intent = Intent()
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    pendingIntent = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(intent)
        getPendingIntent(0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        var mChannel = mNotifyManager.getNotificationChannel(channel)
        if (mChannel == null) {
            mChannel = NotificationChannel(channel, "Course-to-audio updates", importance)
            mChannel.enableVibration(false)
            mNotifyManager.createNotificationChannel(mChannel)
        }
    }
    val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channel)
    builder.setContentTitle(title)
        .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentText(smallMessage)
        .setDefaults(Notification.DEFAULT_ALL)
        .setSilent(true)
        .setAutoCancel(true)
        .setStyle(
            NotificationCompat.BigTextStyle()
            .bigText(bigMessage))
        .setContentIntent(pendingIntent)
        .addAction(R.drawable.baseline_open_in_browser_24,
            getString(R.string.open),
            pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setTicker(title)

    return builder.build()
}
