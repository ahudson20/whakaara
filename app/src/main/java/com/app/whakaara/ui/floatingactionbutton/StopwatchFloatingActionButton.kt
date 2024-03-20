package com.app.whakaara.ui.floatingactionbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
            FloatingActionButtonStop(onStop = onStop)
        }
        FloatingActionButtonPlayPause(
            isPlaying = isPlaying,
            onPause = onPause,
            onStart = onStart
        )
    }
}

@Composable
fun FloatingActionButtonStop(onStop: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        modifier = Modifier.testTag("floating action button stop"),
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
        containerColor = MaterialTheme.colorScheme.error,
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
        modifier = Modifier.testTag("floating action button play-pause"),
        shape = CircleShape,
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
