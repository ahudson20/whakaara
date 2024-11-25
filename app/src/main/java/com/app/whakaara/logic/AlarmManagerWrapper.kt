package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.app.whakaara.R
import com.app.whakaara.activities.MainActivity
import com.whakaara.core.widget.AppWidgetReceiver
import com.app.whakaara.receiver.UpcomingAlarmReceiver
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.utility.DateUtils
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.whakaara.core.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.whakaara.core.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.whakaara.core.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.whakaara.core.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.whakaara.core.constants.NotificationUtilsConstants.PLAY
import com.whakaara.core.constants.NotificationUtilsConstants.SERVICE_ACTION
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_START
import com.whakaara.core.constants.NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_STOP
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//class AlarmManagerWrapper @Inject constructor(
//    private val app: Application,
//    private val alarmManager: AlarmManager
//) {
//    fun createAlarm(
//        alarmId: String,
//        date: Calendar,
//        autoSilenceTime: Int,
//        upcomingAlarmNotificationEnabled: Boolean,
//        upcomingAlarmNotificationTime: Int,
//        repeatAlarmDaily: Boolean,
//        daysOfWeek: MutableList<Int>
//    ) {
//        startAlarm(
//            alarmId = alarmId,
//            autoSilenceTime = autoSilenceTime,
//            date = date,
//            upcomingAlarmNotificationEnabled = upcomingAlarmNotificationEnabled,
//            upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
//            repeatAlarmDaily = repeatAlarmDaily,
//            daysOfWeek = daysOfWeek
//        )
//        updateWidget()
//    }
//
//    fun deleteAlarm(alarmId: String) {
//        stopAlarm(alarmId = alarmId)
//        updateWidget()
//    }
//
//    fun stopStartUpdateWidget(
//        alarmId: String,
//        date: Calendar,
//        autoSilenceTime: Int,
//        upcomingAlarmNotificationEnabled: Boolean,
//        upcomingAlarmNotificationTime: Int,
//        repeatAlarmDaily: Boolean,
//        daysOfWeek: MutableList<Int>
//    ) {
//        stopAlarm(alarmId = alarmId)
//        cancelUpcomingAlarm(alarmId = alarmId, alarmDate = date)
//        startAlarm(
//            alarmId = alarmId,
//            autoSilenceTime = autoSilenceTime,
//            date = date,
//            upcomingAlarmNotificationEnabled = upcomingAlarmNotificationEnabled,
//            upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
//            repeatAlarmDaily = repeatAlarmDaily,
//            daysOfWeek = daysOfWeek
//        )
//        updateWidget()
//    }
//
//    private fun startAlarm(
//        alarmId: String,
//        autoSilenceTime: Int,
//        date: Calendar,
//        upcomingAlarmNotificationEnabled: Boolean,
//        upcomingAlarmNotificationTime: Int,
//        repeatAlarmDaily: Boolean,
//        daysOfWeek: MutableList<Int>
//    ) {
//        if (!userHasNotGrantedAlarmPermission()) {
//            redirectUserToSpecialAppAccessScreen()
//        } else {
//            setAlarm(
//                alarmId = alarmId,
//                autoSilenceTime = autoSilenceTime,
//                date = date,
//                repeatAlarmDaily = repeatAlarmDaily,
//                daysOfWeek = daysOfWeek
//            )
//            setUpcomingAlarm(
//                alarmId = alarmId,
//                alarmDate = date,
//                upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
//                upcomingAlarmNotificationEnabled = upcomingAlarmNotificationEnabled,
//                repeatAlarmDaily = repeatAlarmDaily,
//                daysOfWeek = daysOfWeek
//            )
//        }
//    }
//
//    private fun redirectUserToSpecialAppAccessScreen() {
//        Intent().apply { action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM }.also {
//            app.applicationContext.startActivity(it)
//        }
//    }
//
//    private fun userHasNotGrantedAlarmPermission() = alarmManager.canScheduleExactAlarms()
//
//    @OptIn(ExperimentalLayoutApi::class)
//    private fun setAlarm(
//        alarmId: String,
//        autoSilenceTime: Int,
//        date: Calendar,
//        repeatAlarmDaily: Boolean,
//        daysOfWeek: MutableList<Int>
//    ) {
//        val triggerTime = DateUtils.getTimeAsDate(alarmDate = date)
//
//        val startReceiverIntent = getStartReceiverIntent(
//            alarmId = alarmId,
//            autoSilenceTime = autoSilenceTime,
//            type = NOTIFICATION_TYPE_ALARM
//        )
//
//        val alarmPendingIntent = PendingIntentUtils.getService(
//            app,
//            INTENT_REQUEST_CODE,
//            startReceiverIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val alarmInfoPendingIntent = PendingIntentUtils.getActivity(
//            app,
//            INTENT_REQUEST_CODE,
//            Intent(app, MainActivity::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        if (repeatAlarmDaily || daysOfWeek.isNotEmpty()) {
//            alarmManager.setRepeating(
//                AlarmManager.RTC_WAKEUP,
//                triggerTime.timeInMillis,
//                AlarmManager.INTERVAL_DAY,
//                alarmPendingIntent
//            )
//        } else {
//            alarmManager.setAlarmClock(
//                AlarmManager.AlarmClockInfo(triggerTime.timeInMillis, alarmInfoPendingIntent),
//                alarmPendingIntent
//            )
//        }
//    }
//
//    fun setUpcomingAlarm(
//        alarmId: String,
//        alarmDate: Calendar,
//        upcomingAlarmNotificationEnabled: Boolean,
//        upcomingAlarmNotificationTime: Int,
//        repeatAlarmDaily: Boolean,
//        daysOfWeek: MutableList<Int>
//    ) {
//        val triggerTime = DateUtils.getTimeAsDate(alarmDate = alarmDate)
//        val triggerTimeMinusTenMinutes = (triggerTime.clone() as Calendar).apply {
//            add(Calendar.MINUTE, -upcomingAlarmNotificationTime)
//        }
//
//        val upcomingAlarmIntent = Intent(app, UpcomingAlarmReceiver::class.java).apply {
//            action = alarmId
//            putExtra(UPCOMING_ALARM_INTENT_ACTION, UPCOMING_ALARM_RECEIVER_ACTION_START)
//            putExtra(UPCOMING_ALARM_INTENT_TRIGGER_TIME, triggerTime.timeInMillis)
//        }
//
//        val pendingIntent = PendingIntentUtils.getBroadcast(
//            context = app,
//            id = INTENT_REQUEST_CODE,
//            intent = upcomingAlarmIntent,
//            flag = PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        if (triggerTimeMinusTenMinutes.timeInMillis > Calendar.getInstance().timeInMillis && upcomingAlarmNotificationEnabled) {
//            if (repeatAlarmDaily || daysOfWeek.isNotEmpty()) {
//                alarmManager.setRepeating(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTimeMinusTenMinutes.timeInMillis,
//                    AlarmManager.INTERVAL_DAY,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTimeMinusTenMinutes.timeInMillis,
//                    pendingIntent
//                )
//            }
//        }
//    }
//
//    private fun getStartReceiverIntent(
//        autoSilenceTime: Int,
//        action: Int = PLAY,
//        type: Int,
//        alarmId: String? = null
//    ) = Intent(app, MediaPlayerService::class.java).apply {
//        this.action = alarmId
//        putExtra(INTENT_AUTO_SILENCE, autoSilenceTime)
//        putExtra(SERVICE_ACTION, action)
//        putExtra(NOTIFICATION_TYPE, type)
//        putExtra(INTENT_ALARM_ID, alarmId)
//    }
//
//    private fun stopAlarm(alarmId: String) {
//        val intent = Intent(app, MediaPlayerService::class.java).apply {
//            // setting unique action allows for differentiation when deleting.
//            this.action = alarmId
//        }
//
//        val pendingIntent = PendingIntentUtils.getService(
//            context = app,
//            id = INTENT_REQUEST_CODE,
//            intent = intent,
//            flag = PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        alarmManager.cancel(pendingIntent)
//    }
//
//    fun updateWidget() {
//        app.sendBroadcast(
//            Intent(app, com.whakaara.core.widget.AppWidgetReceiver::class.java).apply {
//                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
//            }
//        )
//    }
//
//    fun cancelUpcomingAlarm(
//        alarmId: String,
//        alarmDate: Calendar
//    ) {
//        val triggerTime = DateUtils.getTimeAsDate(alarmDate = alarmDate)
//
//        val upcomingAlarmIntent = Intent(app, UpcomingAlarmReceiver::class.java).apply {
//            action = alarmId
//        }
//
//        val upcomingAlarmPendingIntent = PendingIntentUtils.getBroadcast(
//            context = app,
//            id = INTENT_REQUEST_CODE,
//            intent = upcomingAlarmIntent,
//            flag = PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        // cancel upcoming alarm
//        alarmManager.cancel(upcomingAlarmPendingIntent)
//
//        // send broadcast to cancel notification
//        upcomingAlarmIntent.also { intent ->
//            intent.putExtra(UPCOMING_ALARM_INTENT_ACTION, UPCOMING_ALARM_RECEIVER_ACTION_STOP)
//            intent.putExtra(UPCOMING_ALARM_INTENT_TRIGGER_TIME, triggerTime.timeInMillis)
//            app.applicationContext.sendBroadcast(intent)
//        }
//    }
//
//    fun getInitialTimeToAlarm(
//        isEnabled: Boolean,
//        time: Calendar
//    ): String {
//        return if (!isEnabled) {
//            app.applicationContext.getString(R.string.card_alarm_sub_title_off)
//        } else {
//            convertSecondsToHMm(
//                seconds = TimeUnit.MILLISECONDS.toSeconds(
//                    DateUtils.getDifferenceFromCurrentTimeInMillis(
//                        time = time
//                    )
//                )
//            )
//        }
//    }
//
//    fun getTimeUntilAlarmFormatted(date: Calendar): String {
//        return convertSecondsToHMm(
//            seconds = TimeUnit.MILLISECONDS.toSeconds(
//                DateUtils.getDifferenceFromCurrentTimeInMillis(
//                    time = date
//                )
//            )
//        )
//    }
//
//    private fun convertSecondsToHMm(seconds: Long): String {
//        val minutes = seconds / 60 % 60
//        val hours = seconds / (60 * 60) % 24
//        val formattedString = StringBuilder()
//        val hoursString = when {
//            hours.toInt() == 0 -> ""
//            else -> {
//                app.applicationContext.resources.getQuantityString(
//                    R.plurals.hours,
//                    hours.toInt(),
//                    hours.toInt()
//                )
//            }
//        }
//        val minutesString = when {
//            minutes.toInt() == 0 && hours.toInt() != 0 -> ""
//            minutes.toInt() == 0 && hours.toInt() == 0 -> app.applicationContext.getString(R.string.alarm_less_than_one_minute)
//            else -> {
//                app.applicationContext.resources.getQuantityString(
//                    R.plurals.minutes,
//                    minutes.toInt(),
//                    minutes.toInt()
//                )
//            }
//        }
//
//        formattedString.append(app.applicationContext.resources.getString(R.string.time_until_alarm_formatted_prefix) + " ")
//        if (hoursString.isNotBlank()) formattedString.append("$hoursString ")
//        if (minutesString.isNotBlank()) formattedString.append("$minutesString ")
//        return formattedString.toString().trim()
//    }
//}
