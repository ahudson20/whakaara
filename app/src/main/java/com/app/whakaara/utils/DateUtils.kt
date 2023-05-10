package com.app.whakaara.utils

import com.app.whakaara.data.Alarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DateUtils {
    companion object {

        private const val BOTTOM_SHEET_ALARM_LABEL_OFF = "Off"
        const val TWENTY_FOUR_HOUR_AM_PM = "hh:mm aa"

        fun convertIntegersToHHMM(hour: Int, minute: Int): String {
            return String.format("%02d:%02d", hour, minute);
        }

        fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
            val formatter = SimpleDateFormat(format, locale) //"hh:mm aa"
            return formatter.format(this)
        }

        fun getCurrentDateTime(): Date {
            return Calendar.getInstance().time
        }

        fun getCurrent(): String {
            val sdf = SimpleDateFormat("hh:mm aa", Locale.getDefault())
            val dateNow = Calendar.getInstance().time
            return sdf.format(dateNow)
        }

        // TODO: make this better...
          fun generateSubTitle(hour: Int, minute: Int): String {
            val subTitle = StringBuilder()
            val hour24 = (hour % 12).toString()
            val min = if (minute < 10) "0$minute" else minute.toString()
            val postFix = if (hour < 12)  "AM" else "PM"

            subTitle.append(SimpleDateFormat("EE", Locale.ENGLISH).format(System.currentTimeMillis())).append(" ")
            subTitle.append("$hour24:$min $postFix")
            return subTitle.toString()
        }

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
            return if (hours > 0) {
                String.format("Alarm in %d hours %d minutes", hours, minutes)
            } else {
                String.format("Alarm in %d minutes", minutes)
            }
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