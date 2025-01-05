package com.whakaara.feature.alarm.utils

import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.model.preferences.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class DateUtils {
    companion object {
        fun getAlarmTimeFormatted(
            date: Calendar,
            timeFormat: TimeFormat
        ): String {
            val format = if (timeFormat == TimeFormat.TWENTY_FOUR_HOURS) DateUtilsConstants.DATE_FORMAT_24_HOUR else DateUtilsConstants.DATE_FORMAT_12_HOUR
            return SimpleDateFormat(format, Locale.getDefault()).format(date.time).uppercase()
        }

        fun getTimeAsDate(alarmDate: Calendar): Calendar {
            val currentTime = Calendar.getInstance()

            if (checkIfSameDay(alarmDate, currentTime)) {
                val duration = abs(currentTime.timeInMillis - alarmDate.timeInMillis)
                val days = TimeUnit.MILLISECONDS.toDays(duration).toInt()
                alarmDate.add(Calendar.DATE, days + 1)
            }

            return alarmDate
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

        fun getDifferenceFromCurrentTimeInMillis(time: Calendar): Long {
            val timeNow = Calendar.getInstance()

            if (checkIfSameDay(time, timeNow)) {
                time.apply {
                    add(Calendar.DATE, 1)
                }
            }

            return time.timeInMillis - timeNow.timeInMillis
        }
    }
}
