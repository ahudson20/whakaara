package com.app.whakaara.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.app.whakaara.R
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.NotificationUtilsConstants

@Composable
fun GeneralSettings() {
    val context = LocalContext.current
    val intentAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

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
}

@Composable
@ThemePreviews
@FontScalePreviews
fun GeneralSettingsPreview() {
    WhakaaraTheme {
        Column {
            GeneralSettings()
        }
    }
}
