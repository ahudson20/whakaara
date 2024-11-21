package com.app.whakaara.utility

import com.whakaara.core.constants.DateUtilsConstants.DATE_FORMAT_12_HOUR
import com.whakaara.core.constants.DateUtilsConstants.DATE_FORMAT_24_HOUR
import com.whakaara.core.constants.DateUtilsConstants.STOPWATCH_FORMAT
import com.whakaara.core.constants.DateUtilsConstants.STOPWATCH_FORMAT_NO_HOURS
import com.whakaara.core.constants.DateUtilsConstants.TIMER_FORMAT
import com.whakaara.model.preferences.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

class DateUtils {
    companion object {
        fun getTimeAsDate(alarmDate: Calendar): Calendar {
            val currentTime = Calendar.getInstance()

            if (checkIfSameDay(alarmDate, currentTime)) {
                val duration = abs(currentTime.timeInMillis - alarmDate.timeInMillis)
                val days = TimeUnit.MILLISECONDS.toDays(duration).toInt()
                alarmDate.add(Calendar.DATE, days + 1)
            }

            return alarmDate
        }

        fun getAlarmTimeFormatted(
            date: Calendar,
            timeFormat: TimeFormat
        ): String {
            val format = if (timeFormat == TimeFormat.TWENTY_FOUR_HOURS) DATE_FORMAT_24_HOUR else DATE_FORMAT_12_HOUR
            return SimpleDateFormat(format, Locale.getDefault()).format(date.time).uppercase()
        }

        fun getDifferenceFromCurrentTimeInMillis(time: Calendar): Long {
            val timeNow = Calendar.getInstance()

            if (checkIfSameDay(time, timeNow)) {
                time.apply {
                    add(Calendar.DATE, 1)
                }
            }

            return time.timeInMillis - timeNow.timeInMillis
        }

        private fun checkIfSameDay(
            alarmTime: Calendar,
            currentTime: Calendar
        ): Boolean {
            if (alarmTime.before(currentTime)) {
                return true
            }

            return alarmTime.get(Calendar.DATE) == currentTime.get(Calendar.DATE) &&
                alarmTime.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                alarmTime.get(Calendar.MINUTE) == currentTime.get(Calendar.MINUTE)
        }

//        fun hoursToMilliseconds(hours: Int): Long {
//            val millisecondsInHour: Long = 3600000 // 1 hour = 3600 seconds = 3600 * 1000 milliseconds
//            return hours * millisecondsInHour
//        }
//
//        fun minutesToMilliseconds(minutes: Int): Long {
//            val millisecondsInMinute: Long = 60000 // 1 minute = 60 seconds = 60 * 1000 milliseconds
//            return minutes * millisecondsInMinute
//        }
//
//        fun secondsToMilliseconds(seconds: Int): Long {
//            val millisecondsInSecond: Long = 1000 // 1 second = 1000 milliseconds
//            return seconds * millisecondsInSecond
//        }

        fun formatTimeForTimer(millis: Long): String {
            return millis.milliseconds.toComponents { hours, minutes, seconds, _ ->
                TIMER_FORMAT.format(hours, minutes, seconds)
            }
        }

        fun formatTimeForStopwatch(millis: Long): String {
            return millis.milliseconds.toComponents { hours, minutes, seconds, nanoseconds ->
                STOPWATCH_FORMAT.format(
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
                    STOPWATCH_FORMAT_NO_HOURS.format(
                        minutes,
                        seconds,
                        TimeUnit.MILLISECONDS.convert(nanoseconds.toLong(), TimeUnit.NANOSECONDS)
                    )
                } else {
                    STOPWATCH_FORMAT.format(
                        hours,
                        minutes,
                        seconds,
                        TimeUnit.MILLISECONDS.convert(nanoseconds.toLong(), TimeUnit.NANOSECONDS)
                    )
                }
            }
        }

//        fun generateMillisecondsFromTimerInputValues(
//            hours: String,
//            minutes: String,
//            seconds: String
//        ): Long {
//            var millis = ZERO_MILLIS
//            millis += hoursToMilliseconds(hours = hours.toIntOrNull() ?: 0)
//            millis += minutesToMilliseconds(minutes = minutes.toIntOrNull() ?: 0)
//            millis += secondsToMilliseconds(seconds = seconds.toIntOrNull() ?: 0)
//            return millis
//        }
    }
}
