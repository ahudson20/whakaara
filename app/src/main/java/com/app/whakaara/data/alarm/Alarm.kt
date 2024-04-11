package com.app.whakaara.data.alarm

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.UUID

@Entity(tableName = "alarm_table")
data class Alarm(
    @PrimaryKey
    var alarmId: UUID = UUID.randomUUID(),
    var date: Calendar,
    var title: String = "Alarm",
    var subTitle: String,
    var vibration: Boolean = true,
    var isEnabled: Boolean = true,
    var isSnoozeEnabled: Boolean = true,
    var deleteAfterGoesOff: Boolean = false,
    var repeatDaily: Boolean = false
)
