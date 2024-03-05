package com.app.whakaara.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.GeneralConstants.SETTINGS_SCREEN_TIME_LIST

@Composable
fun AlarmSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: Boolean) -> Unit
) {
    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_alarm_settings_title)
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        state = rememberBooleanSettingState(preferencesState.preferences.is24HourFormat),
        title = { Text(text = stringResource(id = R.string.settings_screen_24_hour_format_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_24_hour_format_subtitle)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    is24HourFormat = it
                )
            )
            updateAllAlarmSubtitles(it)
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        state = rememberBooleanSettingState(preferencesState.preferences.isVibrateEnabled),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_vibrate_subtitle)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    isVibrateEnabled = it
                )
            )
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        title = { Text(text = stringResource(id = R.string.settings_screen_snooze_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_snooze_subtitle)) },
        state = rememberBooleanSettingState(preferencesState.preferences.isSnoozeEnabled),
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    isSnoozeEnabled = it
                )
            )
        }
    )

    SettingsListDropdown(
        state = rememberIntSettingState(defaultValue = SETTINGS_SCREEN_TIME_LIST.indexOfFirst { it.split(" ")[0].toInt() == preferencesState.preferences.snoozeTime }),
        modifier = Modifier.height(space80),
        title = { Text(text = stringResource(id = R.string.settings_screen_snooze_duration_title)) },
        items = SETTINGS_SCREEN_TIME_LIST,
        onItemSelected = { _, text ->
            updatePreferences(
                preferencesState.preferences.copy(
                    snoozeTime = text.split(" ")[0].toInt()
                )
            )
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        title = { Text(text = stringResource(id = R.string.settings_screen_delete_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_delete_subtitle)) },
        state = rememberBooleanSettingState(preferencesState.preferences.deleteAfterGoesOff),
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    deleteAfterGoesOff = it
                )
            )
        }
    )

    SettingsListDropdown(
        state = rememberIntSettingState(defaultValue = SETTINGS_SCREEN_TIME_LIST.indexOfFirst { it.split(" ")[0].toInt() == preferencesState.preferences.autoSilenceTime }),
        modifier = Modifier.height(space80),
        title = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_subtitle)) },
        items = SETTINGS_SCREEN_TIME_LIST,
        onItemSelected = { _, text ->
            updatePreferences(
                preferencesState.preferences.copy(
                    autoSilenceTime = text.split(" ")[0].toInt()
                )
            )
        }
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun AlarmSettingsPreview() {
    WhakaaraTheme {
        Column {
            AlarmSettings(
                preferencesState = PreferencesState(),
                updatePreferences = {},
                updateAllAlarmSubtitles = {}
            )
        }
    }
}
