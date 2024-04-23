package com.app.whakaara.state.events

import com.app.whakaara.data.preferences.Preferences

interface PreferencesEventCallbacks {
    fun updatePreferences(preferences: Preferences)

    fun updateAllAlarmSubtitles(format: Boolean)

    fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean)
}
