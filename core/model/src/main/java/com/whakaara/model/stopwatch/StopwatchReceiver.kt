package com.whakaara.model.stopwatch

sealed class StopwatchReceiver {
    data object Idle : StopwatchReceiver()
    data object Started : StopwatchReceiver()
    data object Paused : StopwatchReceiver()
    data object Stopped : StopwatchReceiver()
}
