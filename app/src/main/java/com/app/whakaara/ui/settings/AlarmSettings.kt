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
import com.app.whakaara.data.preferences.SettingsTime
import com.app.whakaara.data.preferences.VibrationPattern
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

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

    SettingsListDropdown(
        modifier = Modifier.height(space80),
        enabled = preferencesState.preferences.isVibrateEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.vibrationPattern.value),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_pattern_title)) },
        items = VibrationPattern.values().map { it.label },
        onItemSelected = { int, _ ->
            updatePreferences(
                preferencesState.preferences.copy(
                    vibrationPattern = VibrationPattern.fromOrdinalInt(value = int)
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
        modifier = Modifier.height(space80),
        enabled = preferencesState.preferences.isSnoozeEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.snoozeTime.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_snooze_duration_title)) },
        items = SettingsTime.values().map { it.label },
        onItemSelected = { int, _ ->
            updatePreferences(
                preferencesState.preferences.copy(
                    snoozeTime = SettingsTime.fromOrdinalInt(value = int)
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
        modifier = Modifier.height(space80),
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.autoSilenceTime.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_subtitle)) },
        items = SettingsTime.values().map { it.label },
        onItemSelected = { int, _ ->
            updatePreferences(
                preferencesState.preferences.copy(
                    autoSilenceTime = SettingsTime.fromOrdinalInt(value = int)
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
