package com.app.whakaara.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var alarmId: Int,
    var hour: Int,
    var minute: Int,
    var title: String?,
    var vibration: Boolean
)
