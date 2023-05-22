package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.whakaara.R
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
import java.util.Calendar
import java.util.Timer
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class MainViewModel @Inject constructor(
    private val app: Application,
    private val repository: AlarmRepository
) : AndroidViewModel(app) {

    //alarm
    private val _uiState = MutableStateFlow(AlarmState())
    val uiState: StateFlow<AlarmState> = _uiState.asStateFlow()

    //timer
    private var time: Duration = Duration.ZERO
    private lateinit var timer: Timer
    var millis by mutableStateOf("000")
    var seconds by mutableStateOf("00")
    var minutes by mutableStateOf("00")
    var hours by mutableStateOf("00")
    var isPlaying by mutableStateOf(false)
    var isStart by mutableStateOf(true)

    init {
        getAllAlarms()
    }

    private fun getAllAlarms() = viewModelScope.launch {
        repository.getAllAlarmsFlow().flowOn(Dispatchers.IO).collect { allAlarms ->
            _uiState.value = AlarmState(alarms = allAlarms)
        }
    }

    fun create(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        createAlarm(alarm)
        repository.insert(alarm)
        GeneralUtils.showToast(
            title = DateUtils.convertSecondsToHMm(
                seconds = TimeUnit.MILLISECONDS.toSeconds(
                    DateUtils.getDifferenceFromCurrentTimeInMillis(
                        hours = alarm.hour,
                        minutes = alarm.minute
                    )
                )
            ),
            context = app.applicationContext
        )
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        stopAlarm(alarm)
        repository.delete(alarm)
        GeneralUtils.showToast(
            title = app.getString(
                R.string.notification_action_deleted,
                alarm.title
            ),
            context = app.applicationContext
        )
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = false))
        stopAlarm(alarm)
        GeneralUtils.showToast(title = app.getString(R.string.notification_action_cancelled, alarm.title), context = app.applicationContext)
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = true))
        stopAlarm(alarm)
        createAlarm(alarm)
        GeneralUtils.showToast(
            title = DateUtils.convertSecondsToHMm(
                seconds = TimeUnit.MILLISECONDS.toSeconds(
                    DateUtils.getDifferenceFromCurrentTimeInMillis(
                        hours = alarm.hour,
                        minutes = alarm.minute
                    )
                )
            ),
            context = app.applicationContext
        )
    }

    fun reset(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        updateExistingAlarmInDatabase(alarm)
        stopAlarm(alarm)
        createAlarm(alarm)
    }

    fun snooze(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        val currentTimePlusTenMinutes = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 10)
        }
        stopAlarm(alarm)
        createAlarm(
            alarm.copy(
                hour = currentTimePlusTenMinutes.get(Calendar.HOUR_OF_DAY),
                minute = currentTimePlusTenMinutes.get(Calendar.MINUTE)
            )
        )
    }

    private fun updateExistingAlarmInDatabase(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(alarm)
    }

    private fun createAlarm(
        alarm: Alarm,
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!userHasNotGrantedAlarmPermission(alarmManager)) {
                redirectUserToSpecialAppAccessScreen()
            } else {
                setExactAlarm(alarm, alarmManager)
            }
        } else {
            setExactAlarm(alarm, alarmManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun userHasNotGrantedAlarmPermission(alarmManager: AlarmManager) =
        alarmManager.canScheduleExactAlarms()

    @RequiresApi(Build.VERSION_CODES.S)
    private fun redirectUserToSpecialAppAccessScreen() {
        Intent().apply {
            action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
        }.also {
            app.applicationContext.startActivity(it)
        }
    }

    private fun getStartReceiverIntent(alarm: Alarm) =
        Intent(app, Receiver::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarm.alarmId.toString()
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))
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
            DateUtils.getTimeInMillis(alarm),
            pendingIntent
        )
    }

    private fun stopAlarm(
        alarm: Alarm,
    ) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(app, Receiver::class.java).apply {
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

    //region timer
    fun start() {
        timer = fixedRateTimer(initialDelay = 1L, period = 1L) {
            time = time.plus(1.toDuration(DurationUnit.MILLISECONDS))
            updateTimeStates()
        }
        isPlaying = true
        isStart = false
    }

    private fun updateTimeStates() {
        time.toComponents { hours, minutes, seconds, nanoseconds ->
            this@MainViewModel.millis = (nanoseconds / 1000000).toString().padStart(3, '0')
            this@MainViewModel.seconds = seconds.pad()
            this@MainViewModel.minutes = minutes.pad()
            this@MainViewModel.hours = hours.pad()
        }
    }

    private fun Int.pad(): String {
        return this.toString().padStart(2, '0')
    }

    private fun Long.pad(): String {
        return this.toString().padStart(2, '0')
    }
    fun pause() {
        timer.cancel()
        isPlaying = false
    }

    fun stop() {
        pause()
        time = Duration.ZERO
        isStart = true
        updateTimeStates()
    }
    //endregion
}