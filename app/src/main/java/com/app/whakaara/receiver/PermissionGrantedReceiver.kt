package com.app.whakaara.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.whakaara.data.Alarm
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PermissionGrantedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val alarms = repo.getAllAlarms().filter { it.isEnabled }
                    for (alarm in alarms) {
                        createAlarm(alarm = alarm, context = context)
                    }
                }
            }
        }
    }

    private fun createAlarm(
        alarm: Alarm,
        context: Context?
    ) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        setExactAlarm(alarm, alarmManager, context)
    }

    private fun setExactAlarm(
        alarm: Alarm,
        alarmManager: AlarmManager,
        context: Context
    ) {
        val startReceiverIntent = getStartReceiverIntent(alarm, context)
        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = context,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            DateUtils.getTimeInMillis(alarm),
            pendingIntent
        )
    }

    private fun getStartReceiverIntent(alarm: Alarm, context: Context) =
        Intent(context, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
            putExtra(NotificationUtilsConstants.INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
        }
}