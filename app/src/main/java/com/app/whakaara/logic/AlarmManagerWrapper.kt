package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.app.whakaara.activities.MainActivity
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
import com.app.whakaara.utils.constants.NotificationUtilsConstants.PLAY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.SERVICE_ACTION
import java.util.Calendar
import javax.inject.Inject

class AlarmManagerWrapper @Inject constructor(
    private val app: Application,
    private val alarmManager: AlarmManager
) {
    fun createAlarm(
        alarmId: String,
        autoSilenceTime: Int,
        date: Calendar
    ) {
        if (!userHasNotGrantedAlarmPermission()) {
            redirectUserToSpecialAppAccessScreen()
        } else {
            setExactAlarm(
                alarmId = alarmId,
                autoSilenceTime = autoSilenceTime,
                date = date
            )
        }
    }

    private fun redirectUserToSpecialAppAccessScreen() {
        Intent().apply { action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM }.also {
            app.applicationContext.startActivity(it)
        }
    }
    private fun userHasNotGrantedAlarmPermission() =
        alarmManager.canScheduleExactAlarms()

    @OptIn(ExperimentalLayoutApi::class)
    private fun setExactAlarm(
        alarmId: String,
        autoSilenceTime: Int,
        date: Calendar
    ) {
        val startReceiverIntent = getStartReceiverIntent(
            alarmId = alarmId,
            autoSilenceTime = autoSilenceTime,
            type = NOTIFICATION_TYPE_ALARM
        )

        val alarmPendingIntent = PendingIntentUtils.getService(
            app,
            INTENT_REQUEST_CODE,
            startReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmInfoPendingIntent = PendingIntentUtils.getActivity(
            app,
            INTENT_REQUEST_CODE,
            Intent(app, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(DateUtils.getTimeInMillis(alarmDate = date), alarmInfoPendingIntent),
            alarmPendingIntent
        )
    }

    private fun getStartReceiverIntent(
        autoSilenceTime: Int,
        action: Int = PLAY,
        type: Int,
        alarmId: String? = null
    ) =
        Intent(app, MediaPlayerService::class.java).apply {
            this.action = alarmId
            putExtra(INTENT_AUTO_SILENCE, autoSilenceTime)
            putExtra(SERVICE_ACTION, action)
            putExtra(NOTIFICATION_TYPE, type)
            putExtra(INTENT_ALARM_ID, alarmId)
        }

    fun stopAlarm(
        alarmId: String
    ) {
        val intent = Intent(app, MediaPlayerService::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarmId
        }

        val pendingIntent = PendingIntentUtils.getService(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    fun createTimerNotification(
        milliseconds: Long
    ) {
        val startReceiverIntent = getStartReceiverIntent(
            alarmId = "timer_notification",
            autoSilenceTime = ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES,
            type = NOTIFICATION_TYPE_TIMER
        )

        val pendingIntent = PendingIntentUtils.getService(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            milliseconds,
            pendingIntent
        )
    }

    fun cancelTimerNotification() {
        val startReceiverIntent = Intent(app, MediaPlayerService::class.java).apply {
            this.action = "timer_notification"
        }
//            getStartReceiverIntent(
//            alarmId = "timer_notification",
//            autoSilenceTime = ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES,
//            type = NOTIFICATION_TYPE_TIMER
//        )

        val pendingIntent = PendingIntentUtils.getService(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }
}
