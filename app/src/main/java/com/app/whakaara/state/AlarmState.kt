package com.app.whakaara.state

import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.preferences.Preferences

data class AlarmState(
    val alarms: List<Alarm> = emptyList()
)

data class PreferencesState(
    val preferences: Preferences = Preferences()
)
