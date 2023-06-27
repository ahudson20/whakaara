package com.app.whakaara.ui.card

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.app.whakaara.data.Alarm
import com.app.whakaara.state.AlarmState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardContainerSwipeToDismiss(
    alarms: AlarmState,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    LazyColumn {
        items(alarms.alarms, key = { it.alarmId }) { alarm ->

            val dismissState = rememberDismissState()
            if (dismissState.currentValue != DismissValue.Default) {
                LaunchedEffect(Unit) {
                    delete(alarm)
                    delay(25)
                    dismissState.reset()
                }
            }

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                },
                background = {
                    DismissBackground(dismissState)
                },
                dismissContent = {
                    Card(
                        alarm = alarm,
                        cancel = disable,
                        enable = enable,
                        reset = reset
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardContainerSwipeToDismissPreview() {
    CardContainerSwipeToDismiss(
        alarms =  AlarmState(
            listOf(
                Alarm(
                    hour = 12,
                    minute = 13,
                    subTitle = "12:13 AM"
                )
            )
        ),
        delete = {},
        disable = {},
        enable = {},
        reset = {}
    )
}