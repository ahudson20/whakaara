package com.whakaara.model.events

import com.whakaara.model.preferences.Preferences

interface PreferencesEventCallbacks {
    fun updatePreferences(preferences: Preferences)

    fun updateAllAlarmSubtitles(format: Boolean)

    fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean)
}
