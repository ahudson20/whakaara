package com.whakaara.model.events

interface StopwatchEventCallbacks {
    fun startStopwatch()

    fun pauseStopwatch()

    fun stopStopwatch()

    fun lapStopwatch()
}
