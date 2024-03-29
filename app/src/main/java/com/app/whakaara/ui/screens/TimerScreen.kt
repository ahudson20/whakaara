package com.app.whakaara.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.clock.TimerCountdownDisplay
import com.app.whakaara.ui.clock.TimerInputField
import com.app.whakaara.ui.floatingactionbutton.rememberPermissionStateSafe
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.Spacings.spaceXxLarge
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.NotificationUtils
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_HOURS_INPUT_REGEX
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_MINUTES_AND_SECONDS_INPUT_REGEX
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimerScreen(
    timerState: TimerState,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    startTimer: () -> Unit,
    stopTimer: () -> Unit,
    pauseTimer: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val notificationPermissionState = rememberPermissionStateSafe(permission = Manifest.permission.POST_NOTIFICATIONS)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            startTimer()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spaceMedium)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spaceXxLarge)
                ) {
                    if (!timerState.isStart) {
                        FloatingActionButton(
                            shape = CircleShape,
                            modifier = Modifier.testTag("floating action button stop"),
                            elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
                            containerColor = MaterialTheme.colorScheme.error,
                            onClick = {
                                stopTimer()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = stringResource(id = R.string.stop_timer_icon_content_description)
                            )
                        }
                    }
                    FloatingActionButton(
                        modifier = Modifier.testTag("floating action button play-pause"),
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
                        onClick = {
                            if (timerState.isTimerActive) {
                                pauseTimer()
                            } else {
                                when (notificationPermissionState.status) {
                                    PermissionStatus.Granted -> {
                                        startTimer()
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
                        }
                    ) {
                        if (timerState.isTimerActive) {
                            Icon(
                                imageVector = Icons.Filled.Pause,
                                contentDescription = stringResource(id = R.string.pause_timer_icon_content_description)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = stringResource(id = R.string.start_timer_icon_content_description)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(key1 = timerState, Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            if (timerState.inputHours.isBlank()) updateHours(TIMER_INPUT_INITIAL_VALUE)
                            if (timerState.inputMinutes.isBlank()) updateMinutes(TIMER_INPUT_INITIAL_VALUE)
                            if (timerState.inputSeconds.isBlank()) updateSeconds(TIMER_INPUT_INITIAL_VALUE)
                        }
                    )
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!timerState.isTimerActive && !timerState.isTimerPaused) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = spaceMedium, end = spaceMedium),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimerInputField(
                        label = stringResource(id = R.string.timer_screen_hour_label),
                        regex = TIMER_HOURS_INPUT_REGEX,
                        updateStringEvent = StringStateEvent(
                            value = timerState.inputHours,
                            onValueChange = { newValue ->
                                updateHours(newValue)
                            }
                        )
                    )

                    TimerInputField(
                        label = stringResource(id = R.string.timer_screen_minutes_label),
                        regex = TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
                        updateStringEvent = StringStateEvent(
                            value = timerState.inputMinutes,
                            onValueChange = { newValue ->
                                updateMinutes(newValue)
                            }
                        )
                    )

                    TimerInputField(
                        label = stringResource(id = R.string.timer_screen_seconds_label),
                        regex = TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
                        updateStringEvent = StringStateEvent(
                            value = timerState.inputSeconds,
                            onValueChange = { newValue ->
                                updateSeconds(newValue)
                            }
                        )
                    )
                }
            } else {
                TimerCountdownDisplay(
                    progress = timerState.progress,
                    time = timerState.time,
                    finishTime = timerState.finishTime
                )
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerPreview() {
    WhakaaraTheme {
        TimerScreen(
            timerState = TimerState(),
            updateHours = {},
            updateMinutes = {},
            updateSeconds = {},
            startTimer = {},
            pauseTimer = {},
            stopTimer = {}
        )
    }
}
