package com.app.whakaara.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.logic.AlarmManagerWrapper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PermissionGrantedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    @Inject
    lateinit var preferencesRepo: PreferencesRepository

    @Inject
    lateinit var alarmManagerWrapper: AlarmManagerWrapper

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val preferences = preferencesRepo.getPreferences()
                    repo.getAllAlarms().filter { it.isEnabled }.forEach {
                        alarmManagerWrapper.createAlarm(
                            alarmId = it.alarmId.toString(),
                            autoSilenceTime = preferences.autoSilenceTime.value,
                            date = it.date
                        )
                    }
                }
            }
        }
    }
}
