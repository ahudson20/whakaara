package com.app.whakaara.ui.settings

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.VibrationAttributes
import android.os.VibratorManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utility.GeneralUtils.Companion.getNameFromUri
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.VibrationPattern
import com.whakaara.model.preferences.VibrationPattern.Companion.SINGLE
import com.whakaara.model.preferences.VibrationPattern.Companion.createWaveForm

@Composable
fun TimerSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    val context = LocalContext.current
    val vibrator = (context.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    val currentRingtoneUri: Uri = if (preferencesState.preferences.timerSoundPath.isNotEmpty()) {
        Uri.parse(preferencesState.preferences.timerSoundPath)
    } else {
        Settings.System.DEFAULT_ALARM_ALERT_URI
    }
    val ringtoneSelectionIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_TITLE,
            context.getString(R.string.ringtone_selection_activity_title)
        )
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentRingtoneUri)
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
            Settings.System.DEFAULT_ALARM_ALERT_URI
        )
    }
    val ringtonePicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { uri ->
                if (uri.resultCode == Activity.RESULT_OK && uri.data != null) {
                    val selectedRingtone = uri.data?.getParcelableExtra(
                        RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                        Uri::class.java
                    ) ?: Settings.System.DEFAULT_ALARM_ALERT_URI
                    updatePreferences(
                        preferencesState.preferences.copy(
                            timerSoundPath = selectedRingtone.toString()
                        )
                    )
                }
            }
        )

    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_timer_settings_title)
    )

    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = stringResource(id = R.string.settings_screen_ringtone_selection_icon)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.settings_screen_ringtone_timer_title))
        },
        subtitle = {
            Text(text = "${stringResource(id = R.string.settings_screen_ringtone_subtitle)} ${context.getNameFromUri(currentRingtoneUri)}")
        },
        onClick = {
            ringtonePicker.launch(ringtoneSelectionIntent)
        }
    )

    SettingsSwitch(
        modifier = Modifier
            .height(space80)
            .testTag("timer vibrate switch"),
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
        modifier = Modifier
            .height(space80)
            .testTag("timer vibrate dropdown"),
        enabled = preferencesState.preferences.isVibrationTimerEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.timerVibrationPattern.value),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_pattern_title)) },
        items = VibrationPattern.entries.map { it.label },
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

    SettingsSwitch(
        modifier = Modifier
            .height(space80)
            .testTag("timer autoRestart switch"),
        state = rememberBooleanSettingState(preferencesState.preferences.autoRestartTimer),
        title = { Text(text = stringResource(id = R.string.settings_screen_auto_restart_timer_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_auto_restart_timer_subtitle)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    autoRestartTimer = it
                )
            )
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
