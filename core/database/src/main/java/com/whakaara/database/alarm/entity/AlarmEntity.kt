package com.whakaara.database.alarm.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.whakaara.model.alarm.Alarm
import java.util.Calendar
import java.util.UUID

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey
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

fun AlarmEntity.asExternalModel() =
    Alarm(
        alarmId = alarmId,
        date = date,
        title = title,
        subTitle = subTitle,
        vibration = vibration,
        isEnabled = isEnabled,
        isSnoozeEnabled = isSnoozeEnabled,
        deleteAfterGoesOff = deleteAfterGoesOff,
        repeatDaily = repeatDaily,
        daysOfWeek = daysOfWeek
    )

fun Alarm.asInternalModel() =
    AlarmEntity(
        alarmId = alarmId,
        date = date,
        title = title,
        subTitle = subTitle,
        vibration = vibration,
        isEnabled = isEnabled,
        isSnoozeEnabled = isSnoozeEnabled,
        deleteAfterGoesOff = deleteAfterGoesOff,
        repeatDaily = repeatDaily,
        daysOfWeek = daysOfWeek
    )
