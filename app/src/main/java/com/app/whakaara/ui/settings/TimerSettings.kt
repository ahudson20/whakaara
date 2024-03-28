package com.app.whakaara.ui.settings

import android.app.Service
import android.os.VibrationAttributes
import android.os.VibratorManager
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.VibrationPattern
import com.app.whakaara.data.preferences.VibrationPattern.Companion.SINGLE
import com.app.whakaara.data.preferences.VibrationPattern.Companion.createWaveForm
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun TimerSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    val context = LocalContext.current
    val vibrator = (context.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_timer_settings_title)
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        state = rememberBooleanSettingState(preferencesState.preferences.isVibrationTimerEnabled),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_timer_vibrate_subtitle)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    isVibrationTimerEnabled = it
                )
            )
        }
    )

    SettingsListDropdown(
        modifier = Modifier.height(space80),
        enabled = preferencesState.preferences.isVibrationTimerEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.timerVibrationPattern.value),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_pattern_title)) },
        items = VibrationPattern.values().map { it.label },
        onItemSelected = { int, _ ->
            val selection = VibrationPattern.fromOrdinalInt(value = int)
            val vibrationEffect = createWaveForm(selection = selection, repeat = SINGLE)
            val attributes = VibrationAttributes.Builder().apply {
                setUsage(VibrationAttributes.USAGE_NOTIFICATION)
            }.build()
            vibrator.vibrate(vibrationEffect, attributes)
            if (selection != preferencesState.preferences.timerVibrationPattern) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        timerVibrationPattern = selection
                    )
                )
            }
        }
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerSettingsPreview() {
    WhakaaraTheme {
        TimerSettings(
            preferencesState = PreferencesState(),
            updatePreferences = {}
        )
    }
}
