package com.app.whakaara.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.app.whakaara.R
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.state.PreferencesState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    preferencesState: PreferencesState,
    viewModel: MainViewModel
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val intent = Intent(Settings.ACTION_DATE_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    val test = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = Uri.fromParts("package", context.packageName, null)
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            Text(
                modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                text = "General settings"
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
                    context.startActivity(intent)
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
                    context.startActivity(test)
                }
            )
            Divider()
            Text(
                modifier = modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(id = R.string.settings_screen_alarm_settings_title)
            )
            SettingsSwitch(
                modifier = modifier.height(80.dp),
                state = rememberBooleanSettingState(preferencesState.preferences.isVibrateEnabled),
                title = { Text(text = stringResource(id = R.string.settings_screen_vibrate_title)) },
                subtitle = { Text(text = stringResource(id = R.string.settings_screen_vibrate_subtitle)) },
                onCheckedChange = {
                    viewModel.updatePreferences(
                        preferencesState.preferences.copy(
                            isVibrateEnabled = it
                        )
                    )
                }
            )
            SettingsSwitch(
                modifier = modifier.height(80.dp),
                title = { Text(text = stringResource(id = R.string.settings_screen_snooze_title)) },
                subtitle = { Text(text = stringResource(id = R.string.settings_screen_snooze_subtitle)) },
                state = rememberBooleanSettingState(preferencesState.preferences.isSnoozeEnabled),
                onCheckedChange = {
                    viewModel.updatePreferences(
                        preferencesState.preferences.copy(
                            isSnoozeEnabled = it
                        )
                    )
                }
            )
            SettingsSwitch(
                modifier = modifier.height(80.dp),
                title = { Text(text = stringResource(id = R.string.settings_screen_delete_title)) },
                subtitle = { Text(text = stringResource(id = R.string.settings_screen_delete_subtitle)) },
                state = rememberBooleanSettingState(preferencesState.preferences.deleteAfterGoesOff),
                onCheckedChange = {
                    viewModel.updatePreferences(
                        preferencesState.preferences.copy(
                            deleteAfterGoesOff = it
                        )
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        preferencesState = PreferencesState(),
        viewModel = hiltViewModel()
    )
}
