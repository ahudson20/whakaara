package com.whakaara.model.timer

sealed class TimerStateReceiver {
    data object Idle : TimerStateReceiver()
    data class Started(val currentTime: Long) : TimerStateReceiver()
    data object Paused : TimerStateReceiver()
    data object Stopped : TimerStateReceiver()
}
