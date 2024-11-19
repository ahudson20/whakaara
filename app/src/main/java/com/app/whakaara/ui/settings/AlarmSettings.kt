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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
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
import com.app.whakaara.receiver.AppWidgetReceiver
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space100
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.app.whakaara.utility.GeneralUtils.Companion.getNameFromUri
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.VibrationPattern
import com.whakaara.model.preferences.VibrationPattern.Companion.SINGLE
import com.whakaara.model.preferences.VibrationPattern.Companion.createWaveForm
import kotlinx.coroutines.launch

@Composable
fun AlarmSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification: (shouldEnableUpcomingAlarmNotification: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val vibrator = (context.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator

    val currentRingtoneUri: Uri = if (preferencesState.preferences.alarmSoundPath.isNotEmpty()) {
        Uri.parse(preferencesState.preferences.alarmSoundPath)
    } else {
        Settings.System.DEFAULT_ALARM_ALERT_URI
    }

    val ringtoneSelectionIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, context.getString(R.string.ringtone_selection_activity_title))
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentRingtoneUri)
        putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI)
    }

    val ringtonePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { uri ->
            if (uri.resultCode == Activity.RESULT_OK && uri.data != null) {
                val selectedRingtone = uri.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java) ?: Settings.System.DEFAULT_ALARM_ALERT_URI
                updatePreferences(
                    preferencesState.preferences.copy(
                        alarmSoundPath = selectedRingtone.toString()
                    )
                )
            }
        }
    )

    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_alarm_settings_title)
    )

    SettingsMenuLink(
        modifier = Modifier.height(space100),
        icon = {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = stringResource(id = R.string.settings_screen_ringtone_selection_icon)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.settings_screen_ringtone_alarm_title))
        },
        subtitle = {
            Text(text = "${stringResource(id = R.string.settings_screen_ringtone_subtitle)} ${context.getNameFromUri(currentRingtoneUri)}")
        },
        onClick = {
            ringtonePicker.launch(ringtoneSelectionIntent)
        }
    )

    SettingsListDropdown(
        modifier = Modifier
            .height(space100)
            .testTag("alarm gradual dropdown"),
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.gradualSoundDuration.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_gradual_volume_increase_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_gradual_volume_increase_subtitle)) },
        items = GradualSoundDuration.entries.map { context.getString(it.getStringResource(it.ordinal)) },
        onItemSelected = { int, _ ->
            val selection = GradualSoundDuration.fromOrdinalInt(value = int)
            if (selection != preferencesState.preferences.gradualSoundDuration) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        gradualSoundDuration = selection
                    )
                )
            }
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space100),
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
        modifier = Modifier.height(space100),
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
            .height(space100)
            .testTag(tag = "alarm vibrate drop down"),
        enabled = preferencesState.preferences.isVibrateEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.vibrationPattern.value),
        title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_pattern_title)) },
        items = VibrationPattern.entries.map { context.getString(it.getStringResource(it.ordinal)) },
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
        modifier = Modifier.height(space100),
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
        modifier = Modifier.height(space100),
        enabled = preferencesState.preferences.isSnoozeEnabled,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.snoozeTime.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_snooze_duration_title)) },
        items = SettingsTime.entries.map { context.getString(it.getStringResource(it.ordinal)) },
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
        modifier = Modifier.height(space100),
        title = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_subtitle)) },
        state = rememberBooleanSettingState(preferencesState.preferences.upcomingAlarmNotification),
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    upcomingAlarmNotification = it
                )
            )
            updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(it)
        }
    )

    SettingsListDropdown(
        modifier = Modifier.height(space100),
        enabled = preferencesState.preferences.upcomingAlarmNotification,
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.upcomingAlarmNotificationTime.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_time_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_upcoming_alarm_notification_time_subtitle)) },
        items = SettingsTime.entries.map { context.getString(it.getStringResource(it.ordinal)) },
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
        modifier = Modifier.height(space100),
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
        modifier = Modifier.height(space100),
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.autoSilenceTime.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_auto_silence_subtitle)) },
        items = SettingsTime.entries.map { context.getString(it.getStringResource(it.ordinal)) },
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
        modifier = Modifier.height(space100),
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
                updatePreferences = {},
                updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification = {}
            )
        }
    }
}
