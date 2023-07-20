package com.app.whakaara.utils

import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.utils.constants.DateUtilsConstants.BOTTOM_SHEET_ALARM_LABEL_OFF
import com.app.whakaara.utils.constants.DateUtilsConstants.DATE_FORMAT_24_HOUR
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class DateUtils {
    companion object {
        fun getTimeInMillis(alarm: Alarm): Long {
            val alarmTime = alarm.date
            val currentTime = Calendar.getInstance()

            if (checkIfSameDay(alarmTime, currentTime)) {
                alarmTime.add(Calendar.DATE, 1)
            }

            return alarmTime.timeInMillis
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

        fun getDifferenceFromCurrentTimeInMillis(
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

            if (
                alarmTime.get(Calendar.DATE) == currentTime.get(Calendar.DATE) &&
                alarmTime.get(Calendar.HOUR_OF_DAY) == currentTime.get(Calendar.HOUR_OF_DAY) &&
                alarmTime.get(Calendar.MINUTE) == currentTime.get(Calendar.MINUTE)
            ) {
                return true
            }
            return false
        }

        fun alarmTimeTo24HourFormat(date: Calendar): String {
            return SimpleDateFormat(DATE_FORMAT_24_HOUR, Locale.getDefault()).format(date.time).uppercase()
        }
    }
}
