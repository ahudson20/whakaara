package com.whakaara.model.stopwatch

sealed class StopwatchReceiverState {
    data object Idle : StopwatchReceiverState()
    data object Started : StopwatchReceiverState()
    data object Paused : StopwatchReceiverState()
    data object Stopped : StopwatchReceiverState()
}
