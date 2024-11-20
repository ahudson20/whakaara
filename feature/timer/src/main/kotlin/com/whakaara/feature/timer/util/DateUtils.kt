package com.whakaara.feature.timer.util

import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.constants.GeneralConstants
import kotlin.time.Duration.Companion.milliseconds

class DateUtils {
    companion object {
        fun generateMillisecondsFromTimerInputValues(
            hours: String,
            minutes: String,
            seconds: String
        ): Long {
            var millis = GeneralConstants.ZERO_MILLIS
            millis += hoursToMilliseconds(hours = hours.toIntOrNull() ?: 0)
            millis += minutesToMilliseconds(minutes = minutes.toIntOrNull() ?: 0)
            millis += secondsToMilliseconds(seconds = seconds.toIntOrNull() ?: 0)
            return millis
        }

        private fun hoursToMilliseconds(hours: Int): Long {
            val millisecondsInHour: Long = 3600000 // 1 hour = 3600 seconds = 3600 * 1000 milliseconds
            return hours * millisecondsInHour
        }

        private fun minutesToMilliseconds(minutes: Int): Long {
            val millisecondsInMinute: Long = 60000 // 1 minute = 60 seconds = 60 * 1000 milliseconds
            return minutes * millisecondsInMinute
        }

        private fun secondsToMilliseconds(seconds: Int): Long {
            val millisecondsInSecond: Long = 1000 // 1 second = 1000 milliseconds
            return seconds * millisecondsInSecond
        }

        fun formatTimeForTimer(millis: Long): String {
            return millis.milliseconds.toComponents { hours, minutes, seconds, _ ->
                DateUtilsConstants.TIMER_FORMAT.format(hours, minutes, seconds)
            }
        }
    }
}
