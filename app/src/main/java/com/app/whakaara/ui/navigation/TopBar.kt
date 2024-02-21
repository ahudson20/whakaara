package com.app.whakaara.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.bottomsheet.settings.BottomSheetSettingsWrapper
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    route: String,
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()

    TopAppBar(
        title = {
            Text(
                text = route
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
        },
        actions = {
            when (route) {
                BottomNavItem.Alarm.route -> {
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

    BottomSheetSettingsWrapper(
        state = sheetState,
        preferencesState = preferencesState,
        updatePreferences = updatePreferences,
        updateAllAlarmSubtitles = updateAllAlarmSubtitles
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TopBarPreview() {
    WhakaaraTheme {
        TopBar(
            route = "alarm",
            preferencesState = PreferencesState(),
            updatePreferences = {},
            updateAllAlarmSubtitles = {}
        )
    }
}
