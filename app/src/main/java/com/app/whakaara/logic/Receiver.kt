package com.app.whakaara.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.utils.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class Receiver: BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val title = intent.getStringExtra("title") ?: "Alarm"
            val subTitle = intent.getStringExtra("subtitle") ?: ""
            val alarmId = intent.getStringExtra("alarmId")
            val action = intent.getStringExtra("action")

            val notificationUtils = NotificationUtils(context)
            val notification = notificationUtils.getNotificationBuilder().apply {
                setContentTitle(title)
                setContentText(subTitle)
                addExtras(
                    bundleOf(
                        "alarmId" to alarmId
                    )
                )
            }.build()

            if (action.equals("cancel")) {
                //intent.getSerializableExtra("alarmId", UUID::class.java)
                if (notification.extras.get != null) {
                    val v = notification.extras.getString("alarmId")
                    repo.isEnabled(UUID.fromString(alarmId), false)
                }
            }


            notificationUtils.getManager().notify(1, notification)
        } catch(exception: Exception) {
            Log.d("Receiver exception", exception.printStackTrace().toString())
        }
    }
}