package com.app.whakaara.data.preferences

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences_table")
data class Preferences(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var isVibrateEnabled: Boolean = true,
    var isSnoozeEnabled: Boolean = true,
    var deleteAfterGoesOff: Boolean = false
)
