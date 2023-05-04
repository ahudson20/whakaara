package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.MainActivity
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.state.AlarmState
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalLayoutApi::class)
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
        repository.allAlarms().collect { allAlarms ->
            _uiState.value = AlarmState(alarms = allAlarms).apply {
                    _uiState.stateIn(viewModelScope)
            }
        }
    }

    fun getAlarmById(id: UUID): Alarm {
        return repository.getAlarmById(id = id)
    }

    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        repository.insert(alarm)
        GeneralUtils.showToast(title = "Alarm cancelled", context = app.applicationContext)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        deleteAlarm(alarm)
        repository.delete(alarm)
        GeneralUtils.showToast(title = "Alarm cancelled", context = app.applicationContext)
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        deleteAlarm(alarm)
        update(alarm.copy(isEnabled = false))
        GeneralUtils.showToast(title = "Alarm cancelled", context = app.applicationContext)
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        update(alarm.copy(isEnabled = true))
        GeneralUtils.showToast(title = "Alarm cancelled", context = app.applicationContext)
    }

    private fun createAlarm(
        alarm: Alarm,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    app.applicationContext.startActivity(intent)
                }
            } else {
                val intent = Intent(app, Receiver::class.java).apply {
                    // setting unique action allows for differentiation when deleting.
                    this.action = alarm.alarmId.toString()
                    putExtras(alarm)
                }
                val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)

                val mainActivityIntent = Intent(app.applicationContext, MainActivity::class.java)
                val testPendingIntent = PendingIntentUtils.getBroadcast(app, 1, mainActivityIntent, 0)

                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(DateUtils.getTimeInMillis(alarm), testPendingIntent),
                    pendingIntent
                )
            }
        } else {
            val alarmManager =  app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(app, Receiver::class.java).apply {
                // setting unique action allows for differentiation when deleting.
                this.action = alarm.alarmId.toString()
                putExtras(alarm)
            }
            val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, DateUtils.getTimeInMillis(alarm), pendingIntent)
        }
    }

    private fun deleteAlarm(
        alarm: Alarm,
    ) {
        val alarmManager =  app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
        }
        val pendingIntent = PendingIntentUtils.getBroadcast(app, 0, intent, 0)

        alarmManager.cancel(pendingIntent)
    }

    private fun update(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }
    private fun Intent.putExtras(alarm: Alarm) {
        putExtra("title", "Alarm")
        putExtra("subtitle", DateUtils.generateSubTitle(alarm))
        putExtra("alarmId", alarm.alarmId.toString())
        putExtra("action", "create")
    }

    fun snackBarPromptPermission(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        context: Context
    ) {
        scope.launch {
            val result = snackBarHostState.showSnackbar(
                message = context.getString(R.string.permission_prompt_message),
                actionLabel = context.getString(R.string.permission_prompt_action_label),
                duration = SnackbarDuration.Long
            )
            /**SNACKBAR PROMPT ACCEPTED**/
            if (snackBarHasBeenClicked(result)) {
                openDeviceApplicationSettings(context)
            }
        }
    }

    private fun openDeviceApplicationSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    private fun snackBarHasBeenClicked(result: SnackbarResult) =
        result == SnackbarResult.ActionPerformed
}