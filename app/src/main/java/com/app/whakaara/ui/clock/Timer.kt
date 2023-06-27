package com.app.whakaara.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonPause
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonStart
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButtonStop
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun Timer(
    modifier: Modifier = Modifier,
    formattedTime: String,
    onStart: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
) {
    Scaffold(
        /**
         * I wanted to use a single FAB for play/pause, and show/hide another for reset.
         * But I don't like the animations on the FAB shadow, when show/hide the buttons.
         * https://issuetracker.google.com/issues/224005027
         * */
        /**
         * I wanted to use a single FAB for play/pause, and show/hide another for reset.
         * But I don't like the animations on the FAB shadow, when show/hide the buttons.
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
    ) { innerPadding ->
        Column(
            modifier.fillMaxSize().padding(innerPadding),
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

@Preview(showBackground = true)
@Composable
fun TimerPreview() {
    WhakaaraTheme {
        Timer(
            formattedTime = "01:01:01"
        )
    }
}