package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.data.Alarm
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.receiver.Receiver
import com.app.whakaara.state.AlarmState
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val repository: AlarmRepository
) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(AlarmState())
    val uiState: StateFlow<AlarmState> = _uiState.asStateFlow()

    init {
        getAllAlarms()
    }

    private fun getAllAlarms() = viewModelScope.launch {
        repository.allAlarms().flowOn(Dispatchers.IO).collect { allAlarms ->
            _uiState.value = AlarmState(alarms = allAlarms)
        }
    }


    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        repository.insert(alarm)
        // TODO: Generate string for toast - Alarm set for x time OR Alarm set %d hours %d minute(s)
        GeneralUtils.showToast(title = "Alarm created", context = app.applicationContext)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        deleteAlarm(alarm)
        repository.delete(alarm)
        GeneralUtils.showToast(title = "Alarm deleted", context = app.applicationContext)
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = false))
        deleteAlarm(alarm)
        GeneralUtils.showToast(title = "Alarm cancelled", context = app.applicationContext)
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = true))
        deleteAlarm(alarm)
        createAlarm(alarm)
        // TODO: Generate string for toast - Alarm set for x time OR Alarm set %d hours %d minute(s)
        GeneralUtils.showToast(title = "Alarm enabled", context = app.applicationContext)
    }

    fun reset(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm)
        deleteAlarm(alarm)
        create(alarm)
    }

    private fun updateExistingAlarmInDatabase(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    private fun createAlarm(
        alarm: Alarm,
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (userHasNotGrantedAlarmPermission(alarmManager)) {
                redirectUserToSpecialAppAccessScreen()
            } else {
                val startReceiverIntent = getStartReceiverIntent(alarm)
                val intentActionAfterAlarmGoesOff = PendingIntentUtils.getBroadcast(
                    context = app,
                    id = INTENT_REQUEST_CODE,
                    intent = startReceiverIntent,
                    flag = 0
                )

                val alarmDetailsIntent = PendingIntentUtils.getBroadcast(
                    context = app,
                    id = INTENT_REQUEST_CODE, //1,
                    intent = startReceiverIntent,
                    flag = 0
                )

                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(
                        DateUtils.getTimeInMillis(alarm),
                        alarmDetailsIntent
                    ),
                    intentActionAfterAlarmGoesOff
                )
            }
        } else {
            setExactAlarm(alarm, alarmManager)
        }
    }

    private fun setExactAlarm(
        alarm: Alarm,
        alarmManager: AlarmManager
    ) {
        val startReceiverIntent = getStartReceiverIntent(alarm)
        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = 0
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            DateUtils.getTimeInMillis(alarm),
            pendingIntent
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun redirectUserToSpecialAppAccessScreen() {
        Intent().apply {
            action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
        }.also {
            app.applicationContext.startActivity(it)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun userHasNotGrantedAlarmPermission(alarmManager: AlarmManager) =
        !alarmManager.canScheduleExactAlarms()

    private fun getStartReceiverIntent(alarm: Alarm) =
        Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
        }

    private fun deleteAlarm(
        alarm: Alarm,
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
        }

        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = intent,
            flag = 0
        )

        alarmManager.cancel(pendingIntent)
    }
}