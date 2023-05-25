package com.app.whakaara.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonPause
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonStart
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonStop

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
         * https://issuetracker.google.com/issues/224005027
         * */
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FloatingActionButtonStop(onStop)
                FloatingActionButtonPause(onPause)
                FloatingActionButtonStart(
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