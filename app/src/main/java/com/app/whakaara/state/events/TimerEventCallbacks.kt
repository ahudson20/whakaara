package com.app.whakaara.state.events

interface TimerEventCallbacks {
    fun updateHours(newValue: String)

    fun updateMinutes(newValue: String)

    fun updateSeconds(newValue: String)

    fun startTimer()

    fun stopTimer()

    fun pauseTimer()

    fun restartTimer(autoRestartTimer: Boolean)
}
