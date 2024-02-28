package com.app.whakaara.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.clock.TimerCountdownDisplay
import com.app.whakaara.ui.clock.TimerInputField
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonPlayPauseStop
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_HOURS_INPUT_REGEX
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_MINUTES_AND_SECONDS_INPUT_REGEX

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

    Scaffold(
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spaceMedium)
            ) {
                // TODO: Add notification permission check here.
                FloatingActionButtonPlayPauseStop(
                    isPlaying = timerState.isTimerActive,
                    isStart = timerState.isStart,
                    onStop = stopTimer,
                    onPause = pauseTimer,
                    onStart = startTimer
                )
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
                    time = timerState.time
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
