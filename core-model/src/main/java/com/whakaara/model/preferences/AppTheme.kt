package com.whakaara.model.preferences

enum class AppTheme(val label: String) {
    MODE_DAY("Light mode"),
    MODE_NIGHT("Dark mode"),
    MODE_AUTO("System preference"),
    ;

    companion object {
        fun fromOrdinalInt(value: Int) = entries[value]
    }
}
