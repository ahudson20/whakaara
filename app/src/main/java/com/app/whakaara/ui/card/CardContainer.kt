package com.app.whakaara.ui.card

import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardContainerSwipeToDismiss(
    alarms: AlarmState,
    preferencesState: PreferencesState,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    val context = LocalContext.current
    LazyColumn {
        items(alarms.alarms, key = { it.alarmId }) { alarm ->
            val dismissState = rememberDismissState(
                positionalThreshold = { distance: Float ->
                    distance * 0.1f
                }
            )

            if (dismissState.currentValue != DismissValue.Default) {
                LaunchedEffect(Unit) {
                    Toast.makeText(context, context.getString(R.string.notification_action_deleted, alarm.title), Toast.LENGTH_LONG).show()
                    delete(alarm)
                    delay(25)
                    dismissState.reset()
                }
            }

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(DismissDirection.EndToStart),
                background = {
                    DismissBackground(dismissState)
                },
                dismissContent = {
                    Card(
                        alarm = alarm,
                        preferencesState = preferencesState,
                        disable = disable,
                        enable = enable,
                        reset = reset
                    )
                }
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun CardContainerSwipeToDismissPreview() {
    WhakaaraTheme {
        CardContainerSwipeToDismiss(
            alarms = AlarmState(
                listOf(
                    Alarm(
                        date = Calendar.getInstance(),
                        subTitle = "12:13 AM"
                    )
                )
            ),
            preferencesState = PreferencesState(),
            delete = {},
            disable = {},
            enable = {},
            reset = {}
        )
    }
}
