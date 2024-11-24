package com.whakaara.feature.stopwatch.util

import com.whakaara.core.constants.DateUtilsConstants
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class DateUtils {
    companion object {
        fun formatTimeForStopwatch(millis: Long): String {
            return millis.milliseconds.toComponents { hours, minutes, seconds, nanoseconds ->
                DateUtilsConstants.STOPWATCH_FORMAT.format(
                    hours,
                    minutes,
                    seconds,
                    TimeUnit.MILLISECONDS.convert(nanoseconds.toLong(), TimeUnit.NANOSECONDS)
                )
            }
        }

        fun formatTimeForStopwatchLap(millis: Long): String {
            return millis.milliseconds.toComponents { hours, minutes, seconds, nanoseconds ->
                if (hours == 0L) {
                    DateUtilsConstants.STOPWATCH_FORMAT_NO_HOURS.format(
                        minutes,
                        seconds,
                        TimeUnit.MILLISECONDS.convert(nanoseconds.toLong(), TimeUnit.NANOSECONDS)
                    )
                } else {
                    DateUtilsConstants.STOPWATCH_FORMAT.format(
                        hours,
                        minutes,
                        seconds,
                        TimeUnit.MILLISECONDS.convert(nanoseconds.toLong(), TimeUnit.NANOSECONDS)
                    )
                }
            }
        }
    }
}
