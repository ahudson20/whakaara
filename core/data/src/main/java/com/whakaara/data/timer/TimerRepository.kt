package com.whakaara.data.timer

import com.whakaara.model.timer.TimerStateReceiver
import kotlinx.coroutines.flow.StateFlow

interface TimerRepository {
    val timerState: StateFlow<TimerStateReceiver>
    fun startTimer(currentTime: Long)
    fun pauseTimer()
    suspend fun stopTimer()
}
