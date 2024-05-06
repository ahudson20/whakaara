package com.whakaara.model.preferences

import android.os.VibrationEffect

enum class VibrationPattern(val value: Int, val label: String) {
    CLICK(0, "Click"),
    DOUBLE(1, "Double click"),
    HEAVY(2, "Heavy click"),
    TICK(3, "Tick")
    ;

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
