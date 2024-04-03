package com.app.whakaara.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.card.CardContainerSwipeToDismiss
import com.app.whakaara.ui.floatingactionbutton.rememberPermissionStateSafe
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.DateUtils.Companion.getTimeUntilAlarmFormatted
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import com.app.whakaara.utils.NotificationUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    preferencesState: PreferencesState,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    create: (alarm: Alarm) -> Unit
) {
    val notificationPermissionState = rememberPermissionStateSafe(permission = Manifest.permission.POST_NOTIFICATIONS)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
            if (wasGranted) {
                isDialogShown.value = !isDialogShown.value
            }
        }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                onClick = {
                    /**PERMISSION GRANTED**/
                    when (notificationPermissionState.status) {
                        PermissionStatus.Granted -> {
                            isDialogShown.value = !isDialogShown.value
                        }
                        else -> {
                            /**PERMISSION DENIED - SHOW PROMPT**/
                            if (notificationPermissionState.status.shouldShowRationale) {
                                NotificationUtils.snackBarPromptPermission(
                                    scope = scope,
                                    snackBarHostState = snackbarHostState,
                                    context = context
                                )
                            } else {
                                /**FIRST TIME ACCESSING**/
                                /**OR USER DOESN'T WANT TO BE ASKED AGAIN**/
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.floating_action_button_icon_description)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        CardContainerSwipeToDismiss(
            modifier = Modifier.padding(innerPadding),
            alarms = alarms,
            is24HourFormat = preferencesState.preferences.is24HourFormat,
            delete = delete,
            disable = disable,
            enable = enable,
            reset = reset
        )

        AnimatedVisibility(isDialogShown.value) {
            TimePickerDialog(
                onDismissRequest = { isDialogShown.value = false },
                initialTime = LocalTime.now().plusMinutes(1).noSeconds(),
                onTimeChange = {
                    val date = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, it.hour)
                        set(Calendar.MINUTE, it.minute)
                        set(Calendar.SECOND, 0)
                    }
                    create(
                        Alarm(
                            date = date,
                            subTitle = DateUtils.getAlarmTimeFormatted(
                                date = date,
                                is24HourFormatEnabled = preferencesState.preferences.is24HourFormat
                            ),
                            vibration = preferencesState.preferences.isVibrateEnabled,
                            isSnoozeEnabled = preferencesState.preferences.isSnoozeEnabled,
                            deleteAfterGoesOff = preferencesState.preferences.deleteAfterGoesOff
                        )
                    )
                    isDialogShown.value = false
                    context.showToast(
                        message = context.getTimeUntilAlarmFormatted(date = date)
                    )
                },
                title = { Text(text = stringResource(id = R.string.time_picker_dialog_title)) },
                is24HourFormat = preferencesState.preferences.is24HourFormat
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun AlarmScreenPreview() {
    WhakaaraTheme {
        AlarmScreen(
            alarms = listOf(
                Alarm(
                    date = Calendar.getInstance().apply {
                        set(Calendar.YEAR, 2023)
                        set(Calendar.DAY_OF_MONTH, 13)
                        set(Calendar.MONTH, 6)
                        set(Calendar.HOUR_OF_DAY, 14)
                        set(Calendar.MINUTE, 34)
                        set(Calendar.SECOND, 0)
                    },
                    title = "First Alarm Title",
                    subTitle = "14:34 PM"
                )
            ),
            preferencesState = PreferencesState(),
            delete = {},
            disable = {},
            enable = {},
            reset = {},
            create = {}
        )
    }
}
