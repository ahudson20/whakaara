package com.app.whakaara.ui.bottomsheet

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.data.Alarm
import kotlinx.coroutines.launch
import com.dokar.sheets.BottomSheetState
import com.app.whakaara.R

@Composable
fun BottomSheetContent(
    alarm: Alarm,
    sheetState: BottomSheetState //ModalBottomSheetState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler(sheetState.visible) { //.isVisible
        coroutineScope.launch {
            sheetState.collapse() //sheetState.hide()
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.bottom_sheet_close_button)),
                onClick = {
                    coroutineScope.launch {
                        sheetState.collapse() //sheetState.hide()
                    }
                }
            )
            Text(text = alarm.title ?: stringResource(id = R.string.bottom_sheet_title))
            ClickableText(
                text = AnnotatedString(stringResource(id = R.string.bottom_sheet_save_button)),
                onClick = {
                    // TODO: Call to update alarm with any new values, and close bottom sheet.
                    coroutineScope.launch {
                        Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                        sheetState.collapse() //sheetState.hide()
                    }
                }
            )
        }

        Column {
            Text(text = alarm.title ?: "title")

            Text(text = alarm.subTitle ?: "subtitle")

            Text(text = "${alarm.hour}:${alarm.minute}")

            Text(text = "isEnabled: " + alarm.isEnabled.toString())

            Text(text = "isVibration: " + alarm.vibration.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    BottomSheetContent(
        alarm = Alarm(
            minute = 3,
            hour = 10
        ),
        sheetState = BottomSheetState()
    )
}