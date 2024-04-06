package com.app.whakaara.ui.clock

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.whakaara.state.Lap
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space40
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.Spacings.spaceXLarge
import com.app.whakaara.ui.theme.Spacings.spaceXSmall
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils

@Composable
fun StopwatchLapList(
    stopwatchState: StopwatchState,
    listState: LazyListState
) {
    LazyColumn(
        modifier = Modifier
            .padding(top = if (stopwatchState.lapList.isNotEmpty()) spaceNone else spaceMedium)
            .animateContentSize()
            .height(if (stopwatchState.lapList.isNotEmpty()) 350.dp else spaceNone),
        verticalArrangement = Arrangement.Top,
        state = listState,
        reverseLayout = true
    ) {
        itemsIndexed(stopwatchState.lapList) { index, item ->
            LapCell(index = index, lap = item)
            Spacer(modifier = Modifier.fillMaxWidth().height(spaceXSmall))
        }
    }
}

@Composable
private fun LapCell(index: Int, lap: Lap) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = space40, end = space40)
    ) {
        Card(shape = RoundedCornerShape(spaceMedium)) {
            Box(
                modifier = Modifier.padding(all = spaceMedium)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = index.inc().toString())
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = spaceXLarge, end = spaceMedium)
                    ) {
                        Text(
                            text = DateUtils.formatTimeForStopwatchLap(lap.time)
                        )
                    }
                    Text(text = DateUtils.formatTimeForStopwatchLap(lap.diff))
                }
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchLapListPreview() {
    WhakaaraTheme {
        StopwatchLapList(
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
            ),
            listState = rememberLazyListState()
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun LapCellPreview() {
    WhakaaraTheme {
        LapCell(
            index = 1,
            lap = Lap(
                time = 1000L,
                diff = 250L
            )
        )
    }
}
