package com.app.whakaara.logic

import android.os.CountDownTimer
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
import com.app.whakaara.utils.DateUtils.Companion.formatTimeTimerAndStopwatch
import com.app.whakaara.utils.DateUtils.Companion.generateMillisecondsFromTimerInputValues
import com.app.whakaara.utils.DateUtils.Companion.getAlarmTimeFormatted
import com.app.whakaara.utils.constants.DateUtilsConstants
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_STARTING_FORMAT
import com.app.whakaara.utils.constants.GeneralConstants.STARTING_CIRCULAR_PROGRESS
import com.app.whakaara.utils.constants.GeneralConstants.TIMER_INTERVAL
import com.app.whakaara.utils.constants.GeneralConstants.TIMER_START_DELAY_MILLIS
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository,
    private val alarmManagerWrapper: AlarmManagerWrapper
) : ViewModel() {

    // alarm
    private val _alarmState = MutableStateFlow(AlarmState())
    val alarmState: StateFlow<AlarmState> = _alarmState.asStateFlow()

    // preferences
    private val _preferencesState = MutableStateFlow(PreferencesState())
    val preferencesUiState: StateFlow<PreferencesState> = _preferencesState.asStateFlow()

    // stopwatch
    private var coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _stopwatchState = MutableStateFlow(StopwatchState())
    val stopwatchState: StateFlow<StopwatchState> = _stopwatchState.asStateFlow()

    // timer
    private var countDownTimer: CountDownTimer? = null

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

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
            _alarmState.value = AlarmState(alarms = allAlarms)
        }
    }

    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmManagerWrapper.createAlarm(
            alarmId = alarm.alarmId.toString(),
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime,
            date = alarm.date
        )
        repository.insert(alarm)
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmManagerWrapper.stopAlarm(alarmId = alarm.alarmId.toString())
        repository.delete(alarm)
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = false))
        alarmManagerWrapper.stopAlarm(alarmId = alarm.alarmId.toString())
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = true))
        alarmManagerWrapper.stopAlarm(alarmId = alarm.alarmId.toString())
        alarmManagerWrapper.createAlarm(
            alarmId = alarm.alarmId.toString(),
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime,
            date = alarm.date
        )
    }

    fun reset(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm)
        alarmManagerWrapper.stopAlarm(alarmId = alarm.alarmId.toString())
        alarmManagerWrapper.createAlarm(
            alarmId = alarm.alarmId.toString(),
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime,
            date = alarm.date
        )
    }

    fun snooze(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        val currentTimePlusTenMinutes = Calendar.getInstance().apply {
            add(Calendar.MINUTE, _preferencesState.value.preferences.snoozeTime)
        }
        alarmManagerWrapper.stopAlarm(alarmId = alarm.alarmId.toString())
        alarmManagerWrapper.createAlarm(
            alarmId = alarm.alarmId.toString(),
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime,
            date = currentTimePlusTenMinutes
        )
    }

    private fun updateExistingAlarmInDatabase(alarm: Alarm) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(alarm)
        }

    fun updateAllAlarmSubtitles(format: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _alarmState.value.alarms.forEach {
            updateExistingAlarmInDatabase(
                it.copy(
                    subTitle = getAlarmTimeFormatted(
                        it.date,
                        format
                    )
                )
            )
        }
    }
    //endregion

    //region stopwatch
    fun startStopwatch() {
        if (_stopwatchState.value.isActive) {
            return
        }

        coroutineScope.launch {
            _stopwatchState.update {
                it.copy(
                    lastTimeStamp = System.currentTimeMillis(),
                    isActive = true,
                    isStart = false
                )
            }

            while (_stopwatchState.value.isActive) {
                delay(TIMER_START_DELAY_MILLIS)
                val time = _stopwatchState.value.timeMillis + (System.currentTimeMillis() - _stopwatchState.value.lastTimeStamp)
                _stopwatchState.update {
                    it.copy(
                        timeMillis = time,
                        lastTimeStamp = System.currentTimeMillis(),
                        formattedTime = formatTimeTimerAndStopwatch(time)
                    )
                }
            }
        }
    }

    fun pauseStopwatch() {
        _stopwatchState.update {
            it.copy(
                isActive = false
            )
        }
    }

    fun resetStopwatch() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        _stopwatchState.update {
            it.copy(
                timeMillis = ZERO_MILLIS,
                lastTimeStamp = ZERO_MILLIS,
                formattedTime = TIMER_STARTING_FORMAT,
                isActive = false,
                isStart = true
            )
        }
    }
    //endregion

    //region timer
    fun updateInputHours(newValue: String) {
        _timerState.update {
            it.copy(
                inputHours = newValue
            )
        }
    }

    fun updateInputMinutes(newValue: String) {
        _timerState.update {
            it.copy(
                inputMinutes = newValue
            )
        }
    }

    fun updateInputSeconds(newValue: String) {
        _timerState.update {
            it.copy(
                inputSeconds = newValue
            )
        }
    }

    fun startTimer() {
        if (_timerState.value.isTimerPaused) {
            startCountDownTimer(
                timeToCountDown = _timerState.value.currentTime
            )
            updateTimerStateToStarted()
        } else if (checkIfOneInputValueGreaterThanZero()) {
            val millisecondsFromTimerInput = generateMillisecondsFromTimerInputValues(
                hours = _timerState.value.inputHours,
                minutes = _timerState.value.inputMinutes,
                seconds = _timerState.value.inputSeconds
            )
            startCountDownTimer(
                timeToCountDown = millisecondsFromTimerInput
            )
            updateTimerStateToStarted()
            alarmManagerWrapper.createTimerNotification(milliseconds = millisecondsFromTimerInput)
        }
    }

    private fun updateTimerStateToStarted() {
        _timerState.update {
            it.copy(
                isTimerPaused = false,
                isStart = false,
                isTimerActive = true
            )
        }
    }

    private fun checkIfOneInputValueGreaterThanZero() =
        ((_timerState.value.inputHours.toIntOrNull() ?: 0) > 0) ||
            ((_timerState.value.inputMinutes.toIntOrNull() ?: 0) > 0) ||
            ((_timerState.value.inputSeconds.toIntOrNull() ?: 0) > 0)

    private fun startCountDownTimer(
        timeToCountDown: Long
    ) {
        countDownTimer = object : CountDownTimer(timeToCountDown, TIMER_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                _timerState.update {
                    it.copy(
                        currentTime = millisUntilFinished,
                        progress = millisUntilFinished.toFloat() / timeToCountDown,
                        time = formatTimeTimerAndStopwatch(millisUntilFinished)
                    )
                }
            }

            override fun onFinish() {
                _timerState.update {
                    it.copy(
                        isTimerPaused = false,
                        isTimerActive = false,
                        currentTime = ZERO_MILLIS,
                        inputHours = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                        inputMinutes = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                        inputSeconds = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                        isStart = true,
                        progress = STARTING_CIRCULAR_PROGRESS,
                        time = TIMER_STARTING_FORMAT
                    )
                }
            }
        }.start()
    }

    fun pauseTimer() {
        if (!_timerState.value.isTimerPaused) {
            countDownTimer?.cancel()
            _timerState.update {
                it.copy(
                    isTimerPaused = true,
                    isTimerActive = false
                )
            }
        }
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        _timerState.update {
            it.copy(
                isTimerPaused = false,
                isTimerActive = false,
                currentTime = ZERO_MILLIS,
                inputHours = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                inputMinutes = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                inputSeconds = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                isStart = true,
                progress = STARTING_CIRCULAR_PROGRESS,
                time = TIMER_STARTING_FORMAT
            )
        }
    }
    //endregion
}
