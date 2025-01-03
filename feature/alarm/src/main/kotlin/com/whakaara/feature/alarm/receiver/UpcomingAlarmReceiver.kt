package com.whakaara.feature.alarm.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.whakaara.core.HiltBroadcastReceiver
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.WidgetUpdater
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.goAsync
import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.feature.alarm.R
import com.whakaara.feature.alarm.service.AlarmMediaService
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class UpcomingAlarmReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    @Named("upcoming")
    lateinit var upcomingAlarmNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var alarmManager: AlarmManager

    @Inject
    lateinit var widgetUpdater: WidgetUpdater

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        val actionsList =
            listOf(
                NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_START,
                NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_STOP,
                NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_CANCEL
            )
        val intentAction = intent.getStringExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION)
        if (!actionsList.contains(intentAction)) return

        val millis =
            intent.getLongExtra(
                NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME,
                0L
            )

        goAsync {
            when (intentAction) {
                NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_START -> {
                    startNotification(millis = millis, context = context, alarmId = intent.action)
                }
                NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_STOP -> {
                    stopNotification(millis = millis)
                }
                NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_CANCEL -> {
                    cancelUpcomingAlarm(alarmId = intent.action, millis = millis, context = context)
                }
            }
        }
    }

    private fun startNotification(
        alarmId: String?,
        millis: Long,
        context: Context
    ) {
        val intent =
            Intent(context, UpcomingAlarmReceiver::class.java).apply {
                action = alarmId
                putExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION, NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_CANCEL)
                putExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME, millis)
            }
        val pendingIntent =
            PendingIntentUtils.getBroadcast(
                context = context,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = intent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        notificationManager.notify(
            millis.toInt(),
            upcomingAlarmNotificationBuilder.apply {
                setTimeoutAfter(millis - System.currentTimeMillis())
                setWhen(millis)
                addAction(0, context.getString(R.string.notification_upcoming_alarm_cancel), pendingIntent)
            }.build()
        )
    }

    private fun stopNotification(millis: Long) {
        notificationManager.cancel(millis.toInt())
    }

    private suspend fun cancelUpcomingAlarm(
        context: Context,
        alarmId: String?,
        millis: Long
    ) {
        alarmId?.let { id ->
            alarmRepository.isEnabled(id = UUID.fromString(alarmId), isEnabled = false)

            updateWidget()

            stopAlarm(
                alarmId = id,
                context = context
            )

            stopNotification(millis = millis)
        }
    }

    private fun stopAlarm(alarmId: String, context: Context) {
        val intent = Intent(context, AlarmMediaService::class.java).apply {
            this.action = alarmId
        }

        val pendingIntent = PendingIntentUtils.getService(
            context = context,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun updateWidget() {
        widgetUpdater.updateWidget()
    }
}
