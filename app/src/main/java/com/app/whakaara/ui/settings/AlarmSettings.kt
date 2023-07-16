package com.app.whakaara.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.GeneralConstants

@Composable
fun AlarmSettings(
    modifier: Modifier = Modifier,
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    Text(
        modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_alarm_settings_title)
    )
    SettingsSwitch(
        modifier = modifier.height(80.dp),
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
        modifier = modifier.height(80.dp),
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
        state = rememberIntSettingState(defaultValue = GeneralConstants.SETTINGS_SCREEN_TIME_LIST.indexOfFirst { it.split(" ")[0].toInt() == preferencesState.preferences.snoozeTime }),
        modifier = modifier.height(80.dp),
        title = { Text(text = stringResource(id = R.string.settings_screen_snooze_duration_title)) },
        items = GeneralConstants.SETTINGS_SCREEN_TIME_LIST,
        onItemSelected = { _, text ->
            updatePreferences(
                preferencesState.preferences.copy(
                    snoozeTime = text.split(" ")[0].toInt()
                )
            )
        }
    )
    SettingsSwitch(
        modifier = modifier.height(80.dp),
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
        state = rememberIntSettingState(defaultValue = GeneralConstants.SETTINGS_SCREEN_TIME_LIST.indexOfFirst { it.split(" ")[0].toInt() == preferencesState.preferences.autoSilenceTime }),
        modifier = modifier.height(80.dp),
        title = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_subtitle)) },
        items = GeneralConstants.SETTINGS_SCREEN_TIME_LIST,
        onItemSelected = { _, text ->
            updatePreferences(
                preferencesState.preferences.copy(
                    autoSilenceTime = text.split(" ")[0].toInt()
                )
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AlarmSettingsPreview() {
    WhakaaraTheme {
        Column {
            AlarmSettings(
                preferencesState = PreferencesState(),
                updatePreferences = {}
            )
        }
    }
}
