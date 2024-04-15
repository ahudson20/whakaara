package com.app.whakaara.logic

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.receiver.StopwatchReceiver
import com.app.whakaara.state.Lap
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.DateUtilsConstants.STOPWATCH_STARTING_TIME
import com.app.whakaara.utils.constants.GeneralConstants.TIMER_START_DELAY_MILLIS
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_NOTIFICATION_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_PAUSE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_START
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class StopwatchManagerWrapper @Inject constructor(
    private val app: Application,
    private val notificationManager: NotificationManager,
    @Named("stopwatch")
    private val stopwatchNotificationBuilder: NotificationCompat.Builder,
    private val coroutineScope: CoroutineScope
) {
    val stopwatchState = MutableStateFlow(StopwatchState())

    fun startStopwatch() {
        if (stopwatchState.value.isActive) {
            return
        }

        coroutineScope.launch {
            val lastTimeStamp = System.currentTimeMillis()
            val setWhen = if (stopwatchState.value.isStart) {
                lastTimeStamp
            } else {
                lastTimeStamp - stopwatchState.value.timeMillis
            }

            createStopwatchNotification(milliseconds = setWhen)

            stopwatchState.update {
                it.copy(
                    lastTimeStamp = lastTimeStamp,
                    isActive = true,
                    isStart = false
                )
            }

            while (stopwatchState.value.isActive) {
                delay(TIMER_START_DELAY_MILLIS)
                val time = stopwatchState.value.timeMillis + (System.currentTimeMillis() - stopwatchState.value.lastTimeStamp)
                stopwatchState.update {
                    it.copy(
                        timeMillis = time,
                        lastTimeStamp = System.currentTimeMillis(),
                        formattedTime = DateUtils.formatTimeForStopwatch(
                            millis = time
                        )
                    )
                }
            }
        }
    }

    fun pauseStopwatch() {
        stopwatchState.update {
            it.copy(
                isActive = false
            )
        }
        pauseStopwatchNotification()
    }

    fun resetStopwatch() {
        coroutineScope.coroutineContext.cancelChildren()
        cancelNotification()
        stopwatchState.update {
            it.copy(
                timeMillis = ZERO_MILLIS,
                lastTimeStamp = ZERO_MILLIS,
                formattedTime = STOPWATCH_STARTING_TIME,
                isActive = false,
                isStart = true,
                lapList = mutableListOf()
            )
        }
    }

    fun lapStopwatch() {
        val diff: Long
        val current = stopwatchState.value.timeMillis
        diff = if (stopwatchState.value.lapList.isEmpty()) {
            current
        } else {
            current - stopwatchState.value.lapList.last().time
        }
        val nextLap = Lap(
            time = current,
            diff = diff
        )
        val newList = stopwatchState.value.lapList.plus(nextLap).toMutableList()
        stopwatchState.update {
            it.copy(
                lapList = newList
            )
        }
    }

    private fun createStopwatchNotification(
        milliseconds: Long
    ) {
        val pauseReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_PAUSE)
        val stopReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_STOP)

        val pauseReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = pauseReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = stopReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationManager.notify(
            STOPWATCH_NOTIFICATION_ID,
            stopwatchNotificationBuilder.apply {
                clearActions()
                setWhen(milliseconds)
                setUsesChronometer(true)
                setSubText(app.applicationContext.getString(R.string.stopwatch_notification_sub_text))
                addAction(0, app.applicationContext.getString(R.string.notification_timer_pause_action_label), pauseReceiverPendingIntent)
                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopReceiverPendingIntent)
            }.build()
        )
    }

    private fun pauseStopwatchNotification() {
        val startReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_START)
        val stopReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_STOP)

        val playReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = startReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopReceiverPendingIntent = PendingIntentUtils.getBroadcast(
            context = app.applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = stopReceiverIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationManager.notify(
            STOPWATCH_NOTIFICATION_ID,
            stopwatchNotificationBuilder.apply {
                clearActions()
                setUsesChronometer(false)
                setSubText(app.applicationContext.getString(R.string.notification_sub_text_pasused))
                addAction(0, app.applicationContext.getString(R.string.notification_timer_play_action_label), playReceiverPendingIntent)
                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopReceiverPendingIntent)
            }.build()
        )
    }

    private fun cancelNotification() {
        notificationManager.cancel(STOPWATCH_NOTIFICATION_ID)
    }

    companion object {
        fun Context.getTimerReceiverIntent(intentAction: String): Intent {
            return Intent(this, StopwatchReceiver::class.java).apply {
                action = intentAction
            }
        }
    }
}
