package com.app.whakaara.logic

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.service.NotificationService
import com.app.whakaara.utils.NotificationUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.google.android.material.R.drawable

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val title = intent.getStringExtra("title") ?: "Alarm"
            val subTitle = intent.getStringExtra("subtitle") ?: ""
            val alarmId = intent.getStringExtra("alarmId")
            val uniqueID = System.currentTimeMillis().toInt()

            val snoozeAlarmIntent = Intent().apply {
                setClass(context, NotificationReceiver::class.java)
                putExtra("alarmId", alarmId.toString())
                putExtra("notificationId", uniqueID)
                action = "cancel"
            }
            val snoozeActionPendingIntent = PendingIntentUtils.getBroadcast(context, 0, snoozeAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)


//            val cancelAlarmIntent = Intent().apply {
//                setClass(context, NotificationService::class.java)
//                putExtra("alarmId", alarmId.toString())
//                putExtra("notificationId", uniqueID)
//                action = "cancel"
//
//            }
//            val cancelActionPendingIntent = PendingIntent.getService(context, 0, cancelAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val cancelAlarmIntent = Intent().apply {
                setClass(context, NotificationReceiver::class.java)
                putExtra("alarmId", alarmId.toString())
                putExtra("notificationId", uniqueID)
                action = "cancel"
            }
            val cancelActionPendingIntent = PendingIntentUtils.getBroadcast(context, 0, cancelAlarmIntent,  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)//PendingIntent.FLAG_UPDATE_CURRENT

            val notificationUtils = NotificationUtils(context)
            val notification = notificationUtils.getNotificationBuilder().apply {
                setContentTitle(title)
                setContentText(subTitle)
                addAction(drawable.mtrl_ic_cancel, "Snooze", snoozeActionPendingIntent)
                addAction(drawable.mtrl_ic_cancel, "Cancel", cancelActionPendingIntent)
            }.build()

            notificationUtils.getManager().notify(uniqueID, notification)
        } catch (exception: Exception) {
            Log.d("Receiver exception", exception.printStackTrace().toString())
        }
    }
}