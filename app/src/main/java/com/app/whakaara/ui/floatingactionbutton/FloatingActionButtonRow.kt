package com.app.whakaara.ui.floatingactionbutton

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.app.whakaara.R
import com.app.whakaara.ui.theme.BooleanPreviewProvider
import com.app.whakaara.ui.theme.Shapes
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun FloatingActionButtonRow(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isStart: Boolean,
    isPlayButtonVisible: Boolean = true,
    onStop: () -> Unit,
    onPlayPause: () -> Unit,
    onExtraButtonClicked: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(shape = Shapes.large),
            horizontalArrangement = Arrangement.End,
        ) {
            AnimatedVisibility(
                enter =
                    fadeIn() +
                        expandHorizontally(
                            expandFrom = Alignment.Start,
                        ),
                exit =
                    fadeOut() +
                        shrinkHorizontally(
                            shrinkTowards = Alignment.Start,
                        ),
                visible = !isStart,
            ) {
                FloatingActionButtonStop(
                    onStop = onStop,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(
                visible = isPlayButtonVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButtonPlayPause(
                    isPlaying = isPlaying,
                    onClick = onPlayPause,
                )
            }
        }
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(shape = Shapes.large),
            horizontalArrangement = Arrangement.Start,
        ) {
            AnimatedVisibility(isPlaying) {
                FloatingActionButtonExtraAction(
                    onExtraButtonClicked = onExtraButtonClicked,
                )
            }
        }
    }
}

@Composable
private fun FloatingActionButtonStop(onStop: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier.testTag("floating action button stop"),
        shape = CircleShape,
        elevation =
            FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
            ),
        containerColor = MaterialTheme.colorScheme.error,
        onClick = onStop,
    ) {
        Icon(
            imageVector = Icons.Filled.Stop,
            contentDescription = stringResource(id = R.string.stop_timer_icon_content_description),
        )
    }
}

@Composable
private fun FloatingActionButtonPlayPause(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        modifier = modifier.testTag("floating action button play-pause"),
        shape = CircleShape,
        elevation =
            FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
            ),
        onClick = onClick,
    ) {
        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(id = R.string.pause_timer_icon_content_description),
            )
        } else {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.start_timer_icon_content_description),
            )
        }
    }
}

@Composable
private fun FloatingActionButtonExtraAction(onExtraButtonClicked: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        elevation =
            FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
            ),
        containerColor = MaterialTheme.colorScheme.error,
        onClick = {
            onExtraButtonClicked()
        },
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = stringResource(id = R.string.lap_reset_icon_content_description),
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonStopPreview() {
    WhakaaraTheme {
        FloatingActionButtonStop(
            onStop = {},
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonPlayPausePreview(
    @PreviewParameter(BooleanPreviewProvider::class) isPlaying: Boolean,
) {
    WhakaaraTheme {
        FloatingActionButtonPlayPause(
            isPlaying = isPlaying,
            onClick = {},
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonExtraActionPreview() {
    WhakaaraTheme {
        FloatingActionButtonExtraAction(
            onExtraButtonClicked = {},
        )
    }
}
