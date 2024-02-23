package com.app.whakaara.ui.floatingactionbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.ui.theme.BooleanPreviewProvider
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.Spacings.spaceXxLarge
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun FloatingActionButtonPlayPauseStop(
    isPlaying: Boolean,
    isStart: Boolean,
    onStop: () -> Unit,
    onPause: () -> Unit,
    onStart: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceXxLarge)
    ) {
        if (!isStart) {
            FloatingActionButtonStop(onStop)
        }
        FloatingActionButtonPlayPause(isPlaying, onPause, onStart)
    }
}

@Composable
fun FloatingActionButtonStop(onStop: () -> Unit) {
    FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
        onClick = onStop
    ) {
        Icon(
            imageVector = Icons.Filled.Stop,
            contentDescription = stringResource(id = R.string.stop_timer_icon_content_description)
        )
    }
}

@Composable
private fun FloatingActionButtonPlayPause(
    isPlaying: Boolean,
    onPause: () -> Unit,
    onStart: () -> Unit
) {
    FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
        onClick = {
            if (isPlaying) {
                onPause()
            } else {
                onStart()
            }
        }
    ) {
        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(id = R.string.pause_timer_icon_content_description)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.start_timer_icon_content_description)
            )
        }
    }
}

@Preview
@Composable
fun FloatingActionButtonPlayPauseStopPreview(
    @PreviewParameter(BooleanPreviewProvider::class) isPlaying: Boolean
) {
    WhakaaraTheme {
        FloatingActionButtonPlayPauseStop(
            isPlaying = isPlaying,
            isStart = false,
            onStop = {},
            onPause = {},
            onStart = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonStopPreview() {
    WhakaaraTheme {
        FloatingActionButtonStop(
            onStop = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonPlayPausePreview(
    @PreviewParameter(BooleanPreviewProvider::class) isPlaying: Boolean
) {
    WhakaaraTheme {
        FloatingActionButtonPlayPause(
            isPlaying = isPlaying,
            onStart = {},
            onPause = {}
        )
    }
}
