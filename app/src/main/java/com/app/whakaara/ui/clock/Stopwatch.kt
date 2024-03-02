package com.app.whakaara.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonPlayPauseStop
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun Stopwatch(
    stopwatchState: StopwatchState,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {}
) {
    Scaffold(
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spaceMedium)
            ) {
                FloatingActionButtonPlayPauseStop(
                    isPlaying = stopwatchState.isActive,
                    isStart = stopwatchState.isStart,
                    onStop = onStop,
                    onPause = onPause,
                    onStart = onStart,
                    askForPermissions = false
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StopwatchDisplay(formattedTime = stopwatchState.formattedTime)
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchPreview() {
    WhakaaraTheme {
        Stopwatch(
            stopwatchState = StopwatchState()
        )
    }
}
