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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonRow
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun Stopwatch(
    stopwatchState: StopwatchState,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onLap: () -> Unit = {},
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButtonRow(
                isPlaying = stopwatchState.isActive,
                isStart = stopwatchState.isStart,
                onStop = onStop,
                onPlayPause = if (stopwatchState.isActive) onPause else onStart,
                onExtraButtonClicked = {
                    onLap()
                },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            StopwatchDisplay(formattedTime = stopwatchState.formattedTime)
            Spacer(modifier = Modifier.height(if (stopwatchState.lapList.isNotEmpty()) spaceMedium else spaceNone))
            StopwatchLapList(lapList = stopwatchState.lapList, listState = listState)
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchPreview() {
    WhakaaraTheme {
        Stopwatch(
            stopwatchState = StopwatchState(),
        )
    }
}
