package com.app.whakaara.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "alarm_table")
data class Alarm(
    @PrimaryKey
    var alarmId: UUID = UUID.randomUUID(),
    var hour: Int,
    var minute: Int,
    var title: String?,
    var vibration: Boolean
)
