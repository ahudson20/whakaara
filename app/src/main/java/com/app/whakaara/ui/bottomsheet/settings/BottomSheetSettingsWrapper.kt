package com.app.whakaara.ui.bottomsheet.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.screens.SettingsScreen
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState

@Composable
fun BottomSheetSettingsWrapper(
    state: BottomSheetState,
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: Boolean) -> Unit,
    filterAlarmList: (shouldFilter: Boolean) -> Unit
) {
    BottomSheet(
        backgroundColor = MaterialTheme.colorScheme.surface,
        state = state,
        skipPeeked = true
    ) {
        SettingsScreen(
            preferencesState = preferencesState,
            updatePreferences = updatePreferences,
            updateAllAlarmSubtitles = updateAllAlarmSubtitles,
            filterAlarmList = filterAlarmList
        )
    }
}
