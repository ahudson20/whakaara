@file:OptIn(ExperimentalMaterialApi::class)

package com.app.whakaara.ui.bottomsheet

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun BottomSheet(sheetState: ModalBottomSheetState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Value for storing time as a string
    val time = remember { mutableStateOf("") }

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch {
            time.value = ""
            sheetState.hide()
        }
    }

    // Declaring and initializing a calendar
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    // Creating a TimePicker dialog
    val timePickerDialog = TimePickerDialog(
        context,
        {_, hour : Int, minute: Int ->
            time.value = String.format("%02d:%02d", hour, minute)//"$hour:$minute"
        },
        hour,
        minute,
        false
    )

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
                text = AnnotatedString("Close"),
                onClick = {
                    coroutineScope.launch {
                        if (sheetState.isVisible) {
                            time.value = ""
                            sheetState.hide()
                        }
                    }
                }
            )
            Text(text = "Add Alarm")
            ClickableText(
                text = AnnotatedString("Save"),
                onClick = {
                    Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Text(
            text = "Bottom sheet",
            style = MaterialTheme.typography.h6
        )

        ClickableText(
            text = AnnotatedString(generateString(time.value)),
            onClick = {
                timePickerDialog.show()
            }
        )
    }
}

private fun generateString(time: String): String {
    if(time == "") {
        return "Set Time"
    }
    return time
}

@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    BottomSheet(rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden))
}