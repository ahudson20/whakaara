package com.app.whakaara.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.whakaara.logic.StopwatchManagerWrapper
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_PAUSE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_START
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StopwatchReceiver : BroadcastReceiver() {

    @Inject
    lateinit var stopwatchManagerWrapper: StopwatchManagerWrapper
    override fun onReceive(context: Context, intent: Intent) {
        val actionsList = listOf(STOPWATCH_RECEIVER_ACTION_START, STOPWATCH_RECEIVER_ACTION_PAUSE, STOPWATCH_RECEIVER_ACTION_STOP)
        if (!actionsList.contains(intent.action)) return

        goAsync {
            when (intent.action) {
                STOPWATCH_RECEIVER_ACTION_START -> startStopwatch()
                STOPWATCH_RECEIVER_ACTION_PAUSE -> pauseStopwatch()
                STOPWATCH_RECEIVER_ACTION_STOP -> stopStopwatch()
            }
        }
    }

    private fun startStopwatch() {
        stopwatchManagerWrapper.startStopwatch()
    }

    private fun pauseStopwatch() {
        stopwatchManagerWrapper.pauseStopwatch()
    }

    private fun stopStopwatch() {
        stopwatchManagerWrapper.resetStopwatch()
    }
}
