package com.app.whakaara.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.clock.TimerCountdownDisplay
import com.app.whakaara.ui.clock.TimerInputField
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonRow
import com.app.whakaara.ui.floatingactionbutton.rememberPermissionStateSafe
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utility.DateUtils
import com.app.whakaara.utility.NotificationUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.whakaara.core.constants.DateUtilsConstants.TIMER_HOURS_INPUT_REGEX
import com.whakaara.core.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.whakaara.core.constants.DateUtilsConstants.TIMER_MINUTES_AND_SECONDS_INPUT_REGEX
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimerScreen(
    timerState: TimerState,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    startTimer: () -> Unit,
    stopTimer: () -> Unit,
    restartTimer: (autoRestartTimer: Boolean) -> Unit,
    pauseTimer: () -> Unit,
    is24HourFormat: Boolean,
    autoRestartTimer: Boolean
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
            FloatingActionButtonRow(
                isPlaying = timerState.isTimerActive,
                isStart = timerState.isStart,
                isPlayButtonVisible = timerState.inputHours != TIMER_INPUT_INITIAL_VALUE ||
                    timerState.inputMinutes != TIMER_INPUT_INITIAL_VALUE ||
                    timerState.inputSeconds != TIMER_INPUT_INITIAL_VALUE,
                onStop = stopTimer,
                onPlayPause = {
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
                },
                onExtraButtonClicked = {
                    restartTimer(autoRestartTimer)
                }
            )
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
            AnimatedContent(
                targetState = (!timerState.isTimerActive && !timerState.isTimerPaused),
                transitionSpec = {
                    (scaleIn(animationSpec = tween(1000, delayMillis = 90)))
                        .togetherWith(scaleOut(animationSpec = tween(600)))
                },
                label = ""
            ) { targetState ->
                if (targetState) {
                    Row(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = spaceMedium, end = spaceMedium),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimerInputField(
                            label = stringResource(id = R.string.timer_screen_hour_label),
                            regex = TIMER_HOURS_INPUT_REGEX,
                            updateStringEvent =
                            StringStateEvent(
                                value = timerState.inputHours,
                                onValueChange = { newValue ->
                                    updateHours(newValue)
                                }
                            )
                        )

                        TimerInputField(
                            label = stringResource(id = R.string.timer_screen_minutes_label),
                            regex = TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
                            updateStringEvent =
                            StringStateEvent(
                                value = timerState.inputMinutes,
                                onValueChange = { newValue ->
                                    updateMinutes(newValue)
                                }
                            )
                        )

                        TimerInputField(
                            label = stringResource(id = R.string.timer_screen_seconds_label),
                            regex = TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
                            updateStringEvent =
                            StringStateEvent(
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
                        finishTime =
                        if (timerState.isTimerPaused) {
                            context.getString(R.string.timer_screen_paused)
                        } else {
                            DateUtils.getAlarmTimeFormatted(
                                date =
                                Calendar.getInstance().apply {
                                    add(
                                        Calendar.MILLISECOND,
                                        timerState.millisecondsFromTimerInput.toInt()
                                    )
                                },
                                is24HourFormatEnabled = is24HourFormat
                            )
                        }
                    )
                }
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
            stopTimer = {},
            restartTimer = {},
            is24HourFormat = false,
            autoRestartTimer = true
        )
    }
}
