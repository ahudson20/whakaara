package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.ui.screens.SettingsScreen
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.RoutePreviewProvider
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.TimeFormat
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    route: String,
    preferencesState: PreferencesState,
    preferencesEventCallbacks: PreferencesEventCallbacks
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()

    TopAppBar(
        title = {
            Text(
                text = route.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
        },
        actions = {
            when (route) {
                BottomNavItem.Alarm.route, BottomNavItem.Timer.route -> {
                    IconButton(
                        onClick = {
                            scope.launch { sheetState.expand() }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(id = R.string.settings_icon_content_description)
                        )
                    }
                }
            }
        }
    )

    BottomSheet(
        backgroundColor = MaterialTheme.colorScheme.surface,
        state = sheetState,
        skipPeeked = true
    ) {
        SettingsScreen(
            route = route,
            preferencesState = preferencesState,
            updatePreferences = preferencesEventCallbacks::updatePreferences,
            updateAllAlarmSubtitles = preferencesEventCallbacks::updateAllAlarmSubtitles,
            updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification = preferencesEventCallbacks::updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TopBarPreview(
    @PreviewParameter(RoutePreviewProvider::class) route: String
) {
    WhakaaraTheme {
        TopBar(
            route = route,
            preferencesState = PreferencesState(),
            preferencesEventCallbacks = object : PreferencesEventCallbacks {
                override fun updatePreferences(preferences: Preferences) {}

                override fun updateAllAlarmSubtitles(format: TimeFormat) {}

                override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean) {}
            }
        )
    }
}
