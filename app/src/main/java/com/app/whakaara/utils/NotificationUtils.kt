package com.app.whakaara.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color.GREEN
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
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

        // TODO: re-visit this.
        channel.enableVibration(true)
        channel.enableLights(true)

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
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // TODO: pass in custom values.
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Alarm....")
            .setContentText("AlarmManager is working.")
            .setSmallIcon(drawable.mtrl_ic_error)
            .setColor(GREEN)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
    }
}