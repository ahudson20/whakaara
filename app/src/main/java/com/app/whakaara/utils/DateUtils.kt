package com.app.whakaara.utils

import com.app.whakaara.utils.constants.DateUtilsConstants
import com.app.whakaara.utils.constants.DateUtilsConstants.BOTTOM_SHEET_ALARM_LABEL_OFF
import com.app.whakaara.utils.constants.DateUtilsConstants.DATE_FORMAT_12_HOUR
import com.app.whakaara.utils.constants.DateUtilsConstants.DATE_FORMAT_24_HOUR
import com.app.whakaara.utils.constants.GeneralConstants
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class DateUtils {
    companion object {
        fun getTimeInMillis(alarmDate: Calendar): Long {
            val currentTime = Calendar.getInstance()

            if (checkIfSameDay(alarmDate, currentTime)) {
                alarmDate.add(Calendar.DATE, 1)
            }

            return alarmDate.timeInMillis
        }

        fun getInitialTimeToAlarm(isEnabled: Boolean, time: Calendar): String {
            return if (!isEnabled) {
                BOTTOM_SHEET_ALARM_LABEL_OFF
            } else {
                return convertSecondsToHMm(
                    seconds = TimeUnit.MILLISECONDS.toSeconds(
                        getDifferenceFromCurrentTimeInMillis(
                            time = time
                        )
                    )
                )
            }
        }

        fun getAlarmTimeFormatted(date: Calendar, is24HourFormatEnabled: Boolean): String {
            val format = if (is24HourFormatEnabled) DATE_FORMAT_24_HOUR else DATE_FORMAT_12_HOUR
            return SimpleDateFormat(format, Locale.getDefault()).format(date.time).uppercase()
        }

        fun getTimeUntilAlarmFormatted(date: Calendar): String {
            return convertSecondsToHMm(
                seconds = TimeUnit.MILLISECONDS.toSeconds(
                    getDifferenceFromCurrentTimeInMillis(
                        time = date
                    )
                )
            )
        }

        fun convertSecondsToHMm(
            seconds: Long
        ): String {
            val minutes = seconds / 60 % 60
            val hours = seconds / (60 * 60) % 24
            val formattedString = StringBuilder()
            val hoursString = when {
                hours.toInt() == 1 -> String.format("%d hour ", hours)
                hours.toInt() == 0 -> ""
                else -> String.format("%d hours ", hours)
            }
            val minutesString = when {
                minutes.toInt() == 1 -> String.format("%d minute ", minutes)
                minutes.toInt() == 0 && hours.toInt() == 0 -> "less than 1 minute"
                minutes.toInt() == 0 -> ""
                else -> String.format("%d minutes ", minutes)
            }

            formattedString.append("Alarm in ")
            if (hoursString.isNotBlank()) formattedString.append(hoursString)
            if (minutesString.isNotBlank()) formattedString.append(minutesString)
            return formattedString.toString().trim()
        }

        private fun getDifferenceFromCurrentTimeInMillis(
            time: Calendar
        ): Long {
            val timeNowNoSeconds = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
            }
            var newTime: Calendar = time

            if (checkIfSameDay(time, timeNowNoSeconds)) {
                newTime = Calendar.getInstance().apply {
                    add(Calendar.DATE, 1)
                    set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, time.get(Calendar.MINUTE))
                }
            }

            return newTime.timeInMillis - timeNowNoSeconds.timeInMillis
        }

        private fun checkIfSameDay(alarmTime: Calendar, currentTime: Calendar): Boolean {
            if (alarmTime.before(currentTime)) {
                return true
            }

            return alarmTime.get(Calendar.DATE) == currentTime.get(Calendar.DATE) &&
                alarmTime.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                alarmTime.get(Calendar.MINUTE) == currentTime.get(Calendar.MINUTE)
        }

        fun hoursToMilliseconds(hours: Int): Long {
            val millisecondsInHour: Long = 3600000 // 1 hour = 3600 seconds = 3600 * 1000 milliseconds
            return hours * millisecondsInHour
        }

        fun minutesToMilliseconds(minutes: Int): Long {
            val millisecondsInMinute: Long = 60000 // 1 minute = 60 seconds = 60 * 1000 milliseconds
            return minutes * millisecondsInMinute
        }

        fun secondsToMilliseconds(seconds: Int): Long {
            val millisecondsInSecond: Long = 1000 // 1 second = 1000 milliseconds
            return seconds * millisecondsInSecond
        }

        fun formatTimeTimerAndStopwatch(timeMillis: Long): String {
            val localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeMillis),
                ZoneId.systemDefault()
            )
            val formatter = DateTimeFormatter.ofPattern(DateUtilsConstants.TIMER_MIN_SEC_MILLIS, Locale.getDefault())
            return localDateTime.format(formatter)
        }

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
    }
}
