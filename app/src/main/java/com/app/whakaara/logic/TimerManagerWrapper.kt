package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.data.datastore.PreferencesDataStore
import com.app.whakaara.receiver.TimerReceiver
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.state.TimerState
import com.app.whakaara.state.TimerStateDataStore
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_STARTING_FORMAT
import com.app.whakaara.utils.constants.GeneralConstants.STARTING_CIRCULAR_PROGRESS
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_TIMER_NOTIFICATION_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
import com.app.whakaara.utils.constants.NotificationUtilsConstants.SERVICE_ACTION
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_NOTIFICATION_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_PAUSE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_START
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_STOP
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_CURRENT_TIME_EXTRA
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.cancellation.CancellationException

class TimerManagerWrapper @Inject constructor(
    private val app: Application,
    private val alarmManager: AlarmManager,
    private val notificationManager: NotificationManager,
    @Named("timer")
    private val timerNotificationBuilder: NotificationCompat.Builder,
    private val countDownTimerUtil: CountDownTimerUtil,
    private val preferencesDatastore: PreferencesDataStore,
    private val coroutineScope: CoroutineScope
) {
    val timerState = MutableStateFlow(TimerState())

    fun updateInputHours(newValue: String) {
        timerState.update {
            it.copy(
                inputHours = newValue
            )
        }
    }

    fun updateInputMinutes(newValue: String) {
        timerState.update {
            it.copy(
                inputMinutes = newValue
            )
        }
    }

    fun updateInputSeconds(newValue: String) {
        timerState.update {
            it.copy(
                inputSeconds = newValue
            )
        }
    }

    fun recreateActiveTimer(
        milliseconds: Long
    ) {
        countDownTimerUtil.cancel()
        startCountDownTimer(timeToCountDown = milliseconds)
        timerState.update {
            it.copy(
                isTimerPaused = false,
                isStart = false,
                isTimerActive = true,
                millisecondsFromTimerInput = milliseconds,
                inputHours = TimeUnit.MILLISECONDS.toHours(milliseconds).toString(),
                inputMinutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds).toString(),
                inputSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds).toString()
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
            val millisecondsFromTimerInput = DateUtils.generateMillisecondsFromTimerInputValues(
                hours = timerState.value.inputHours,
                minutes = timerState.value.inputMinutes,
                seconds = timerState.value.inputSeconds
            )
            createTimerNotification(milliseconds = currentTimeInMillis + millisecondsFromTimerInput)
            startCountDownTimer(timeToCountDown = millisecondsFromTimerInput)
            updateTimerStateToStarted(millisecondsToAdd = millisecondsFromTimerInput)
        }
    }

    private fun updateTimerStateToStarted(
        millisecondsToAdd: Long
    ) {
        timerState.update {
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

    private fun startCountDownTimer(
        timeToCountDown: Long
    ) {
        countDownTimerUtil.countdown(
            period = timeToCountDown,
            onTickAction = { millisUntilFinished ->
                timerState.update {
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
                timerState.update {
                    it.copy(
                        isTimerPaused = false,
                        isTimerActive = false,
                        currentTime = ZERO_MILLIS,
                        inputHours = TIMER_INPUT_INITIAL_VALUE,
                        inputMinutes = TIMER_INPUT_INITIAL_VALUE,
                        inputSeconds = TIMER_INPUT_INITIAL_VALUE,
                        isStart = true,
                        progress = STARTING_CIRCULAR_PROGRESS,
                        time = TIMER_STARTING_FORMAT,
                        millisecondsFromTimerInput = ZERO_MILLIS
                    )
                }
                resetTimerStateDataStore()
            }
        )
    }

    fun recreatePausedTimer(
        milliseconds: Long
    ) {
        timerState.update {
            it.copy(
                isStart = false,
                isTimerActive = false,
                isTimerPaused = true,
                currentTime = milliseconds,
                millisecondsFromTimerInput = milliseconds,
                time = DateUtils.formatTimeForTimer(
                    millis = milliseconds
                )
            )
        }
    }

    fun pauseTimer() {
        if (!timerState.value.isTimerPaused) {
            cancelTimerAlarm()
            countDownTimerUtil.cancel()
            timerState.update {
                it.copy(
                    isTimerPaused = true,
                    isTimerActive = false
                )
            }
        }
    }

    fun resetTimer() {
        cancelNotification()
        cancelTimerAlarm()
        countDownTimerUtil.cancel()
        timerState.update {
            it.copy(
                isTimerPaused = false,
                isTimerActive = false,
                currentTime = ZERO_MILLIS,
                inputHours = TIMER_INPUT_INITIAL_VALUE,
                inputMinutes = TIMER_INPUT_INITIAL_VALUE,
                inputSeconds = TIMER_INPUT_INITIAL_VALUE,
                isStart = true,
                progress = STARTING_CIRCULAR_PROGRESS,
                time = TIMER_STARTING_FORMAT,
                millisecondsFromTimerInput = ZERO_MILLIS
            )
        }
        resetTimerStateDataStore()
    }

    fun restartTimer(
        autoRestartTimer: Boolean
    ) {
        if (autoRestartTimer) {
            cancelNotification()
            cancelTimerAlarm()
            countDownTimerUtil.cancel()
            timerState.update {
                it.copy(
                    isTimerPaused = false,
                    isTimerActive = false,
                    currentTime = ZERO_MILLIS,
                    isStart = true,
                    progress = STARTING_CIRCULAR_PROGRESS,
                    time = TIMER_STARTING_FORMAT,
                    millisecondsFromTimerInput = ZERO_MILLIS
                )
            }
            startTimer()
        } else {
            cancelNotification()
            cancelTimerAlarm()
            countDownTimerUtil.cancel()
            timerState.update {
                it.copy(
                    isTimerPaused = false,
                    isTimerActive = false,
                    isStart = true,
                    progress = STARTING_CIRCULAR_PROGRESS,
                    time = TIMER_STARTING_FORMAT,
                    currentTime = ZERO_MILLIS
                )
            }
        }
    }

    private fun createTimerNotification(
        milliseconds: Long
    ) {
        val startReceiverIntent = getStartReceiverIntent(
            alarmId = INTENT_TIMER_NOTIFICATION_ID,
            autoSilenceTime = ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES,
            type = NOTIFICATION_TYPE_TIMER
        )

        val pendingIntent = PendingIntentUtils.getService(
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

    private fun getStartReceiverIntent(
        autoSilenceTime: Int,
        action: Int = NotificationUtilsConstants.PLAY,
        type: Int,
        alarmId: String? = null
    ) =
        Intent(app, MediaPlayerService::class.java).apply {
            this.action = alarmId
            putExtra(INTENT_AUTO_SILENCE, autoSilenceTime)
            putExtra(SERVICE_ACTION, action)
            putExtra(NOTIFICATION_TYPE, type)
            putExtra(INTENT_ALARM_ID, alarmId)
        }

    fun startTimerNotificationCountdown(
        milliseconds: Long
    ) {
        val pauseReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = TIMER_RECEIVER_ACTION_PAUSE)
        val stopTimerReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = TIMER_RECEIVER_ACTION_STOP)

        val pauseTimerReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = pauseReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopTimerReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = stopTimerReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        timerNotificationBuilder.clearActions()
        notificationManager.notify(
            TIMER_NOTIFICATION_ID,
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
                addAction(0, app.applicationContext.getString(R.string.notification_timer_pause_action_label), pauseTimerReceiverPendingIntent)
                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopTimerReceiverPendingIntent)
            }.build()
        )
    }

    fun pauseTimerNotificationCountdown() {
        val startTimerReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = TIMER_RECEIVER_ACTION_START).apply { putExtra(TIMER_RECEIVER_CURRENT_TIME_EXTRA, timerState.value.currentTime) }
        val stopTimerReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = TIMER_RECEIVER_ACTION_STOP)

        val playTimerReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = startTimerReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopTimerReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = stopTimerReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        timerNotificationBuilder.clearActions()

        notificationManager.notify(
            TIMER_NOTIFICATION_ID,
            timerNotificationBuilder.apply {
                setWhen(System.currentTimeMillis())
                setUsesChronometer(false)
                setChronometerCountDown(false)
                setContentTitle(app.applicationContext.getString(R.string.timer_notification_title_paused))
                setSubText(app.applicationContext.getString(R.string.notification_sub_text_paused))
                addAction(0, app.applicationContext.getString(R.string.notification_timer_play_action_label), playTimerReceiverPendingIntent)
                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopTimerReceiverPendingIntent)
            }.build()
        )
    }

    fun cancelTimerAlarm() {
        val startReceiverIntent = Intent(app, MediaPlayerService::class.java).apply {
            this.action = INTENT_TIMER_NOTIFICATION_ID
        }

        val pendingIntent = PendingIntentUtils.getService(
            context = app,
            id = INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    fun cancelNotification() {
        notificationManager.cancel(TIMER_NOTIFICATION_ID)
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
            Log.e("resetTimerStateDataStoreTAG", "resetTimerStateDataStore execution failed", t)
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
}
