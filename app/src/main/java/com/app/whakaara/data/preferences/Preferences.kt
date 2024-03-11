package com.app.whakaara.data.preferences

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences_table")
data class Preferences(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var is24HourFormat: Boolean = true,
    var isVibrateEnabled: Boolean = true,
    var isSnoozeEnabled: Boolean = true,
    var deleteAfterGoesOff: Boolean = false,
    var autoSilenceTime: Int = 10,
    var snoozeTime: Int = 10,
    var alarmSoundPath: String = ""
)
