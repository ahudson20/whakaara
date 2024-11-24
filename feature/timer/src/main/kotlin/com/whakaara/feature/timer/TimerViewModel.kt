package com.whakaara.feature.timer

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.whakaara.core.CountDownTimerUtil
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.constants.GeneralConstants
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.di.ApplicationScope
import com.whakaara.core.di.IoDispatcher
import com.whakaara.core.di.MainDispatcher
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.data.timer.TimerRepository
import com.whakaara.feature.timer.reciever.TimerReceiver
import com.whakaara.feature.timer.service.TimerMediaService
import com.whakaara.feature.timer.util.DateUtils
import com.whakaara.model.datastore.TimerStateDataStore
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.timer.TimerState
import com.whakaara.model.timer.TimerStateReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val app: Application,
    private val alarmManager: AlarmManager,
    private val notificationManager: NotificationManager,
    @Named("timer")
    private val timerNotificationBuilder: NotificationCompat.Builder,
    private val countDownTimerUtil: CountDownTimerUtil,
    private val preferencesDatastore: PreferencesDataStoreRepository,
    private val timerRepository: TimerRepository,
    private val preferencesRepository: PreferencesRepository,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : AndroidViewModel(application = app) {

    private val _timerState: MutableStateFlow<TimerState> = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _preferences: MutableStateFlow<PreferencesState> = MutableStateFlow(PreferencesState())
    val preferences: StateFlow<PreferencesState> = _preferences.asStateFlow()

    init {
        collectPreferencesState()
        collectTimerStateFromReceiver()
    }

    private fun collectPreferencesState() = viewModelScope.launch {
        preferencesRepository.getPreferencesFlow().flowOn(ioDispatcher).collect { preferences ->
            _preferences.value = PreferencesState(preferences = preferences, isReady = true)
        }
    }

    private fun collectTimerStateFromReceiver() = viewModelScope.launch {
        timerRepository.timerState.collectLatest { state ->
            when (state) {
                is TimerStateReceiver.Idle -> {
                    Log.d("TimerViewModel", "TimerStateReceiver.Idle")
                }

                is TimerStateReceiver.Started -> {
                    startTimer()
                    startTimerNotificationCountdown(milliseconds = state.currentTime + Calendar.getInstance().timeInMillis)

                    Log.d("TimerViewModel", "TimerStateReceiver.Started")
                }

                is TimerStateReceiver.Paused -> {
                    pauseTimer()
                    pauseTimerNotificationCountdown()

                    Log.d("TimerViewModel", "TimerStateReceiver.Paused")
                }

                is TimerStateReceiver.Stopped -> {
                    resetTimer()

                    Log.d("TimerViewModel", "TimerStateReceiver.Stopped")
                }
            }
        }
    }

    //region mvm timer
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
        val currentTimeInMillis = Calendar.getInstance().timeInMillis
        if (timerState.value.isTimerPaused) {
            createTimerNotification(milliseconds = currentTimeInMillis + timerState.value.currentTime)
            startCountDownTimer(timeToCountDown = timerState.value.currentTime)
            updateTimerStateToStarted(millisecondsToAdd = timerState.value.currentTime)
        } else if (checkIfOneInputValueGreaterThanZero()) {
            val millisecondsFromTimerInput =
                DateUtils.generateMillisecondsFromTimerInputValues(
                    hours = timerState.value.inputHours,
                    minutes = timerState.value.inputMinutes,
                    seconds = timerState.value.inputSeconds
                )
            createTimerNotification(milliseconds = currentTimeInMillis + millisecondsFromTimerInput)
            startCountDownTimer(timeToCountDown = millisecondsFromTimerInput)
            updateTimerStateToStarted(millisecondsToAdd = millisecondsFromTimerInput)
        }
    }

    fun pauseTimer() {
        if (!_timerState.value.isTimerPaused) {
            cancelTimerAlarm()
            countDownTimerUtil.cancel()
            _timerState.update {
                it.copy(
                    isTimerPaused = true,
                    isTimerActive = false
                )
            }
        }
    }

    fun resetTimer() = viewModelScope.launch {
        cancelNotification()
        cancelTimerAlarm()
        countDownTimerUtil.cancel()
        _timerState.update {
            it.copy(
                isTimerPaused = false,
                isTimerActive = false,
                currentTime = GeneralConstants.ZERO_MILLIS,
                inputHours = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                inputMinutes = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                inputSeconds = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                isStart = true,
                progress = GeneralConstants.STARTING_CIRCULAR_PROGRESS,
                time = DateUtilsConstants.TIMER_STARTING_FORMAT,
                millisecondsFromTimerInput = GeneralConstants.ZERO_MILLIS
            )
        }
        preferencesDatastore.saveTimerData(
            state = TimerStateDataStore()
        )
    }

    fun restartTimer() {
        if (_preferences.value.preferences.autoRestartTimer) {
            cancelNotification()
            cancelTimerAlarm()
            countDownTimerUtil.cancel()
            _timerState.update {
                it.copy(
                    isTimerPaused = false,
                    isTimerActive = false,
                    currentTime = GeneralConstants.ZERO_MILLIS,
                    isStart = true,
                    progress = GeneralConstants.STARTING_CIRCULAR_PROGRESS,
                    time = DateUtilsConstants.TIMER_STARTING_FORMAT,
                    millisecondsFromTimerInput = GeneralConstants.ZERO_MILLIS
                )
            }
            startTimer()
        } else {
            cancelNotification()
            cancelTimerAlarm()
            countDownTimerUtil.cancel()
            _timerState.update {
                it.copy(
                    isTimerPaused = false,
                    isTimerActive = false,
                    isStart = true,
                    progress = GeneralConstants.STARTING_CIRCULAR_PROGRESS,
                    time = DateUtilsConstants.TIMER_STARTING_FORMAT,
                    currentTime = GeneralConstants.ZERO_MILLIS
                )
            }
        }
    }

    fun recreateTimer() = viewModelScope.launch(mainDispatcher) {
        if (timerState.value == TimerState()) {
            val status = preferencesDatastore.readTimerStatus().first()
            if (status != TimerStateDataStore()) {
                with(status) {
                    val difference = java.lang.System.currentTimeMillis() - timeStamp
                    if (remainingTimeInMillis > 0 && (remainingTimeInMillis > difference)) {
                        if (isActive) {
                            recreateActiveTimer(
                                milliseconds = remainingTimeInMillis - difference,
                                inputHours = inputHours,
                                inputMinutes = inputMinutes,
                                inputSeconds = inputSeconds
                            )
                        } else if (isPaused) {
                            recreatePausedTimer(
                                milliseconds = remainingTimeInMillis - difference,
                                inputHours = inputHours,
                                inputMinutes = inputMinutes,
                                inputSeconds = inputSeconds
                            )
                        }
                        preferencesDatastore.saveTimerData(
                            state = TimerStateDataStore()
                        )
                    }
                }
            }
        }
    }

    fun saveTimerStateForRecreation() = viewModelScope.launch(ioDispatcher) {
        if (!timerState.value.isStart) {
            preferencesDatastore.saveTimerData(
                TimerStateDataStore(
                    remainingTimeInMillis = timerState.value.currentTime,
                    isActive = timerState.value.isTimerActive,
                    isPaused = timerState.value.isTimerPaused,
                    timeStamp = System.currentTimeMillis(),
                    inputHours = timerState.value.inputHours,
                    inputMinutes = timerState.value.inputMinutes,
                    inputSeconds = timerState.value.inputSeconds
                )
            )
        }
    }

    fun startTimerNotification() {
        if (timerState.value.isTimerPaused) {
            pauseTimerNotificationCountdown()
        } else if (timerState.value.isTimerActive) {
            startTimerNotificationCountdown(
                milliseconds = timerState.value.currentTime + Calendar.getInstance().timeInMillis
            )
        }
    }

    fun cancelTimerNotification() {
        cancelNotification()
    }
    // endregion

    //region tmw
    fun recreateActiveTimer(
        milliseconds: Long,
        inputHours: String,
        inputMinutes: String,
        inputSeconds: String
    ) {
        countDownTimerUtil.cancel()
        startCountDownTimer(timeToCountDown = milliseconds)
        _timerState.update {
            it.copy(
                isTimerPaused = false,
                isStart = false,
                isTimerActive = true,
                millisecondsFromTimerInput = milliseconds,
                inputHours = inputHours,
                inputMinutes = inputMinutes,
                inputSeconds = inputSeconds
            )
        }
    }

    private fun updateTimerStateToStarted(millisecondsToAdd: Long) {
        _timerState.update {
            it.copy(
                isTimerPaused = false,
                isStart = false,
                isTimerActive = true,
                millisecondsFromTimerInput = millisecondsToAdd
            )
        }
    }

    private fun checkIfOneInputValueGreaterThanZero() =
        ((timerState.value.inputHours.toIntOrNull() ?: 0) > 0) ||
            ((timerState.value.inputMinutes.toIntOrNull() ?: 0) > 0) ||
            ((timerState.value.inputSeconds.toIntOrNull() ?: 0) > 0)

    private fun startCountDownTimer(timeToCountDown: Long) {
        countDownTimerUtil.countdown(
            period = timeToCountDown,
            onTickAction = { millisUntilFinished ->
                _timerState.update {
                    it.copy(
                        currentTime = millisUntilFinished,
                        progress = millisUntilFinished.toFloat() / timeToCountDown,
                        time = DateUtils.formatTimeForTimer(
                            millis = millisUntilFinished
                        )
                    )
                }
            },
            onFinishAction = {
                _timerState.update {
                    it.copy(
                        isTimerPaused = false,
                        isTimerActive = false,
                        currentTime = GeneralConstants.ZERO_MILLIS,
                        inputHours = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                        inputMinutes = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                        inputSeconds = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                        isStart = true,
                        progress = GeneralConstants.STARTING_CIRCULAR_PROGRESS,
                        time = DateUtilsConstants.TIMER_STARTING_FORMAT,
                        millisecondsFromTimerInput = GeneralConstants.ZERO_MILLIS
                    )
                }
                resetTimerStateDataStore()
            }
        )
    }

    fun recreatePausedTimer(
        milliseconds: Long,
        inputHours: String,
        inputMinutes: String,
        inputSeconds: String
    ) {
        _timerState.update {
            it.copy(
                isStart = false,
                isTimerActive = false,
                isTimerPaused = true,
                currentTime = milliseconds,
                millisecondsFromTimerInput = milliseconds,
                time = DateUtils.formatTimeForTimer(
                    millis = milliseconds
                ),
                inputHours = inputHours,
                inputMinutes = inputMinutes,
                inputSeconds = inputSeconds
            )
        }
    }

    private fun createTimerNotification(milliseconds: Long) {
        val startReceiverIntent =
            getStartReceiverIntent(
                alarmId = NotificationUtilsConstants.INTENT_TIMER_NOTIFICATION_ID,
                autoSilenceTime = NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES,
                type = NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
            )

        val pendingIntent =
            PendingIntentUtils.getService(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = startReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            milliseconds,
            pendingIntent
        )
    }

    private fun getStartReceiverIntent(
        autoSilenceTime: Int,
        action: Int = NotificationUtilsConstants.PLAY,
        type: Int,
        alarmId: String? = null
    ) = Intent(app, TimerMediaService::class.java).apply {
        this.action = alarmId
        putExtra(NotificationUtilsConstants.INTENT_AUTO_SILENCE, autoSilenceTime)
        putExtra(NotificationUtilsConstants.SERVICE_ACTION, action)
        putExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, type)
        putExtra(NotificationUtilsConstants.INTENT_ALARM_ID, alarmId)
    }

    fun startTimerNotificationCountdown(milliseconds: Long) {
        val pauseReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.TIMER_RECEIVER_ACTION_PAUSE)
        val stopTimerReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.TIMER_RECEIVER_ACTION_STOP)

        val pauseTimerReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = pauseReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )
        val stopTimerReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = stopTimerReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )
        timerNotificationBuilder.clearActions()
        notificationManager.notify(
            NotificationUtilsConstants.TIMER_NOTIFICATION_ID,
            timerNotificationBuilder.apply {
                setWhen(milliseconds)
                setUsesChronometer(true)
                setChronometerCountDown(true)
                setAutoCancel(false)
                setTimeoutAfter(milliseconds - System.currentTimeMillis())
                setCategory(NotificationCompat.CATEGORY_ALARM)
                setOngoing(true)
                setContentTitle(app.applicationContext.getString(R.string.timer_notification_title_active))
                setSubText(app.applicationContext.getString(R.string.timer_notification_sub_text_active))
                addAction(
                    0,
                    app.applicationContext.getString(R.string.notification_timer_pause_action_label),
                    pauseTimerReceiverPendingIntent
                )
                addAction(
                    0,
                    app.applicationContext.getString(R.string.notification_timer_stop_action_label),
                    stopTimerReceiverPendingIntent
                )
            }.build()
        )
    }

    fun pauseTimerNotificationCountdown() {
        val startTimerReceiverIntent =
            app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.TIMER_RECEIVER_ACTION_START).apply {
                putExtra(NotificationUtilsConstants.TIMER_RECEIVER_CURRENT_TIME_EXTRA, timerState.value.currentTime)
            }
        val stopTimerReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.TIMER_RECEIVER_ACTION_STOP)

        val playTimerReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = startTimerReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        val stopTimerReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = stopTimerReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        timerNotificationBuilder.clearActions()

        notificationManager.notify(
            NotificationUtilsConstants.TIMER_NOTIFICATION_ID,
            timerNotificationBuilder.apply {
                setWhen(System.currentTimeMillis())
                setUsesChronometer(false)
                setChronometerCountDown(false)
                setContentTitle(app.applicationContext.getString(R.string.timer_notification_title_paused))
                setSubText(app.applicationContext.getString(R.string.notification_sub_text_paused))
                addAction(
                    0,
                    app.applicationContext.getString(R.string.notification_timer_play_action_label),
                    playTimerReceiverPendingIntent
                )
                addAction(
                    0,
                    app.applicationContext.getString(R.string.notification_timer_stop_action_label),
                    stopTimerReceiverPendingIntent
                )
            }.build()
        )
    }

    fun cancelTimerAlarm() {
        val startReceiverIntent =
            Intent(app, TimerMediaService::class.java).apply {
                this.action = NotificationUtilsConstants.INTENT_TIMER_NOTIFICATION_ID
            }

        val pendingIntent =
            PendingIntentUtils.getService(
                context = app,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = startReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.cancel(pendingIntent)
    }

    private fun cancelNotification() {
        notificationManager.cancel(NotificationUtilsConstants.TIMER_NOTIFICATION_ID)
    }

    private fun resetTimerStateDataStore() {
        try {
            coroutineScope.launch {
                preferencesDatastore.saveTimerData(
                    state = TimerStateDataStore()
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            Log.e(GeneralConstants.RESET_TIMER_DATASTORE_TAG, "resetTimerStateDataStore execution failed", t)
        } finally {
            // Nothing can be in the `finally` block after this, as this throws a
            // `CancellationException`
            coroutineScope.cancel()
        }
    }

    companion object {
        fun Context.getTimerReceiverIntent(intentAction: String): Intent {
            return Intent(this, TimerReceiver::class.java).apply {
                action = intentAction
            }
        }
    }
    // endregion
}
