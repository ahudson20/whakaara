package com.app.whakaara.ui.card

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.FractionalThreshold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.whakaara.logic.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardContainerSwipeToDismiss(
    viewModel: MainViewModel,
) {
    val alarms by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.padding(10.dp)
    ) {
        items(alarms.alarms, key = { it.alarmId }) { alarm ->

            val dismissState = rememberDismissState()
            if (dismissState.currentValue != DismissValue.Default) {
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
                        alarm = alarm,
                        cancel = viewModel::disable,
                        enable = viewModel::enable
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
        viewModel = hiltViewModel(),
    )
}