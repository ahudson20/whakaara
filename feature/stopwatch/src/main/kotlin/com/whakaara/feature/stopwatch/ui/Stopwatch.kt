package com.whakaara.feature.stopwatch.ui

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
import androidx.compose.ui.unit.dp
import com.whakaara.core.designsystem.FloatingActionButtonRow
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.Spacings.spaceNone
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.stopwatch.Lap
import com.whakaara.model.stopwatch.StopwatchState

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
                StopwatchDisplay(formattedTime = stopwatchState.formattedTime)
                Spacer(modifier = Modifier.height(if (stopwatchState.lapList.isNotEmpty()) spaceMedium else spaceNone))
                if (stopwatchState.lapList.isNotEmpty()) {
                    StopwatchHeader()
                }
                StopwatchLapList(
                    modifier = Modifier.fillMaxWidth(),
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
