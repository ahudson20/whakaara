package com.whakaara.feature.timer.ui

import android.content.res.Configuration
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.timer.R
import com.whakaara.model.StringStateEvent
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.timer.TimerState

@Composable
fun TimerScreen(
    timerState: TimerState,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    timeFormat: TimeFormat,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
) {
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isLargeScreen = (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM || windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED)
    val isSplitMode = isLandscape && isLargeScreen

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(key1 = timerState, Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                        if (timerState.inputHours.isBlank()) updateHours(DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE)
                        if (timerState.inputMinutes.isBlank()) updateMinutes(DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE)
                        if (timerState.inputSeconds.isBlank()) updateSeconds(DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE)
                    }
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSplitMode) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spaceMedium),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimerInputView(
                    modifier = Modifier.weight(1F),
                    timerState = timerState,
                    updateHours = updateHours,
                    updateMinutes = updateMinutes,
                    updateSeconds = updateSeconds
                )

                TimerCountdownView(
                    modifier = Modifier.weight(1F),
                    timerState = timerState,
                    timeFormat = timeFormat
                )
            }
        } else {
            AnimatedContent(
                targetState = (!timerState.isTimerActive && !timerState.isTimerPaused),
                transitionSpec = {
                    (scaleIn(animationSpec = tween(1000, delayMillis = 90)))
                        .togetherWith(scaleOut(animationSpec = tween(600)))
                },
                label = ""
            ) { targetState ->
                if (targetState) {
                    TimerInputView(
                        timerState = timerState,
                        updateHours = updateHours,
                        updateMinutes = updateMinutes,
                        updateSeconds = updateSeconds
                    )
                } else {
                    TimerCountdownView(
                        timerState = timerState,
                        timeFormat = timeFormat
                    )
                }
            }
        }
    }
}

@Composable
fun TimerInputView(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = spaceMedium, end = spaceMedium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TimerInputField(
            label = stringResource(id = R.string.timer_screen_hour_label),
            regex = DateUtilsConstants.TIMER_HOURS_INPUT_REGEX,
            updateStringEvent = StringStateEvent(
                value = timerState.inputHours,
                onValueChange = { newValue ->
                    updateHours(newValue)
                }
            )
        )

        TimerInputField(
            label = stringResource(id = R.string.timer_screen_minutes_label),
            regex = DateUtilsConstants.TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
            updateStringEvent = StringStateEvent(
                value = timerState.inputMinutes,
                onValueChange = { newValue ->
                    updateMinutes(newValue)
                }
            )
        )

        TimerInputField(
            label = stringResource(id = R.string.timer_screen_seconds_label),
            regex = DateUtilsConstants.TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
            updateStringEvent = StringStateEvent(
                value = timerState.inputSeconds,
                onValueChange = { newValue ->
                    updateSeconds(newValue)
                }
            )
        )
    }
}

@Composable
fun TimerCountdownView(
    modifier: Modifier = Modifier,
    timerState: TimerState,
    timeFormat: TimeFormat
) {
    TimerCountdownDisplay(
        modifier = modifier,
        progress = timerState.progress,
        time = timerState.time,
        isPaused = timerState.isTimerPaused,
        isStart = timerState.isStart,
        millisecondsFromTimerInput = timerState.millisecondsFromTimerInput,
        timeFormat = timeFormat
    )
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
            timeFormat = TimeFormat.TWELVE_HOURS
        )
    }
}
