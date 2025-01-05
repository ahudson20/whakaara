package com.whakaara.data.stopwatch

import com.whakaara.model.stopwatch.StopwatchReceiverState
import kotlinx.coroutines.flow.StateFlow

interface StopwatchRepository {
    val stopwatchState: StateFlow<StopwatchReceiverState>
    fun startStopwatch()
    fun pauseStopwatch()
    suspend fun stopStopwatch()
}
