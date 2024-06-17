package com.mardillu.malami.ui.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Created on 09/06/2024 at 12:09 am
 * @author mardillu
 */
class MediaActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val action = intent.action ?: return
            val mediaServiceIntent = Intent(context, AudioPlayerService1::class.java).apply {
                this.action = action
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(mediaServiceIntent)
            } else {
                context.startService(mediaServiceIntent)
            }
        }
    }
}