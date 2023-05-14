package com.app.whakaara.utils

import com.app.whakaara.data.Alarm
import com.app.whakaara.utils.constants.DateUtilsConstants.DATE_FORMAT_24_HOUR
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class DateUtils {
    companion object {

        private const val BOTTOM_SHEET_ALARM_LABEL_OFF = "Off"

        fun getTimeInMillis(alarm: Alarm): Long {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.SECOND, 0)
            }

            /** check if time has already elapsed, set for following day **/
            if (cal.timeInMillis < System.currentTimeMillis()) {
                cal.add(Calendar.DATE, 1)
            }

            return cal.timeInMillis
        }

        fun getDifferenceFromCurrentTimeInMillis(
            hours: Int,
            minutes: Int
        ): Long {
            val timeNowNoSeconds = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
            }

            val futureTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
            }

            if (futureTime.timeInMillis < timeNowNoSeconds.timeInMillis) {
                futureTime.add(Calendar.DATE, 1)
            }

            return futureTime.timeInMillis - timeNowNoSeconds.timeInMillis
        }

        fun convertSecondsToHMm(seconds: Long): String {
            val minutes = seconds / 60 % 60
            val hours = seconds / (60 * 60) % 24
            return if (hours.toInt() == 1) {
                String.format("Alarm in %d hour %d minutes", hours, minutes)
            } else if (hours > 0) {
                String.format("Alarm in %d hours %d minutes", hours, minutes)
            } else {
                String.format("Alarm in %d minutes", minutes)
            }
        }

        fun alarmTimeTo24HourFormat(hour: Int, minute: Int): String {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }
            return SimpleDateFormat(DATE_FORMAT_24_HOUR, Locale.getDefault()).format(cal.time)
        }

        fun getInitialTimeToAlarm(isEnabled: Boolean, hours: Int, minutes: Int): String {
            return if (!isEnabled) {
                BOTTOM_SHEET_ALARM_LABEL_OFF
            } else {
                return convertSecondsToHMm(
                    seconds = TimeUnit.MILLISECONDS.toSeconds(
                        getDifferenceFromCurrentTimeInMillis(
                            hours = hours,
                            minutes = minutes
                        )
                    )
                )
            }
        }
    }
}