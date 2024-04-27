package com.app.whakaara.receiver

import android.content.Context
import android.content.Intent
import com.app.whakaara.logic.TimerManagerWrapper
import com.app.whakaara.module.MainDispatcher
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_PAUSE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_START
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_STOP
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_CURRENT_TIME_EXTRA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class TimerReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var timerManagerWrapper: TimerManagerWrapper

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val actionsList = listOf(TIMER_RECEIVER_ACTION_PAUSE, TIMER_RECEIVER_ACTION_STOP, TIMER_RECEIVER_ACTION_START)
        if (!actionsList.contains(intent.action)) return

        val currentTime = intent.getLongExtra(TIMER_RECEIVER_CURRENT_TIME_EXTRA, 0L)

        goAsync(
            coroutineContext = mainDispatcher
        ) {
            when (intent.action) {
                TIMER_RECEIVER_ACTION_START -> {
                    startTimer(
                        currentTime = currentTime
                    )
                }
                TIMER_RECEIVER_ACTION_PAUSE -> {
                    pauseTimer()
                }
                TIMER_RECEIVER_ACTION_STOP -> {
                    stopTimer()
                }
            }
        }
    }

    private fun startTimer(
        currentTime: Long
    ) {
        timerManagerWrapper.startTimer()
        timerManagerWrapper.startTimerNotificationCountdown(milliseconds = currentTime + Calendar.getInstance().timeInMillis)
    }

    private fun pauseTimer() {
        timerManagerWrapper.pauseTimer()
        timerManagerWrapper.pauseTimerNotificationCountdown()
    }

    private fun stopTimer() {
        timerManagerWrapper.resetTimer()
    }
}
