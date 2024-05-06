package com.whakaara.database.preferences.converters

import androidx.room.TypeConverter
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.VibrationPattern

class PreferencesConverter {
    @TypeConverter
    fun toVibrationPattern(value: Int) = enumValues<VibrationPattern>()[value]

    @TypeConverter
    fun fromVibrationPattern(value: VibrationPattern) = value.ordinal

    @TypeConverter
    fun toTime(value: Int) = enumValues<SettingsTime>()[value]

    @TypeConverter
    fun fromTime(value: SettingsTime) = value.ordinal

    @TypeConverter
    fun toAppTheme(value: Int) = enumValues<AppTheme>()[value]

    @TypeConverter
    fun fromAppTheme(value: AppTheme) = value.ordinal
}
