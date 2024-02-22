package com.app.whakaara.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonPlayPauseStop
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun Stopwatch(
    formattedTime: String,
    isActive: Boolean,
    isStart: Boolean,
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
                    isPlaying = isActive,
                    isStart = isStart,
                    onStop = onStop,
                    onPause = onPause,
                    onStart = onStart
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchPreview() {
    WhakaaraTheme {
        Stopwatch(
            formattedTime = "01:01:01",
            isActive = false,
            isStart = true
        )
    }
}
