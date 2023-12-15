package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.receiver.NotificationReceiver
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.utils.DateUtils.Companion.getAlarmTimeFormatted
import com.app.whakaara.utils.DateUtils.Companion.getTimeInMillis
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_SCHEDULE_ALARM_PERMISSION
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_TIME_FORMAT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val repository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(app) {

    // alarm
    private val _uiState = MutableStateFlow(AlarmState())
    val uiState: StateFlow<AlarmState> = _uiState.asStateFlow()

    // preferences
    private val _preferencesState = MutableStateFlow(PreferencesState())
    val preferencesUiState: StateFlow<PreferencesState> = _preferencesState.asStateFlow()

    // timer
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    private var timeMillis by mutableLongStateOf(0L)
    private var lastTimeStamp by mutableLongStateOf(0L)

    var formattedTime by mutableStateOf("00:00:000")
    var isActive by mutableStateOf(false)
    var isStart by mutableStateOf(true)

    init {
        getAllAlarms()
        getPreferences()
    }

    //region preferences
    private fun getPreferences() = viewModelScope.launch {
        preferencesRepository.getPreferencesFlow().flowOn(Dispatchers.IO).collect { preferences ->
            _preferencesState.value = PreferencesState(preferences = preferences)
        }
    }

    fun updatePreferences(preferences: Preferences) = viewModelScope.launch(Dispatchers.IO) {
        preferencesRepository.updatePreferences(preferences = preferences)
    }
    //endregion

    //region alarm
    private fun getAllAlarms() = viewModelScope.launch {
        repository.getAllAlarmsFlow().flowOn(Dispatchers.IO).collect { allAlarms ->
            _uiState.value = AlarmState(alarms = allAlarms)
        }
    }

    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        repository.insert(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        stopAlarm(alarm)
        repository.delete(alarm)
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = false))
        stopAlarm(alarm)
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = true))
        stopAlarm(alarm)
        createAlarm(alarm)
    }

    fun reset(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm)
        stopAlarm(alarm)
        createAlarm(alarm)
    }

    fun snooze(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        val currentTimePlusTenMinutes = Calendar.getInstance().apply {
            add(Calendar.MINUTE, _preferencesState.value.preferences.snoozeTime)
        }
        stopAlarm(alarm)
        createAlarm(
            alarm.copy(
                date = currentTimePlusTenMinutes
            )
        )
    }

    private fun updateExistingAlarmInDatabase(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    private fun createAlarm(
        alarm: Alarm
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!userHasNotGrantedAlarmPermission(alarmManager)) {
            redirectUserToSpecialAppAccessScreen()
        } else {
            setExactAlarm(alarm, alarmManager)
        }
    }

    private fun userHasNotGrantedAlarmPermission(alarmManager: AlarmManager) =
        alarmManager.canScheduleExactAlarms()

    private fun redirectUserToSpecialAppAccessScreen() {
        INTENT_SCHEDULE_ALARM_PERMISSION.also {
            app.applicationContext.startActivity(it)
        }
    }

    private fun getStartReceiverIntent(alarm: Alarm) =
        Intent(app, NotificationReceiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
            putExtra(INTENT_AUTO_SILENCE, preferencesUiState.value.preferences.autoSilenceTime)
            putExtra(INTENT_TIME_FORMAT, preferencesUiState.value.preferences.is24HourFormat)
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
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getTimeInMillis(alarm),
            pendingIntent
        )
    }

    private fun stopAlarm(
        alarm: Alarm
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, NotificationReceiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
        }

        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    fun updateAllAlarmSubtitles(format: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value.alarms.forEach {
            updateExistingAlarmInDatabase(it.copy(subTitle = getAlarmTimeFormatted(it.date, format)))
        }
    }
    //endregion

    //region timer
    fun start() {
        if (isActive) {
            return
        }

        coroutineScope.launch {
            lastTimeStamp = System.currentTimeMillis()
            isActive = true
            isStart = false

            while (isActive) {
                delay(10L)
                timeMillis += System.currentTimeMillis() - lastTimeStamp
                lastTimeStamp = System.currentTimeMillis()
                formattedTime = formatTime(timeMillis)
            }
        }
    }

    fun pause() {
        isActive = false
    }

    fun resetTimer() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimeStamp = 0L
        formattedTime = "00:00:000"
        isActive = false
        isStart = true
    }

    private fun formatTime(timeMillis: Long): String {
        val localDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis),
            ZoneId.systemDefault()
        )
        val formatter = DateTimeFormatter.ofPattern("mm:ss:SSS", Locale.getDefault())
        return localDateTime.format(formatter)
    }
    //endregion
}
