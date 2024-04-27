package com.app.whakaara.state

import com.app.whakaara.data.preferences.Preferences

data class PreferencesState(
    val isReady: Boolean = false,
    val preferences: Preferences = Preferences()
)
