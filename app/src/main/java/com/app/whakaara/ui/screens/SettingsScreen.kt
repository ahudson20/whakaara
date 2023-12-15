package com.app.whakaara.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.settings.AlarmSettings
import com.app.whakaara.ui.settings.GeneralSettings
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: Boolean) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            GeneralSettings()
            HorizontalDivider()
            AlarmSettings(
                preferencesState = preferencesState,
                updatePreferences = updatePreferences,
                updateAllAlarmSubtitles = updateAllAlarmSubtitles
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun SettingsScreenPreview() {
    WhakaaraTheme {
        SettingsScreen(
            preferencesState = PreferencesState(),
            updatePreferences = {},
            updateAllAlarmSubtitles = {}
        )
    }
}
