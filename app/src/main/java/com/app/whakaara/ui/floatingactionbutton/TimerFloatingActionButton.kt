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
import androidx.compose.ui.unit.dp
import com.app.whakaara.R

@Composable
fun FloatingActionButtonStart(
    onStart: () -> Unit
) {
    FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = 0.dp),
        onClick = onStart
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = stringResource(id = R.string.start_timer_icon_content_description)
        )
    }
}

@Composable
fun FloatingActionButtonPauseStop(
    isPlaying: Boolean,
    onStop: () -> Unit,
    onPause: () -> Unit,
    onStart: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        FloatingActionButtonStop(onStop)
        FloatingActionButtonPlayPause(isPlaying, onPause, onStart)
    }
}

@Composable
fun FloatingActionButtonStop(onStop: () -> Unit) {
    FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = 0.dp),
        onClick = onStop
    ) {
        Icon(
            imageVector = Icons.Filled.Stop,
            contentDescription = stringResource(id = R.string.stop_timer_icon_content_description)
        )
    }
}

@Composable
fun FloatingActionButtonPause(onPause: () -> Unit) {
    FloatingActionButton(
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = 0.dp),
        onClick = onPause
    ) {
        Icon(
            imageVector = Icons.Filled.Pause,
            contentDescription = stringResource(id = R.string.pause_timer_icon_content_description)
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
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = 0.dp),
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
