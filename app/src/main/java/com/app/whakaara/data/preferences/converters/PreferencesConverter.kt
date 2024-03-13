package com.app.whakaara.data.preferences.converters

import androidx.room.TypeConverter
import com.app.whakaara.data.preferences.SettingsTime
import com.app.whakaara.data.preferences.VibrationPattern

class PreferencesConverter {
    @TypeConverter
    fun toVibrationPattern(value: Int) = enumValues<VibrationPattern>()[value]

    @TypeConverter
    fun fromVibrationPattern(value: VibrationPattern) = value.ordinal

    @TypeConverter
    fun toTime(value: Int) = enumValues<SettingsTime>()[value]

    @TypeConverter
    fun fromTime(value: SettingsTime) = value.ordinal
}
