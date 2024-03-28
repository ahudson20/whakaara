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
    var autoSilenceTime: SettingsTime = SettingsTime.TEN,
    var snoozeTime: SettingsTime = SettingsTime.TEN,
    var alarmSoundPath: String = "",
    var vibrationPattern: VibrationPattern = VibrationPattern.CLICK,
    var appTheme: AppTheme = AppTheme.MODE_AUTO,
    var shouldShowOnboarding: Boolean = false
)

enum class VibrationPattern(val value: Int, val label: String) {
    CLICK(0, "Click"),
    DOUBLE(1, "Double click"),
    HEAVY(2, "Heavy click"),
    TICK(3, "Tick");

    companion object {
        fun fromOrdinalInt(value: Int) = VibrationPattern.values().first { it.ordinal == value }

        val clickPattern = longArrayOf(0, 200)
        val clickPatternAmplitude = intArrayOf(0, 200)

        val doubleClickPattern = longArrayOf(0, 200)
        val doubleClickPatternAmplitude = intArrayOf(0, 200)

        val heavyClickPattern = longArrayOf(0, 200)
        val heavyClickPatternAmplitude = intArrayOf(0, 200)

        val tickPattern = longArrayOf(0, 200)
        val tickPatternAmplitude = intArrayOf(0, 200)
    }
}

enum class SettingsTime(val value: Int, val label: String) {
    ONE(0, "1 minute"),
    FIVE(5, "5 minutes"),
    TEN(10, "10 minutes"),
    FIFTEEN(15, "15 minutes");

    companion object {
        fun fromOrdinalInt(value: Int) = SettingsTime.values().first { it.ordinal == value }
    }
}

enum class AppTheme(val label: String) {
    MODE_DAY("Light mode"),
    MODE_NIGHT("Dark mode"),
    MODE_AUTO("System preference");

    companion object {
        fun fromOrdinalInt(value: Int) = values()[value]
    }
}
