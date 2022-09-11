@file:OptIn(ExperimentalMaterialApi::class)

package com.app.whakaara.ui.bottomsheet

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun BottomSheet(
    sheetState: ModalBottomSheetState,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler(sheetState.isVisible) {
        coroutineScope.launch {
            sheetState.hide()
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
                text = AnnotatedString("Close"),
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
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
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    BottomSheet(rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden))
}