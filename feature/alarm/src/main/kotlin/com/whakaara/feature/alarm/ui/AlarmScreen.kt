package com.whakaara.feature.alarm.ui

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
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import com.whakaara.core.NotificationUtils
import com.whakaara.core.designsystem.theme.AlarmPreviewProvider
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.core.rememberPermissionStateSafe
import com.whakaara.feature.alarm.R
import com.whakaara.feature.alarm.utils.DateUtils
import com.whakaara.feature.alarm.utils.GeneralUtils.Companion.showToast
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.preferences.TimeFormat
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    preferencesState: PreferencesState,
//    alarmEventCallbacks: AlarmEventCallbacks
) {
    val notificationPermissionState = rememberPermissionStateSafe(permission = Manifest.permission.POST_NOTIFICATIONS)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
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
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                ),
                onClick = {
                    when (notificationPermissionState.status) {
                        PermissionStatus.Granted -> {
                            isDialogShown.value = !isDialogShown.value
                        }

                        else -> {
                            if (notificationPermissionState.status.shouldShowRationale) {
                                NotificationUtils.snackBarPromptPermission(
                                    scope = scope,
                                    snackBarHostState = snackbarHostState,
                                    context = context
                                )
                            } else {
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
            timeFormat = preferencesState.preferences.timeFormat,
            delete = alarmEventCallbacks::delete,
            disable = alarmEventCallbacks::disable,
            enable = alarmEventCallbacks::enable,
            reset = alarmEventCallbacks::reset,
            getInitialTimeToAlarm = alarmEventCallbacks::getInitialTimeToAlarm,
            getTimeUntilAlarmFormatted = alarmEventCallbacks::getTimeUntilAlarmFormatted
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
                    alarmEventCallbacks.create(
                        Alarm(
                            date = date,
                            subTitle = DateUtils.getAlarmTimeFormatted(
                                date = date,
                                timeFormat = preferencesState.preferences.timeFormat
                            ),
                            vibration = preferencesState.preferences.isVibrateEnabled,
                            isSnoozeEnabled = preferencesState.preferences.isSnoozeEnabled,
                            deleteAfterGoesOff = preferencesState.preferences.deleteAfterGoesOff
                        )
                    )
                    isDialogShown.value = false
                    context.showToast(
                        message = alarmEventCallbacks.getTimeUntilAlarmFormatted(date)
                    )
                },
                title = { Text(text = stringResource(id = R.string.time_picker_dialog_title)) },
                is24HourFormat = preferencesState.preferences.timeFormat == TimeFormat.TWENTY_FOUR_HOURS
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun AlarmScreenPreview(
    @PreviewParameter(AlarmPreviewProvider::class) alarm: Alarm
) {
    WhakaaraTheme {
        AlarmScreen(
            alarms = listOf(alarm),
            preferencesState = PreferencesState(),
//            alarmEventCallbacks = object : AlarmEventCallbacks {
//                override fun create(alarm: Alarm) {}
//
//                override fun delete(alarm: Alarm) {}
//
//                override fun disable(alarm: Alarm) {}
//
//                override fun enable(alarm: Alarm) {}
//
//                override fun reset(alarm: Alarm) {}
//
//                override fun getInitialTimeToAlarm(
//                    isEnabled: Boolean,
//                    time: Calendar
//                ): String {
//                    return ""
//                }
//
//                override fun getTimeUntilAlarmFormatted(date: Calendar): String {
//                    return ""
//                }
//            }
        )
    }
}
