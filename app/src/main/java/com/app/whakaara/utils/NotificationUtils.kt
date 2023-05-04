package com.app.whakaara.utils

import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color.WHITE
import android.media.RingtoneManager
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.core.app.NotificationCompat
import com.app.whakaara.MainActivity
import com.app.whakaara.activities.FullScreenNotificationActivity
import com.google.android.material.R.drawable

class NotificationUtils(context: Context): ContextWrapper(context) {
    private val channelId = "channel_id"
    private val channelName = "channel_name"

    private var manager: NotificationManager? = null

    init {
        createChannel()
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
            setBypassDnd(true)
            lockscreenVisibility = VISIBILITY_PUBLIC
            setShowBadge(true)
            //setVibrationPattern
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
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val onNotificationClickPendingIntent = PendingIntentUtils.getActivity(this, 0, mainActivityIntent, 0)

//        val testIntent = Intent(this, FullScreenNotificationActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val testPendingIntent = PendingIntentUtils.getActivity(this, 0, testIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(drawable.ic_clock_black_24dp)
            .setColor(WHITE)
            .setContentIntent(onNotificationClickPendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .setOngoing(true)
//            .setFullScreenIntent(testPendingIntent, true)
    }
}