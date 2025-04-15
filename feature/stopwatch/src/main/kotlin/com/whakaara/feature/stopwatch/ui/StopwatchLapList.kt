package com.whakaara.feature.stopwatch.ui

import android.content.res.Configuration
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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Shapes
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.Spacings.spaceNone
import com.whakaara.core.designsystem.theme.Spacings.spaceSmall
import com.whakaara.core.designsystem.theme.Spacings.spaceXLarge
import com.whakaara.core.designsystem.theme.Spacings.spaceXSmall
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.stopwatch.R
import com.whakaara.feature.stopwatch.util.DateUtils
import com.whakaara.model.stopwatch.Lap
import java.util.Locale

@Composable
fun StopwatchLapList(
    modifier: Modifier = Modifier,
    lapList: MutableList<Lap>,
    listState: LazyListState
) {
    val minDiff = lapList.minOfOrNull { it.diff }
    val maxDiff = lapList.maxOfOrNull { it.diff }
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val adjustedModifier = when {
        isPortrait && lapList.isNotEmpty() -> modifier.height(screenHeightDp * 0.45f)
        isPortrait -> modifier.height(spaceNone)
        else -> modifier
    }

    LazyColumn(
        modifier = adjustedModifier
            .fillMaxWidth()
            .animateContentSize()
            .verticalFadingEdge(
                lazyListState = listState,
                length = 100.dp
            ),
        verticalArrangement = Arrangement.Top,
        state = listState,
        reverseLayout = lapList.isNotEmpty()
    ) {
        if (lapList.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.stopwatch_lap_list_empty),
                    textAlign = TextAlign.Center
                )
            }
        }

        itemsIndexed(items = lapList) { index, item ->
            if (index != 0) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spaceXSmall)

                )
            }

            if (index == 0) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spaceSmall)
                )
            }

            LapCell(
                index = index,
                lap = item,
                isMinDiff = (item.diff == minDiff),
                isMaxDiff = (item.diff == maxDiff)
            )
        }
    }
}

@Composable
private fun LapCell(
    modifier: Modifier = Modifier,
    index: Int,
    lap: Lap,
    isMinDiff: Boolean = false,
    isMaxDiff: Boolean = false
) {
    val diffTextColor = when {
        isMinDiff -> com.whakaara.core.designsystem.theme.lightGreen
        isMaxDiff -> MaterialTheme.colorScheme.error
        else -> Color.Unspecified
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = spaceMedium, end = spaceMedium)
    ) {
        Card(shape = Shapes.small) {
            Box(
                modifier = Modifier.padding(top = spaceMedium, bottom = spaceMedium)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = String.format(locale = Locale.ROOT, format = "%02d", index.inc()),
                        color = diffTextColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier
                            .weight(3f)
                            .padding(start = spaceXLarge, end = spaceMedium),
                        text = DateUtils.formatTimeForStopwatchLap(lap.time),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(2f),
                        text = DateUtils.formatTimeForStopwatchLap(lap.diff),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

fun Modifier.verticalFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "length"
        value = length
    }
) {
    val color = edgeColor ?: MaterialTheme.colorScheme.background

    drawWithContent {
        val topFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo
                .run {
                    val firstItem = visibleItemsInfo.firstOrNull()
                    if (firstItem == null) {
                        0f
                    } else {
                        when {
                            visibleItemsInfo.size in 0..1 -> 0f
                            firstItem.index > 0 -> 1f // Added
                            firstItem.offset == viewportStartOffset -> 0f
                            firstItem.offset < viewportStartOffset ->
                                firstItem.run { kotlin.math.abs(offset) / size.toFloat() }

                            else -> 1f
                        }
                    }
                }
                .coerceAtMost(1f) * length.value
        }
        val bottomFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo
                .run {
                    val lastItem = visibleItemsInfo.lastOrNull()
                    if (lastItem == null) {
                        0f
                    } else {
                        when {
                            visibleItemsInfo.size in 0..1 -> 0f
                            lastItem.index < totalItemsCount - 1 -> 1f // Added
                            lastItem.offset + lastItem.size <= viewportEndOffset -> 0f // added the <=
                            lastItem.offset + lastItem.size > viewportEndOffset ->
                                lastItem.run {
                                    (size - (viewportEndOffset - offset)) / size.toFloat() // Fixed the percentage computation
                                }

                            else -> 1f
                        }
                    }
                }
                .coerceAtMost(1f) * length.value
        }

        drawContent()

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    color,
                    Color.Transparent
                ),
                startY = 0f,
                endY = bottomFadingEdgeStrength
            ),
            size = size.copy(height = bottomFadingEdgeStrength)
        )

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startY = size.height - topFadingEdgeStrength,
                endY = size.height,
            ),
            topLeft = Offset(x = 0f, y = size.height - topFadingEdgeStrength)
        )
    }
}

fun Modifier.customHeight(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchLapListPreview() {
    WhakaaraTheme {
        StopwatchLapList(
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
