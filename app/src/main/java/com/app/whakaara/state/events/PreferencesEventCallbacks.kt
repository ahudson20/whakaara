package com.app.whakaara.state.events

import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.TimeFormat

interface PreferencesEventCallbacks {
    fun updatePreferences(preferences: Preferences)

    fun updateAllAlarmSubtitles(format: TimeFormat)

    fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean)
}
