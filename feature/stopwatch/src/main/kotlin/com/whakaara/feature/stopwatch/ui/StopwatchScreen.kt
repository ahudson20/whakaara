package com.whakaara.feature.stopwatch.ui

import androidx.compose.runtime.Composable
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.stopwatch.StopwatchState

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
