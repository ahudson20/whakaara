package com.app.whakaara.state.events

interface StopwatchEventCallbacks {
    fun startStopwatch()
    fun pauseStopwatch()
    fun stopStopwatch()
    fun lapStopwatch()
}
