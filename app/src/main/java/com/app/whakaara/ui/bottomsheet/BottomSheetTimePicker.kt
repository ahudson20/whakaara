package com.app.whakaara.ui.bottomsheet

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker

@Composable
fun BottomSheetTimePicker(
    modifier: Modifier = Modifier,
    pickerValue: Hours,
    updatePickerValue: (Hours) -> Unit
) {
    HoursNumberPicker(
        dividersColor = MaterialTheme.colors.primary,
        leadingZero = false,
        value = pickerValue,
        onValueChange = {
            updatePickerValue(it)
        },
        hoursDivider = {
            Text(
                modifier = modifier.size(24.dp),
                textAlign = TextAlign.Center,
                text = ":"
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTimePickerPreview() {
    BottomSheetTimePicker(
        pickerValue = FullHours(
            hours = 12,
            minutes = 12
        ),
        updatePickerValue = {}
    )
}