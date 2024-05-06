package com.whakaara.model.preferences

enum class SettingsTime(val value: Int, val label: String) {
    ONE(1, "1 minute"),
    FIVE(5, "5 minutes"),
    TEN(10, "10 minutes"),
    FIFTEEN(15, "15 minutes")
    ;

    companion object {
        fun fromOrdinalInt(value: Int) = entries.first { it.ordinal == value }
    }
}
