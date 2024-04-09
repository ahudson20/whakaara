package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.ui.clock.Stopwatch
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun StopwatchScreen(
    stopwatchState: StopwatchState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onLap: () -> Unit
) {
    Stopwatch(
        stopwatchState = stopwatchState,
        onStart = onStart,
        onPause = onPause,
        onStop = onStop,
        onLap = onLap
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchScreenPreview() {
    WhakaaraTheme {
        StopwatchScreen(
            stopwatchState = StopwatchState(),
            onStart = {},
            onPause = {},
            onStop = {},
            onLap = {}
        )
    }
}
