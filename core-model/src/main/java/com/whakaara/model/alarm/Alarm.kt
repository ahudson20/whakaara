package com.whakaara.model.alarm

import java.util.Calendar
import java.util.UUID

data class Alarm(
    var alarmId: UUID = UUID.randomUUID(),
    var date: Calendar,
    var title: String = "Alarm",
    var subTitle: String,
    var vibration: Boolean = true,
    var isEnabled: Boolean = true,
    var isSnoozeEnabled: Boolean = true,
    var deleteAfterGoesOff: Boolean = false,
    var repeatDaily: Boolean = false,
    var daysOfWeek: MutableList<Int> = mutableListOf()
)
