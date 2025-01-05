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

    const val ONBOARDING_ROUTE = "onboarding"

    val DAYS_OF_WEEK = listOf("M", "T", "W", "T", "F", "S", "S")

    const val MAX_NUMBER_OF_LAPS = 100

    const val RESET_TIMER_DATASTORE_TAG = "resetTimerStateDataStoreTAG"
    const val GO_ASYNC_TAG = "goAsyncTAG"

    const val ACTION_UPDATE_WIDGET = "com.whakaara.core.widget.ACTION_UPDATE_WIDGET"
}
