package com.whakaara.core.constants

object GeneralConstants {
    const val TIMER_START_DELAY_MILLIS = 10L
    const val TIMER_INTERVAL = 50L
    const val ZERO_MILLIS = 0L
    const val STARTING_CIRCULAR_PROGRESS = 1.00F
    const val RINGTONE_NONE_SELECTED = "None selected"

    const val DEEPLINK_ALARM = "whakaara://alarm"
    const val DEEPLINK_TIMER = "whakaara://timer"
    const val DEEPLINK_STOPWATCH = "whakaara://stopwatch"

    const val WAKE_LOCK_TAG = "whakaara::WakelockTag"

    val DAYS_OF_WEEK = listOf("M", "T", "W", "T", "F", "S", "S")

    const val MAX_NUMBER_OF_LAPS = 100
}
