package com.app.whakaara.utils

class DateUtils {

    companion object {

        fun convertIntegersToHHMM(hour: Int, minute: Int): String {
            return String.format("%02d:%02d", hour, minute);
        }

    }

}