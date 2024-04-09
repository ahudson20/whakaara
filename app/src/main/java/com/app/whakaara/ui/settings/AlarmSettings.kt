package com.app.whakaara.ui.settings

import android.app.Service
import android.os.VibrationAttributes
import android.os.VibratorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.SettingsTime
import com.app.whakaara.data.preferences.VibrationPattern
import com.app.whakaara.data.preferences.VibrationPattern.Companion.SINGLE
import com.app.whakaara.data.preferences.VibrationPattern.Companion.createWaveForm
import com.app.whakaara.receiver.AppWidgetReceiver
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import kotlinx.coroutines.launch

@Composable
fun AlarmSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val vibrator = (context.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_alarm_settings_title)
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        state = rememberBooleanSettingState(preferencesState.preferences.filteredAlarmList),
        title = { Text(text = stringResource(id = R.string.settings_screen_sort_alarm_list_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_sort_alarm_list_sub_title)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    filteredAlarmList = it
                )
            )
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
        modifier = Modifier
            .height(space80)
            .testTag(tag = "alarm vibrate drop down"),
        enabled = preferencesState.preferences.isVibrateEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.vibrationPattern.value),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_pattern_title)) },
        items = VibrationPattern.values().map { it.label },
        onItemSelected = { int, _ ->
            val selection = VibrationPattern.fromOrdinalInt(value = int)
            val vibrationEffect = createWaveForm(selection = selection, repeat = SINGLE)
            val attributes = VibrationAttributes.Builder().apply {
                setUsage(VibrationAttributes.USAGE_NOTIFICATION)
            }.build()
            vibrator.vibrate(vibrationEffect, attributes)
            if (selection != preferencesState.preferences.vibrationPattern) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        vibrationPattern = selection
                    )
                )
            }
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
            val selection = SettingsTime.fromOrdinalInt(value = int)
            if (selection != preferencesState.preferences.snoozeTime) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        snoozeTime = selection
                    )
                )
            }
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        title = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_subtitle)) },
        state = rememberBooleanSettingState(preferencesState.preferences.upcomingAlarmNotification),
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    upcomingAlarmNotification = it
                )
            )
        }
    )

    SettingsListDropdown(
        modifier = Modifier.height(space80),
        enabled = preferencesState.preferences.upcomingAlarmNotification,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.upcomingAlarmNotificationTime.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_time_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_time_subtitle)) },
        items = SettingsTime.values().map { it.label },
        onItemSelected = { int, _ ->
            val selection = SettingsTime.fromOrdinalInt(value = int)
            if (selection != preferencesState.preferences.upcomingAlarmNotificationTime) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        upcomingAlarmNotificationTime = selection
                    )
                )
            }
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
            val selection = SettingsTime.fromOrdinalInt(value = int)
            if (selection != preferencesState.preferences.autoSilenceTime) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        autoSilenceTime = selection
                    )
                )
            }
        }
    )

    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = stringResource(id = R.string.settings_screen_add_widget_icon)
            )
        },
        title = { Text(text = stringResource(id = R.string.settings_screen_add_widget_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_add_widget_subtitle)) },
        onClick = {
            scope.launch {
                GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                    receiver = AppWidgetReceiver::class.java
                )
            }
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
                updatePreferences = {}
            )
        }
    }
}
