package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import com.app.whakaara.ui.clock.Timer
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun TimerScreen(
    formattedTime: String,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    Timer(
        formattedTime = formattedTime,
        onStart = onStart,
        onPause = onPause,
        onStop = onStop
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerPreview() {
    WhakaaraTheme {
        TimerScreen(
            formattedTime = "01:01:01",
            onStart = {},
            onPause = {},
            onStop = {}
        )
    }
}
