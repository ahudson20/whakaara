package com.app.whakaara.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color.WHITE
import android.media.RingtoneManager
import android.os.Build
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.core.app.NotificationCompat
import com.app.whakaara.MainActivity
import com.google.android.material.R.drawable

class NotificationUtils(context: Context): ContextWrapper(context) {
    private val channelId = "channel_id"
    private val channelName = "channel_name"

    private var manager: NotificationManager? = null

    init {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            // TODO: re-visit this.
            enableVibration(true)
            enableLights(true)
        }

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager as NotificationManager
    }

    @OptIn(ExperimentalLayoutApi::class)
    fun getNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntentUtils.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(drawable.ic_clock_black_24dp)
            .setColor(WHITE)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
    }
}