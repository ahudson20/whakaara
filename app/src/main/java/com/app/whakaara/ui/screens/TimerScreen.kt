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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.state.StringStateEvent
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
fun TimerScreen() {
    val focusManager = LocalFocusManager.current
    var inputHours by remember { mutableStateOf(TIMER_INPUT_INITIAL_VALUE) }
    var inputMinutes by remember { mutableStateOf(TIMER_INPUT_INITIAL_VALUE) }
    var inputSeconds by remember { mutableStateOf(TIMER_INPUT_INITIAL_VALUE) }

    Scaffold(
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spaceMedium)
            ) {
                FloatingActionButtonPlayPauseStop(
                    isPlaying = false,
                    isStart = true,
                    onStop = { /** TODO */ },
                    onPause = { /** TODO */ },
                    onStart = { /** TODO */ }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            if (inputHours.isEmpty()) inputHours = TIMER_INPUT_INITIAL_VALUE
                            if (inputMinutes.isEmpty()) inputMinutes = TIMER_INPUT_INITIAL_VALUE
                            if (inputSeconds.isEmpty()) inputSeconds = TIMER_INPUT_INITIAL_VALUE
                        }
                    )
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        value = inputHours,
                        onValueChange = { newValue ->
                            inputHours = newValue
                        }
                    )
                )

                TimerInputField(
                    label = stringResource(id = R.string.timer_screen_minutes_label),
                    regex = TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
                    updateStringEvent = StringStateEvent(
                        value = inputMinutes,
                        onValueChange = { newValue ->
                            inputMinutes = newValue
                        }
                    )
                )

                TimerInputField(
                    label = stringResource(id = R.string.timer_screen_seconds_label),
                    regex = TIMER_MINUTES_AND_SECONDS_INPUT_REGEX,
                    updateStringEvent = StringStateEvent(
                        value = inputSeconds,
                        onValueChange = { newValue ->
                            inputSeconds = newValue
                        }
                    )
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
        TimerScreen()
    }
}
