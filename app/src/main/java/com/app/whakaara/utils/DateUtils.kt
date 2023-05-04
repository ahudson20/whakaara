package com.app.whakaara.utils

import com.app.whakaara.data.Alarm
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DateUtils {
    companion object {
        fun convertIntegersToHHMM(hour: Int, minute: Int): String {
            return String.format("%02d:%02d", hour, minute);
        }
        fun generateSubTitle(alarm: Alarm): String {
            val subTitle = StringBuilder()
            val hour24 = (alarm.hour % 12).toString()
            val minute = if (alarm.minute < 10) "0" + alarm.minute.toString() else alarm.minute.toString()
            val postFix = if (alarm.hour < 12)  "AM" else "PM"

            subTitle.append(SimpleDateFormat("EE", Locale.ENGLISH).format(System.currentTimeMillis())).append(" ")
            subTitle.append("$hour24:$minute $postFix")
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
    }
}