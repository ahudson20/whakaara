@file:OptIn(ExperimentalMaterialApi::class)

package com.app.whakaara.ui.card

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import kotlinx.coroutines.delay
import java.util.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardContainerSwipeToDismiss(
    viewModel: MainViewModel,
) {
    val alarms =  mutableStateOf(viewModel.alarms)
    LazyColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        items(alarms.value, key = {it.alarmId}) { alarm ->

            val dismissState = rememberDismissState()
            if(dismissState.currentValue != DismissValue.Default) {
                LaunchedEffect(Unit) {
                    viewModel.delete(alarm)
                    delay(25)
                    dismissState.reset()
                }
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.padding(vertical = Dp(1f)),
                directions = setOf(DismissDirection.EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                },
                background = {
                    DismissBackground(dismissState)
                },
                dismissContent = {
                    Card(
                        time = (alarm.hour.toString() + ":" + alarm.minute.toString()),
                        day = alarm.title ?: "",
                        alarm = alarm,
                        cancel = viewModel::disable,
                        enable = viewModel::enable
                    )
                }
            )
        }
    }
}

@Composable
private fun DismissBackground(dismissState: DismissState) {
    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.White
            else -> Color.Red
        }
    )
    val alignment = Alignment.CenterEnd
    val icon = Icons.Default.Delete

    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1.25f
    )

    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.0.dp))
            .background(color),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = "Delete Icon",
            modifier = Modifier
                .scale(scale)
                .padding(horizontal = 20.dp)
        )
    }
}

//@Composable
//fun CardContainer(
//    alarms: MutableList<Alarm>
//) {
//    LazyColumn(
//        modifier = Modifier.padding(10.dp)
//    ) {
//        items(alarms) { alarm ->
//            Card(time = (alarm.hour.toString() + ":" + alarm.minute.toString()), day = alarm.title ?: "")
//        }
//    }
//}

@Composable
fun Card(
    time: String,
    day: String,
    alarm: Alarm,
    cancel: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit
) {
    val checkedState = remember { mutableStateOf(true) }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.0.dp))
            .clickable {
                // TODO: open bottom sheet, pre-fill details.
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = time,
                )
                Text(
                    text = day,
                )
            }
            Spacer(Modifier.weight(1f))
            Switch(
                checked = checkedState.value,
                onCheckedChange = {
                    if(!it) {
                        cancel(alarm)
                    } else {
                        enable(alarm)
                    }
                    checkedState.value = it
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardContainerSwipeToDismissPreview() {
    CardContainerSwipeToDismiss(
        viewModel = hiltViewModel(),
    )
}

//@Preview(showBackground = true)
//@Composable
//fun CardContainerPreview() {
//    CardContainer(
//        alarms = mutableListOf(
//            Alarm(hour = 3, minute = 30, title = "hello", vibration = false),
//            Alarm(hour = 13, minute = 35, title = "goodbye", vibration = false)
//        )
//    )
//}