package com.app.whakaara.state

import com.app.whakaara.data.alarm.Alarm

data class AlarmState(
    val alarms: List<Alarm> = emptyList()
)
