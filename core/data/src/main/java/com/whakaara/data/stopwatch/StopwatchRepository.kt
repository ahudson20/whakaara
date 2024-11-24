package com.whakaara.data.stopwatch

import com.whakaara.model.stopwatch.StopwatchReceiver
import kotlinx.coroutines.flow.StateFlow

interface StopwatchRepository {
    val stopwatchState: StateFlow<StopwatchReceiver>
    fun startStopwatch()
    fun pauseStopwatch()
    suspend fun stopStopwatch()
}
