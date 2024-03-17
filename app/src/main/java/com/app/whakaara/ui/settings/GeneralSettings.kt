package com.app.whakaara.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.storage.base.rememberIntSettingState
import com.alorma.compose.settings.ui.SettingsListDropdown
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.app.whakaara.R
import com.app.whakaara.data.preferences.AppTheme
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.GeneralUtils.Companion.getNameFromUri
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.RINGTONE_SELECTOR_TITLE

@Composable
fun GeneralSettings(
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    val context = LocalContext.current
    val currentRingtoneUri: Uri = if (preferencesState.preferences.alarmSoundPath.isNotEmpty()) {
        Uri.parse(preferencesState.preferences.alarmSoundPath)
    } else {
        Settings.System.DEFAULT_ALARM_ALERT_URI
    }
    val intentAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    val ringtoneSelectionIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, RINGTONE_SELECTOR_TITLE)
        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentRingtoneUri)
        putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI)
    }

    val ringtonePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { uri ->
            if (uri.resultCode == RESULT_OK && uri.data != null) {
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
        text = stringResource(id = R.string.settings_screen_general_title)
    )

    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_chronic_24),
                contentDescription = stringResource(
                    id = R.string.system_time_icon_content_description
                )
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
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = stringResource(id = R.string.settings_screen_ringtone_selection_icon)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.settings_screen_ringtone_title))
        },
        subtitle = {
            Text(text = "${stringResource(id = R.string.settings_screen_ringtone_subtitle)} ${context.getNameFromUri(currentRingtoneUri)}")
        },
        onClick = {
            ringtonePicker.launch(ringtoneSelectionIntent)
        }
    )

    SettingsListDropdown(
        modifier = Modifier.height(space80),
        state = rememberIntSettingState(defaultValue = preferencesState.preferences.appTheme.ordinal),
        title = { Text(text = stringResource(id = R.string.settings_screen_app_theme_title)) },
        items = AppTheme.values().map { it.label },
        onItemSelected = { int, _ ->
            updatePreferences(
                preferencesState.preferences.copy(
                    appTheme = AppTheme.fromOrdinalInt(value = int)
                )
            )
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
                updatePreferences = {}
            )
        }
    }
}
