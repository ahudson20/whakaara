package com.app.whakaara.logic

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.core.app.NotificationCompat
import com.app.whakaara.MainActivity


@OptIn(ExperimentalLayoutApi::class)
class Notifications {
    private val NOTIFIYTAG = "new request"
    fun notify(context: Context, message:String, number:Int) {
        val intent = Intent(context, MainActivity::class.java)

        val builder = NotificationCompat.Builder(context)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("New request")
            .setContentText(message)
            .setNumber(number)
//            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentIntent(
                PendingIntent
                    .getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
            )
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIFIYTAG, 0, builder.build())

    }
}