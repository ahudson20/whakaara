package com.app.whakaara.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.utils.DateUtils.Companion.getAlarmTimeFormatted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository,
    private val alarmManagerWrapper: AlarmManagerWrapper,
    private val timerManagerWrapper: TimerManagerWrapper,
    private val stopwatchManagerWrapper: StopwatchManagerWrapper
) : ViewModel() {

    // alarm
    private val _alarmState = MutableStateFlow<AlarmState>(AlarmState.Loading)
    val alarmState: StateFlow<AlarmState> = _alarmState.asStateFlow()

    // preferences
    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()
    private val _preferencesState = MutableStateFlow(PreferencesState())
    val preferencesUiState: StateFlow<PreferencesState> = _preferencesState.asStateFlow()

    // stopwatch
    val stopwatchState: StateFlow<StopwatchState> = stopwatchManagerWrapper.stopwatchState.asStateFlow()

    // timer
    val timerState: StateFlow<TimerState> = timerManagerWrapper.timerState.asStateFlow()

    init {
        getAllAlarms()
        getPreferences()
    }

    //region preferences
    private fun getPreferences() = viewModelScope.launch {
        preferencesRepository.getPreferencesFlow().flowOn(Dispatchers.IO).collect { preferences ->
            _preferencesState.value = PreferencesState(preferences = preferences)
            _isReady.value = true
        }
    }

    fun updatePreferences(preferences: Preferences) = viewModelScope.launch(Dispatchers.IO) {
        preferencesRepository.updatePreferences(preferences = preferences)
    }
    //endregion

    //region alarm
    private fun getAllAlarms() = viewModelScope.launch {
        repository.getAllAlarmsFlow().flowOn(Dispatchers.IO).collect { allAlarms ->
            _alarmState.value = AlarmState.Success(alarms = allAlarms)
        }
    }

    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(alarm = alarm)
        alarmManagerWrapper.createAlarm(
            alarmId = alarm.alarmId.toString(),
            date = alarm.date,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily
        )
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(alarm = alarm)
        alarmManagerWrapper.deleteAlarm(alarmId = alarm.alarmId.toString())
        alarmManagerWrapper.stopUpcomingAlarmNotification(alarmId = alarm.alarmId.toString(), alarmDate = alarm.date)
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = false))
        alarmManagerWrapper.deleteAlarm(alarmId = alarm.alarmId.toString())
        alarmManagerWrapper.stopUpcomingAlarmNotification(alarmId = alarm.alarmId.toString(), alarmDate = alarm.date)
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = true))
        alarmManagerWrapper.stopStartUpdateWidget(
            alarmId = alarm.alarmId.toString(),
            date = alarm.date,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily
        )
    }

    fun reset(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm = alarm)
        alarmManagerWrapper.stopStartUpdateWidget(
            alarmId = alarm.alarmId.toString(),
            date = alarm.date,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily
        )
    }

    fun snooze(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        val currentTimePlusTenMinutes = Calendar.getInstance().apply {
            add(Calendar.MINUTE, _preferencesState.value.preferences.snoozeTime.value)
        }
        alarmManagerWrapper.stopStartUpdateWidget(
            alarmId = alarm.alarmId.toString(),
            date = currentTimePlusTenMinutes,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily
        )
    }

    private fun updateExistingAlarmInDatabase(alarm: Alarm) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(alarm)
        }

    fun updateAllAlarmSubtitles(format: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val state = _alarmState.value
        if (state is AlarmState.Success) {
            state.alarms.forEach {
                updateExistingAlarmInDatabase(
                    it.copy(
                        subTitle = getAlarmTimeFormatted(
                            it.date,
                            format
                        )
                    )
                )
            }
            alarmManagerWrapper.updateWidget()
        }
    }
    //endregion

    //region stopwatch
    fun startStopwatch() {
        stopwatchManagerWrapper.startStopwatch()
    }

    fun pauseStopwatch() {
        stopwatchManagerWrapper.pauseStopwatch()
    }

    fun resetStopwatch() {
        stopwatchManagerWrapper.resetStopwatch()
    }

    fun lapStopwatch() {
        stopwatchManagerWrapper.lapStopwatch()
    }
    //endregion

    //region timer
    fun updateInputHours(newValue: String) {
        timerManagerWrapper.updateInputHours(newValue)
    }

    fun updateInputMinutes(newValue: String) {
        timerManagerWrapper.updateInputMinutes(newValue)
    }

    fun updateInputSeconds(newValue: String) {
        timerManagerWrapper.updateInputSeconds(newValue)
    }

    fun startTimer() {
        timerManagerWrapper.startTimer()
    }

    fun pauseTimer() {
        timerManagerWrapper.pauseTimer()
    }

    fun resetTimer() {
        timerManagerWrapper.resetTimer()
    }

    fun restartTimer() {
        timerManagerWrapper.restartTimer()
    }
    // endregion
}
