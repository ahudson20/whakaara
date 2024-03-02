package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.os.CountDownTimer
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.receiver.NotificationReceiver
import com.app.whakaara.receiver.TimerNotificationReceiver
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.utils.DateUtils.Companion.formatTimeTimerAndStopwatch
import com.app.whakaara.utils.DateUtils.Companion.getAlarmTimeFormatted
import com.app.whakaara.utils.DateUtils.Companion.getTimeInMillis
import com.app.whakaara.utils.DateUtils.Companion.hoursToMilliseconds
import com.app.whakaara.utils.DateUtils.Companion.minutesToMilliseconds
import com.app.whakaara.utils.DateUtils.Companion.secondsToMilliseconds
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.DateUtilsConstants
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_STARTING_FORMAT
import com.app.whakaara.utils.constants.GeneralConstants.STARTING_CIRCULAR_PROGRESS
import com.app.whakaara.utils.constants.GeneralConstants.TIMER_INTERVAL
import com.app.whakaara.utils.constants.GeneralConstants.TIMER_START_DELAY_MILLIS
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
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
    private val app: Application,
    private val repository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository,
    private val alarmManager: AlarmManager
) : AndroidViewModel(app) {

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

    private fun updateExistingAlarmInDatabase(alarm: Alarm) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(alarm)
        }

    private fun createAlarm(
        alarm: Alarm
    ) {
        if (!userHasNotGrantedAlarmPermission()) {
            redirectUserToSpecialAppAccessScreen()
        } else {
            setExactAlarm(alarm, alarmManager)
        }
    }

    private fun userHasNotGrantedAlarmPermission() =
        alarmManager.canScheduleExactAlarms()

    private fun redirectUserToSpecialAppAccessScreen() {
        Intent().apply { action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM }.also {
            app.applicationContext.startActivity(it)
        }
    }

    private fun getStartReceiverIntent(alarm: Alarm) =
        Intent(app, NotificationReceiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
//            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
            putExtra(INTENT_AUTO_SILENCE, preferencesUiState.value.preferences.autoSilenceTime)
//            putExtra(INTENT_TIME_FORMAT, preferencesUiState.value.preferences.is24HourFormat)
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
            getTimeInMillis(alarm.date),
            pendingIntent
        )
    }

    private fun stopAlarm(
        alarm: Alarm
    ) {
        val intent = Intent(app, NotificationReceiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
//            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
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
        if (!userHasNotGrantedAlarmPermission()) {
            redirectUserToSpecialAppAccessScreen()
        } else {
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
                createTimerNotification(milliseconds = millisecondsFromTimerInput)
            }
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

    private fun generateMillisecondsFromTimerInputValues(
        hours: String,
        minutes: String,
        seconds: String
    ): Long {
        var millis = ZERO_MILLIS
        millis += hoursToMilliseconds(hours = hours.toIntOrNull() ?: 0)
        millis += minutesToMilliseconds(minutes = minutes.toIntOrNull() ?: 0)
        millis += secondsToMilliseconds(seconds = seconds.toIntOrNull() ?: 0)
        return millis
    }

    private fun createTimerNotification(
        milliseconds: Long
    ) {
        val startReceiverIntent = Intent(app, TimerNotificationReceiver::class.java)

        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            milliseconds,
            pendingIntent
        )
    }

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
