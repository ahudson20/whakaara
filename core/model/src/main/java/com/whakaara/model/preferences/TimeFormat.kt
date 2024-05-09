package com.whakaara.model.preferences

enum class TimeFormat {
    TWENTY_FOUR_HOURS,
    TWELVE_HOURS;

    companion object {
        fun TimeFormat.toBoolean(): Boolean = when (this) {
            TWELVE_HOURS -> false
            TWENTY_FOUR_HOURS -> true
        }

        fun Boolean.toTimeFormat(): TimeFormat = when (this) {
            false -> TWELVE_HOURS
            true -> TWENTY_FOUR_HOURS
        }
    }
}
