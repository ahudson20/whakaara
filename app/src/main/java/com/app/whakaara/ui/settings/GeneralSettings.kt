package com.app.whakaara.ui.settings

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.storage.base.rememberFloatSettingState
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.preferences.TimeFormat.Companion.toBoolean
import com.whakaara.model.preferences.TimeFormat.Companion.toTimeFormat
import kotlin.math.roundToInt

@Composable
fun GeneralSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: TimeFormat) -> Unit
) {
    val context = LocalContext.current
    val intentAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val originalAlarmVolume = remember { mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_ALARM)) }
    val maxValue = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM) }

    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_general_title)
    )

    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_chronic_24),
                contentDescription = stringResource(id = R.string.system_time_icon_content_description)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.settings_screen_system_time))
        },
        onClick = {
            context.startActivity(Intent(Settings.ACTION_DATE_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        }
    )

    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.settings_screen_app_settings)
            )
        },
        title = { Text(text = stringResource(id = R.string.settings_screen_app_settings)) },
        onClick = {
            context.startActivity(
                intentAppSettings.apply {
                    data = Uri.fromParts(NotificationUtilsConstants.INTENT_PACKAGE, context.packageName, null)
                }
            )
        }
    )

    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                imageVector = Icons.Default.BatterySaver,
                contentDescription = stringResource(id = R.string.settings_screen_battery_optimization_icon_content_description)
            )
        },
        title = { Text(text = stringResource(id = R.string.settings_screen_battery_optimization)) },
        onClick = {
            val intent =
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            context.startActivity(
                intent.apply {
                    Uri.fromParts(NotificationUtilsConstants.INTENT_PACKAGE, context.packageName, null)
                }
            )
        }
    )

    SettingsSlider(
        title = { Text(text = stringResource(id = R.string.settings_screen_alarm_volume_title, originalAlarmVolume.intValue)) },
        icon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.VolumeUp,
                contentDescription = stringResource(id = R.string.settings_screen_alarm_volume_icon)
            )
        },
        state = rememberFloatSettingState(originalAlarmVolume.intValue.toFloat()),
        steps = maxValue - 2,
        valueRange = 1f..maxValue.toFloat(),
        onValueChange = { value ->
            originalAlarmVolume.intValue = value.roundToInt()
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalAlarmVolume.intValue, 0)
        }
    )

    SettingsListDropdown(
        modifier = Modifier.height(space80),
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.appTheme.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_app_theme_title)) },
        items = AppTheme.entries.map { it.label },
        onItemSelected = { int, _ ->
            val selection = AppTheme.fromOrdinalInt(value = int)
            if (selection != preferencesState.preferences.appTheme) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        appTheme = selection
                    )
                )
            }
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        state = rememberBooleanSettingState(preferencesState.preferences.dynamicTheme),
        title = { Text(text = stringResource(id = R.string.settings_screen_dynamic_theme_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_dynamic_theme_subtitle)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    dynamicTheme = it
                )
            )
        }
    )

    SettingsSwitch(
        modifier = Modifier.height(space80),
        state = rememberBooleanSettingState(preferencesState.preferences.timeFormat.toBoolean()),
        title = { Text(text = stringResource(id = R.string.settings_screen_24_hour_format_title)) },
        subtitle = { Text(text = stringResource(id = R.string.settings_screen_24_hour_format_subtitle)) },
        onCheckedChange = {
            updatePreferences(
                preferencesState.preferences.copy(
                    timeFormat = it.toTimeFormat()
                )
            )
            updateAllAlarmSubtitles(it.toTimeFormat())
        }
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun GeneralSettingsPreview() {
    WhakaaraTheme {
        Column {
            GeneralSettings(
                preferencesState = PreferencesState(),
                updatePreferences = {},
                updateAllAlarmSubtitles = {}
            )
        }
    }
}
