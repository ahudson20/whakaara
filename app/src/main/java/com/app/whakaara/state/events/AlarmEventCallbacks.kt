package com.app.whakaara.state.events

import com.app.whakaara.data.alarm.Alarm

interface AlarmEventCallbacks {
    fun create(alarm: Alarm)
    fun delete(alarm: Alarm)
    fun disable(alarm: Alarm)
    fun enable(alarm: Alarm)
    fun reset(alarm: Alarm)
}
