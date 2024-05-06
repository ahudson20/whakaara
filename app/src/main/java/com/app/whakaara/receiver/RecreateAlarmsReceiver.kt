package com.app.whakaara.receiver

import android.content.Context
import android.content.Intent
import com.app.whakaara.logic.AlarmManagerWrapper
import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.data.preferences.PreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecreateAlarmsReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var repo: AlarmRepository

    @Inject
    lateinit var preferencesRepo: PreferencesRepository

    @Inject
    lateinit var alarmManagerWrapper: AlarmManagerWrapper

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        val actionsList =
            listOf(
                "android.intent.action.DATE_CHANGED",
                "android.intent.action.TIME_SET",
                "android.intent.action.TIMEZONE_CHANGED",
                "android.intent.action.BOOT_COMPLETED",
                "android.intent.action.LOCKED_BOOT_COMPLETED",
                "android.intent.action.QUICKBOOT_POWERON",
                "android.intent.action.MY_PACKAGE_REPLACED",
                "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
            )
        if (!actionsList.contains(intent.action)) return

        goAsync {
            val preferences = preferencesRepo.getPreferences()
            repo.getAllAlarms().filter { it.isEnabled }.forEach {
                alarmManagerWrapper.createAlarm(
                    alarmId = it.alarmId.toString(),
                    autoSilenceTime = preferences.autoSilenceTime.value,
                    date = it.date,
                    upcomingAlarmNotificationEnabled = preferences.upcomingAlarmNotification,
                    upcomingAlarmNotificationTime = preferences.upcomingAlarmNotificationTime.value,
                    repeatAlarmDaily = it.repeatDaily,
                    daysOfWeek = it.daysOfWeek
                )
            }
        }
    }
}
