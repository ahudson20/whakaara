package com.app.whakaara.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.utils.NotificationUtils

class Receiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val title = intent?.getStringExtra("title") ?: "~Alarm~"
            val subTitle = intent?.getStringExtra("subtitle") ?: "~do something~"
            val notificationUtils = NotificationUtils(context)
            val notification = notificationUtils.getNotificationBuilder().apply {
                setContentTitle(title)
                setContentText(subTitle)
            }.build()
            notificationUtils.getManager().notify(1, notification)
        } catch(exception: Exception) {
            Log.d("Receiver exception", exception.printStackTrace().toString())
        }
    }
}