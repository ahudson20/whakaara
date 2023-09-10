package com.app.whakaara.state

import com.app.whakaara.data.preferences.Preferences

data class PreferencesState(
    val preferences: Preferences = Preferences()
)
