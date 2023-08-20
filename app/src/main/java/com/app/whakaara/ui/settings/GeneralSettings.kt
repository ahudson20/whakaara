package com.app.whakaara.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.app.whakaara.R
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_DATE_SETTINGS
import com.app.whakaara.utils.constants.NotificationUtilsConstants.getAppSettingsIntent

@Composable
fun GeneralSettings() {
    val context = LocalContext.current

    Text(
        modifier = Modifier.padding(start = spaceMedium, top = spaceMedium, bottom = spaceMedium),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_general_title)
    )
    SettingsMenuLink(
        modifier = Modifier.height(space80),
        icon = {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = stringResource(
                    id = R.string.system_time_icon_content_description
                )
            )
        },
        title = { Text(text = stringResource(id = R.string.settings_screen_system_time)) },
        onClick = {
            context.startActivity(INTENT_DATE_SETTINGS)
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
            context.startActivity(getAppSettingsIntent(context.packageName))
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GeneralSettingsPreview() {
    WhakaaraTheme {
        Column {
            GeneralSettings()
        }
    }
}
