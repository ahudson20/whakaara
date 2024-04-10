package com.app.whakaara.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonRow
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import kotlinx.coroutines.launch

@Composable
fun Stopwatch(
    stopwatchState: StopwatchState,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onLap: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Scaffold(
        floatingActionButton = {
            FloatingActionButtonRow(
                isPlaying = stopwatchState.isActive,
                isStart = stopwatchState.isStart,
                onStop = onStop,
                onPlayPause = if (stopwatchState.isActive) onPause else onStart,
                onExtraButtonClicked = {
                    onLap()
                    scope.launch {
                        if (stopwatchState.lapList.lastIndex >= 0) {
                            listState.animateScrollToItem(stopwatchState.lapList.lastIndex, 0)
                        }
                    }
                }
            )
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
            Spacer(modifier = Modifier.height(if (stopwatchState.lapList.isNotEmpty()) spaceMedium else spaceNone))
            StopwatchLapList(stopwatchState = stopwatchState, listState = listState)
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
