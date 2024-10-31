package com.app.whakaara.ui.clock

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonRow
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.whakaara.model.stopwatch.Lap

@Composable
fun Stopwatch(
    stopwatchState: StopwatchState,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onLap: () -> Unit = {}
) {
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = stopwatchState.lapList) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect {
                if (stopwatchState.lapList.lastIndex >= 0) {
                    listState.animateScrollToItem(stopwatchState.lapList.lastIndex, 0)
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                StopwatchDisplay(modifier = Modifier.border(1.dp, Color.Blue), formattedTime = stopwatchState.formattedTime)
                Spacer(modifier = Modifier.height(if (stopwatchState.lapList.isNotEmpty()) spaceMedium else spaceNone))
                StopwatchLapList(
                    modifier = Modifier
                        .border(1.dp, Color.Green)
                        .fillMaxWidth(),
                    lapList = stopwatchState.lapList,
                    listState = listState
                )
            }

            FloatingActionButtonRow(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                isPlaying = stopwatchState.isActive,
                isStart = stopwatchState.isStart,
                onStop = onStop,
                onPlayPause = if (stopwatchState.isActive) onPause else onStart,
                onExtraButtonClicked = { onLap() }
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
            stopwatchState = StopwatchState(
                lapList = mutableListOf(
                    Lap(
                        time = 1000L,
                        diff = 250L
                    ),
                    Lap(
                        time = 1000L,
                        diff = 250L
                    ),
                    Lap(
                        time = 1000L,
                        diff = 250L
                    )
                )
            )
        )
    }
}
