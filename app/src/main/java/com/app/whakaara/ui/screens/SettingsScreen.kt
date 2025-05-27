package com.app.whakaara.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.ui.navigation.BottomNavItem
import com.app.whakaara.ui.settings.AlarmSettings
import com.app.whakaara.ui.settings.AppInfoDisplay
import com.app.whakaara.ui.settings.GeneralSettings
import com.app.whakaara.ui.settings.TimerSettings
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.RoutePreviewProvider
import com.whakaara.core.designsystem.theme.Spacings.space20
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.preferences.TimeFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    route: String,
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: TimeFormat) -> Unit,
    updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification: (shouldEnableUpcomingAlarmNotification: Boolean) -> Unit
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
            Text(
                text = stringResource(id = R.string.settings_screen_title),
                textAlign = Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = spaceMedium, bottom = space20)
                    .align(alignment = CenterHorizontally),
                style = MaterialTheme.typography.titleLarge
            )
            GeneralSettings(
                preferencesState = preferencesState,
                updatePreferences = updatePreferences,
                updateAllAlarmSubtitles = updateAllAlarmSubtitles
            )
            HorizontalDivider(
                modifier = Modifier.padding(top = spaceMedium)
            )
            when (route) {
                BottomNavItem.Alarm.route -> {
                    AlarmSettings(
                        preferencesState = preferencesState,
                        updatePreferences = updatePreferences,
                        updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification = updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification
                    )
                }
                BottomNavItem.Timer.route -> {
                    TimerSettings(
                        preferencesState = preferencesState,
                        updatePreferences = updatePreferences
                    )
                }
            }

            AppInfoDisplay()
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun SettingsScreenPreview(
    @PreviewParameter(RoutePreviewProvider::class) route: String
) {
    WhakaaraTheme {
        SettingsScreen(
            route = route,
            preferencesState = PreferencesState(),
            updatePreferences = {},
            updateAllAlarmSubtitles = {},
            updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification = {}
        )
    }
}
