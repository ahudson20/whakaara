package com.app.whakaara.state.events

import com.whakaara.model.alarm.Alarm
import java.util.Calendar

interface AlarmEventCallbacks {
    fun create(alarm: Alarm)

    fun delete(alarm: Alarm)

    fun disable(alarm: Alarm)

    fun enable(alarm: Alarm)

    fun reset(alarm: Alarm)

    fun getInitialTimeToAlarm(
        isEnabled: Boolean,
        time: Calendar
    ): String

    fun getTimeUntilAlarmFormatted(date: Calendar): String
}
