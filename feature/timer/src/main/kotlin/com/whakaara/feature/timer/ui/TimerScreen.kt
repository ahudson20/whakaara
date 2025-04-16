package com.whakaara.feature.timer.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.timer.R
import com.whakaara.feature.timer.util.DateUtils
import com.whakaara.model.StringStateEvent
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.timer.TimerState
import java.util.Calendar

@Composable
fun TimerScreen(
    timerState: TimerState,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    timeFormat: TimeFormat
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
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
                    modifier = Modifier
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
            } else {
                TimerCountdownDisplay(
                    progress = timerState.progress,
                    time = timerState.time,
                    finishTime = if (timerState.isTimerPaused) {
                        context.getString(R.string.timer_screen_paused)
                    } else {
                        DateUtils.getTimerFinishFormatted(
                            date = Calendar.getInstance().apply {
                                add(
                                    Calendar.MILLISECOND,
                                    timerState.millisecondsFromTimerInput.toInt()
                                )
                            },
                            timeFormat = timeFormat
                        )
                    }
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
            timeFormat = TimeFormat.TWELVE_HOURS
        )
    }
}
