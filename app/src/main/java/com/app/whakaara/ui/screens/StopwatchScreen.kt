package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import com.app.whakaara.ui.clock.Stopwatch
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun StopwatchScreen(
    formattedTime: String,
    isActive: Boolean,
    isStart: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Stopwatch(
        formattedTime = formattedTime,
        isActive = isActive,
        isStart = isStart,
        onStart = onStart,
        onPause = onPause,
        onStop = onStop
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchScreenPreview() {
    WhakaaraTheme {
        StopwatchScreen(
            formattedTime = "01:01:01",
            isActive = false,
            isStart = true,
            onStart = {},
            onPause = {},
            onStop = {}
        )
    }
}
