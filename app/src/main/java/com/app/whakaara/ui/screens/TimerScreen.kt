package com.app.whakaara.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonPauseStop
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonStart

@Composable
fun TimerScreen(
    viewModel: MainViewModel,
) {
    Timer(
        isPlaying = viewModel.isActive,
        isStart = viewModel.isStart,
        formattedTime = viewModel.formattedTime,
        onStart = viewModel::start,
        onPause = viewModel::pause,
        onStop = viewModel::reset
    )
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    isStart: Boolean,
    isPlaying: Boolean,
    formattedTime: String,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    Scaffold(
        /**
         * I don't like the animations on the FAB shadow.
         * Just doing a basic show/hide for now.
         * https://issuetracker.google.com/issues/224005027
         * */
        floatingActionButton = {
            if (isStart) {
                FloatingActionButtonStart(
                    onStart = onStart
                )
            } else {
                FloatingActionButtonPauseStop(
                    isPlaying = isPlaying,
                    onStop = onStop,
                    onPause = onPause,
                    onStart = onStart
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge
            )
        }
    }
}