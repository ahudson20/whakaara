package com.whakaara.feature.stopwatch.receiver

import android.content.Context
import android.content.Intent
import com.whakaara.core.HiltBroadcastReceiver
import com.whakaara.core.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_PAUSE
import com.whakaara.core.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_START
import com.whakaara.core.constants.NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP
import com.whakaara.core.di.MainDispatcher
import com.whakaara.core.goAsync
import com.whakaara.data.stopwatch.StopwatchRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@AndroidEntryPoint
class StopwatchReceiver : HiltBroadcastReceiver() {
    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: StopwatchRepository

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        val actionsList = listOf(STOPWATCH_RECEIVER_ACTION_START, STOPWATCH_RECEIVER_ACTION_PAUSE, STOPWATCH_RECEIVER_ACTION_STOP)
        if (!actionsList.contains(intent.action)) return

        goAsync(
            coroutineContext = mainDispatcher
        ) {
            when (intent.action) {
                STOPWATCH_RECEIVER_ACTION_START -> {
                    startStopwatch()
                }

                STOPWATCH_RECEIVER_ACTION_PAUSE -> {
                    pauseStopwatch()
                }

                STOPWATCH_RECEIVER_ACTION_STOP -> {
                    stopStopwatch()
                }
            }
        }
    }

    private fun startStopwatch() {
        repository.startStopwatch()
    }

    private fun pauseStopwatch() {
        repository.pauseStopwatch()
    }

    private suspend fun stopStopwatch() {
        repository.stopStopwatch()
    }
}
