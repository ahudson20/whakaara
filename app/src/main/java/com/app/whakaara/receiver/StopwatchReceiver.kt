package com.app.whakaara.receiver

import android.content.Context
import android.content.Intent
import com.app.whakaara.logic.StopwatchManagerWrapper
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.asExternalModel
import com.whakaara.core.HiltBroadcastReceiver
import com.whakaara.core.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_PAUSE
import com.whakaara.core.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_START
import com.whakaara.core.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP
import com.whakaara.core.goAsync
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class StopwatchReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var stopwatchManagerWrapper: StopwatchManagerWrapper

    @Inject
    lateinit var preferencesDatastore: PreferencesDataStoreRepository

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        val actionsList = listOf(STOPWATCH_RECEIVER_ACTION_START, STOPWATCH_RECEIVER_ACTION_PAUSE, STOPWATCH_RECEIVER_ACTION_STOP)
        if (!actionsList.contains(intent.action)) return

        goAsync {
            val state = preferencesDatastore.readStopwatchState().first()
            when (intent.action) {
                STOPWATCH_RECEIVER_ACTION_START -> {
                    startStopwatch(state = state.asExternalModel())
                }
                STOPWATCH_RECEIVER_ACTION_PAUSE -> {
                    pauseStopwatch(state = state.asExternalModel())
                }
                STOPWATCH_RECEIVER_ACTION_STOP -> {
                    stopStopwatch()
                }
            }
        }
    }

    private fun startStopwatch(state: StopwatchState) {
        with(stopwatchManagerWrapper) {
            recreateStopwatchActiveFromReceiver(state = state)
            cancelNotification()
            createStopwatchNotification()
        }
    }

    private fun pauseStopwatch(state: StopwatchState) {
        with(stopwatchManagerWrapper) {
            recreateStopwatchPausedFromReceiver(state = state)
            cancelNotification()
            pauseStopwatchNotification()
        }
    }

    private suspend fun stopStopwatch() {
        stopwatchManagerWrapper.resetStopwatch()
        preferencesDatastore.clearStopwatchState()
    }
}
