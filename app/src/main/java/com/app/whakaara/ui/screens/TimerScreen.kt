package com.app.whakaara.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
        isPlaying = viewModel.isPlaying,
        isStart = viewModel.isStart,
        millis = viewModel.millis,
        seconds = viewModel.seconds,
        minutes = viewModel.minutes,
        hours = viewModel.hours,
        onStart = viewModel::start,
        onPause = viewModel::pause,
        onStop = viewModel::stop
    )
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    isStart: Boolean,
    isPlaying: Boolean,
    millis: String,
    seconds: String,
    minutes: String,
    hours: String,
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
            Row {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.displayLarge) {
                    AnimatedContent(
                        targetState = hours,
                        transitionSpec = {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut() using (SizeTransform(
                                clip = false
                            ))
                        }
                    ) {
                        Text(text = it)
                    }

                    Text(text = ":")

                    AnimatedContent(
                        targetState = minutes,
                        transitionSpec = {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut() using (SizeTransform(
                                clip = false
                            ))
                        }
                    ) {
                        Text(text = it)
                    }

                    Text(text = ":")

                    AnimatedContent(
                        targetState = seconds,
                        transitionSpec = {
                            slideInVertically { height -> height } + fadeIn() togetherWith
                                    slideOutVertically { height -> -height } + fadeOut() using (SizeTransform(
                                clip = false
                            ))
                        }
                    ) {
                        Text(text = it)
                    }

                    Text(text = ".")

                    Text(text = millis)
                }
            }
        }
    }
}