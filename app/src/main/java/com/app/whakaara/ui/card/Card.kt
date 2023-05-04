package com.app.whakaara.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.whakaara.data.Alarm
import com.app.whakaara.ui.bottomsheet.BottomSheetWrapper
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch

@Composable
fun Card(
    alarm: Alarm,
    cancel: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit
) {
    val valueSlider by remember(alarm.isEnabled) { mutableStateOf(alarm.isEnabled) }
    val scope = rememberCoroutineScope()
    val simpleSheetState = rememberBottomSheetState()
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.0.dp))
            .clickable {
                scope.launch { simpleSheetState.expand() }
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
                    text = (alarm.hour.toString() + ":" + alarm.minute.toString()),
                )
                Text(
                    text = alarm.title ?: "",
                )
            }
            Spacer(Modifier.weight(1f))
            Switch(
                checked = valueSlider,
                onCheckedChange = {
                    if (!it) {
                        cancel(alarm)
                    } else {
                        enable(alarm)
                    }
                }
            )
        }
    }

    BottomSheetWrapper(
        state = simpleSheetState,
        alarm = alarm
    )
}

@Preview(showBackground = true)
@Composable
fun CardContainerSwipeToDismissPreview() {
    CardContainerSwipeToDismiss(
        viewModel = hiltViewModel(),
    )
}