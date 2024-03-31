package com.app.whakaara.utils.constants

object DateUtilsConstants {
    const val DATE_FORMAT_24_HOUR = "HH:mm"
    const val DATE_FORMAT_24_HOUR_WITH_SECONDS = "HH:mm:ss aa"

    const val DATE_FORMAT_12_HOUR = "h:mm aa"
    const val DATE_FORMAT_12_HOUR_WITH_SECONDS = "hh:mm:ss aa"

    const val STOPWATCH_FORMAT = "%02d:%02d:%02d:%03d"
    const val STOPWATCH_STARTING_TIME = "00:00:00:000"

    const val TIMER_FORMAT = "%02d:%02d:%02d"
    const val TIMER_STARTING_FORMAT = "00:00:00"

    const val TIMER_HOURS_INPUT_REGEX = "^(0?[0-9]|1[0-9]|2[0-3])?$"
    const val TIMER_MINUTES_AND_SECONDS_INPUT_REGEX = "^(0?[0-9]|[1-5][0-9])?$"
    const val TIMER_INPUT_INITIAL_VALUE = "00"
}
