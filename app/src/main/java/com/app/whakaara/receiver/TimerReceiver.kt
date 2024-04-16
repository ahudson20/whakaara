package com.app.whakaara.receiver

import android.content.Context
import android.content.Intent
import com.app.whakaara.logic.TimerManagerWrapper
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_PAUSE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_START
import com.app.whakaara.utils.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_STOP
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerReceiver : HiltBroadcastReceiver() {

    @Inject
    lateinit var timerManagerWrapper: TimerManagerWrapper
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val actionsList = listOf(TIMER_RECEIVER_ACTION_PAUSE, TIMER_RECEIVER_ACTION_STOP, TIMER_RECEIVER_ACTION_START)
        if (!actionsList.contains(intent.action)) return

        goAsync {
            when (intent.action) {
                TIMER_RECEIVER_ACTION_START -> {
                    startTimer()
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

    private fun startTimer() {
        timerManagerWrapper.startTimer()
    }

    private fun pauseTimer() {
        timerManagerWrapper.pauseTimer()
    }

    private fun stopTimer() {
        timerManagerWrapper.resetTimer()
    }
}
