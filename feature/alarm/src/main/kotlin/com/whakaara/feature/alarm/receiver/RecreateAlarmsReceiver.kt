package com.whakaara.feature.alarm.receiver

import android.content.Context
import android.content.Intent
import com.whakaara.core.HiltBroadcastReceiver
import com.whakaara.data.alarm.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecreateAlarmsReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

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

        if (intent.action in actionsList) {
            alarmRepository.triggerAlarmRecreation()
        }
    }
}
