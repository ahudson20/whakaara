package com.app.whakaara.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.logic.AlarmManagerWrapper
import com.app.whakaara.utility.PendingIntentUtils
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_CANCEL
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_START
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_STOP
import com.whakaara.data.alarm.AlarmRepository
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
    lateinit var alarmManagerWrapper: AlarmManagerWrapper

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        super.onReceive(context, intent)
        val actionsList =
            listOf(
                UPCOMING_ALARM_RECEIVER_ACTION_START,
                UPCOMING_ALARM_RECEIVER_ACTION_STOP,
                UPCOMING_ALARM_RECEIVER_ACTION_CANCEL,
            )
        val intentAction = intent.getStringExtra(UPCOMING_ALARM_INTENT_ACTION)
        if (!actionsList.contains(intentAction)) return

        val millis =
            intent.getLongExtra(
                UPCOMING_ALARM_INTENT_TRIGGER_TIME,
                0L,
            )

        goAsync {
            when (intentAction) {
                UPCOMING_ALARM_RECEIVER_ACTION_START -> {
                    startNotification(millis = millis, context = context, alarmId = intent.action)
                }
                UPCOMING_ALARM_RECEIVER_ACTION_STOP -> {
                    stopNotification(millis = millis)
                }
                UPCOMING_ALARM_RECEIVER_ACTION_CANCEL -> {
                    cancelUpcomingAlarm(alarmId = intent.action, millis = millis)
                }
            }
        }
    }

    private fun startNotification(
        alarmId: String?,
        millis: Long,
        context: Context,
    ) {
        val intent =
            Intent(context, UpcomingAlarmReceiver::class.java).apply {
                action = alarmId
                putExtra(UPCOMING_ALARM_INTENT_ACTION, UPCOMING_ALARM_RECEIVER_ACTION_CANCEL)
                putExtra(UPCOMING_ALARM_INTENT_TRIGGER_TIME, millis)
            }
        val pendingIntent =
            PendingIntentUtils.getBroadcast(
                context = context,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = intent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT,
            )

        notificationManager.notify(
            millis.toInt(),
            upcomingAlarmNotificationBuilder.apply {
                setTimeoutAfter(millis - System.currentTimeMillis())
                setWhen(millis)
                addAction(0, context.getString(R.string.notification_upcoming_alarm_cancel), pendingIntent)
            }.build(),
        )
    }

    private fun stopNotification(millis: Long) {
        notificationManager.cancel(millis.toInt())
    }

    private suspend fun cancelUpcomingAlarm(
        alarmId: String?,
        millis: Long,
    ) {
        if (alarmId != null) {
            alarmRepository.isEnabled(id = UUID.fromString(alarmId), isEnabled = false)
            alarmManagerWrapper.deleteAlarm(alarmId = alarmId)
            stopNotification(millis = millis)
        }
    }
}
