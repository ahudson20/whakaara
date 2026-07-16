package com.whakaara.database.preferences.converters

import androidx.room.TypeConverter
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.preferences.VibrationPattern
import kotlin.enums.enumEntries

class PreferencesConverter {
    @TypeConverter
    fun toVibrationPattern(value: Int) = enumEntries<VibrationPattern>()[value]

    @TypeConverter
    fun fromVibrationPattern(value: VibrationPattern) = value.ordinal

    @TypeConverter
    fun toTime(value: Int) = enumEntries<SettingsTime>()[value]

    @TypeConverter
    fun fromTime(value: SettingsTime) = value.ordinal

    @TypeConverter
    fun toAppTheme(value: Int) = enumEntries<AppTheme>()[value]

    @TypeConverter
    fun fromAppTheme(value: AppTheme) = value.ordinal

    @TypeConverter
    fun toTimeFormat(value: Int) = enumEntries<TimeFormat>()[value]

    @TypeConverter
    fun fromTimeFormat(value: TimeFormat) = value.ordinal

    @TypeConverter
    fun toGradualSoundDuration(value: Int) = enumEntries<GradualSoundDuration>()[value]

    @TypeConverter
    fun fromGradualSoundDuration(value: GradualSoundDuration) = value.ordinal
}
