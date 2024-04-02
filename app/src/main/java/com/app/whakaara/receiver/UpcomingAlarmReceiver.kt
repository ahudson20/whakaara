package com.app.whakaara.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.app.whakaara.utils.constants.NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION
import com.app.whakaara.utils.constants.NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME
import com.app.whakaara.utils.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_START
import com.app.whakaara.utils.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_STOP
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class UpcomingAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    @Named("upcoming")
    lateinit var upcomingAlarmNotificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
        val actionsList = listOf(
            UPCOMING_ALARM_RECEIVER_ACTION_START,
            UPCOMING_ALARM_RECEIVER_ACTION_STOP
        )
        val intentAction = intent.getStringExtra(UPCOMING_ALARM_INTENT_ACTION)
        if (!actionsList.contains(intentAction)) return

        val millis = intent.getLongExtra(
            UPCOMING_ALARM_INTENT_TRIGGER_TIME,
            Calendar.getInstance().apply {
                add(Calendar.MINUTE, 10)
            }.timeInMillis
        )

        goAsync {
            when (intentAction) {
                UPCOMING_ALARM_RECEIVER_ACTION_START -> startNotification(millis = millis)
                UPCOMING_ALARM_RECEIVER_ACTION_STOP -> stopNotification(millis = millis)
            }
        }
    }

    private fun startNotification(
        millis: Long
    ) {
        notificationManager.notify(
            millis.toInt(),
            upcomingAlarmNotificationBuilder.apply {
                setTimeoutAfter(millis - System.currentTimeMillis())
                setWhen(millis)
            }.build()
        )
    }

    private fun stopNotification(millis: Long) {
        notificationManager.cancel(millis.toInt())
    }
}
