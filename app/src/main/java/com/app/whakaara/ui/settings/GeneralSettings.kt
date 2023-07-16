package com.app.whakaara.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.app.whakaara.R
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.NotificationUtilsConstants

@Composable
fun GeneralSettings(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dateSettingsIntent = Intent(Settings.ACTION_DATE_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.fromParts(NotificationUtilsConstants.INTENT_PACKAGE, context.packageName, null)
    }

    Text(
        modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
        style = MaterialTheme.typography.titleMedium,
        text = stringResource(id = R.string.settings_screen_general_title)
    )
    SettingsMenuLink(
        modifier = modifier.height(80.dp),
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
            context.startActivity(dateSettingsIntent)
        }
    )
    SettingsMenuLink(
        modifier = modifier.height(80.dp),
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.settings_screen_app_settings)
            )
        },
        title = { Text(text = stringResource(id = R.string.settings_screen_app_settings)) },
        onClick = {
            context.startActivity(appSettingsIntent)
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
