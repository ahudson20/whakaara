package com.whakaara.model.preferences

import android.os.VibrationEffect
import androidx.annotation.StringRes
import com.whakaara.core.model.R

enum class VibrationPattern(val value: Int) {
    CLICK(0),
    DOUBLE(1),
    HEAVY(2),
    TICK(3);

    @StringRes
    fun getStringResource(ordinal: Int): Int {
        return when (ordinal) {
            0 -> R.string.settings_screen_vibration_pattern_click
            1 -> R.string.settings_screen_vibration_pattern_heavy_click
            2 -> R.string.settings_screen_vibration_pattern_double_click
            3 -> R.string.settings_screen_vibration_pattern_tick
            else -> R.string.settings_screen_vibration_pattern_click
        }
    }

    companion object {
        fun fromOrdinalInt(value: Int) = entries.first { it.ordinal == value }

        fun createWaveForm(
            selection: VibrationPattern,
            repeat: Int
        ): VibrationEffect =
            when (selection) {
                CLICK -> VibrationEffect.createWaveform(clickPattern, clickPatternAmplitude, repeat)
                DOUBLE -> VibrationEffect.createWaveform(doubleClickPattern, doubleClickPatternAmplitude, repeat)
                HEAVY -> VibrationEffect.createWaveform(heavyClickPattern, heavyClickPatternAmplitude, repeat)
                TICK -> VibrationEffect.createWaveform(tickPattern, tickPatternAmplitude, repeat)
            }

        const val REPEAT = 0
        const val SINGLE = -1

        private val clickPattern = longArrayOf(0, 200)
        private val clickPatternAmplitude = intArrayOf(0, 255)

        private val doubleClickPattern = longArrayOf(0, 200, 160, 200)
        private val doubleClickPatternAmplitude = intArrayOf(0, 255, 0, 255)

        private val heavyClickPattern = longArrayOf(0, 230)
        private val heavyClickPatternAmplitude = intArrayOf(0, 255)

        private val tickPattern = longArrayOf(0, 100)
        private val tickPatternAmplitude = intArrayOf(0, 100)
    }
}
