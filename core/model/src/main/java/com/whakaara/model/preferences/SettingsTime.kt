package com.whakaara.model.preferences

import androidx.annotation.StringRes
import com.whakaara.core.model.R

enum class SettingsTime(val value: Int) {
    ONE(1),
    FIVE(5),
    TEN(10),
    FIFTEEN(15);

    @StringRes
    fun getStringResource(ordinal: Int): Int {
        return when (ordinal) {
            0 -> R.string.settings_screen_time_one_minute
            1 -> R.string.settings_screen_time_five_minutes
            2 -> R.string.settings_screen_time_ten_minutes
            3 -> R.string.settings_screen_time_fifteen_minutes
            else -> R.string.settings_screen_time_one_minute
        }
    }

    companion object {
        fun fromOrdinalInt(value: Int) = entries.first { it.ordinal == value }
    }
}
