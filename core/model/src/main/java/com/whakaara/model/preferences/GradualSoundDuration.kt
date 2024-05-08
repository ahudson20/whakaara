package com.whakaara.model.preferences

import androidx.annotation.StringRes
import com.whakaara.core.model.R

enum class GradualSoundDuration(val seconds: Int) {
    GRADUAL_INCREASE_DURATION_NEVER(0),
    GRADUAL_INCREASE_DURATION_10_SECONDS(10),
    GRADUAL_INCREASE_DURATION_20_SECONDS(20),
    GRADUAL_INCREASE_DURATION_30_SECONDS(30),
    GRADUAL_INCREASE_DURATION_60_SECONDS(60);

    fun inMillis(): Long = (seconds * 1000).toLong()

    override fun toString(): String {
        return seconds.toString()
    }

    @StringRes
    fun getStringResource(ordinal: Int): Int {
        return when (ordinal) {
            0 -> R.string.settings_screen_gradual_volume_increase_never
            1 -> R.string.settings_screen_gradual_volume_ten_seconds
            2 -> R.string.settings_screen_gradual_volume_twenty_seconds
            3 -> R.string.settings_screen_gradual_volume_thirty_seconds
            4 -> R.string.settings_screen_gradual_volume_sixty_seconds
            else -> R.string.settings_screen_gradual_volume_increase_never
        }
    }

    companion object {
        fun fromOrdinalInt(value: Int) = entries.first { it.ordinal == value }
    }
}
