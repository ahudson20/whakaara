package com.whakaara.feature.timer.reciever

import android.content.Context
import android.content.Intent
import com.whakaara.core.HiltBroadcastReceiver
import com.whakaara.core.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_PAUSE
import com.whakaara.core.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_START
import com.whakaara.core.constants.NotificationUtilsConstants.TIMER_RECEIVER_ACTION_STOP
import com.whakaara.core.constants.NotificationUtilsConstants.TIMER_RECEIVER_CURRENT_TIME_EXTRA
import com.whakaara.core.di.MainDispatcher
import com.whakaara.core.goAsync
import com.whakaara.data.timer.TimerRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@AndroidEntryPoint
class TimerReceiver : HiltBroadcastReceiver() {

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var repository: TimerRepository

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
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

    private fun startTimer(currentTime: Long) {
        repository.startTimer(currentTime = currentTime)
    }

    private fun pauseTimer() {
        repository.pauseTimer()
    }

    private suspend fun stopTimer() {
        repository.stopTimer()
    }
}
