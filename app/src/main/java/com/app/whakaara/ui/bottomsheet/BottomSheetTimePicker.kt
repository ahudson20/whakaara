package com.app.whakaara.ui.bottomsheet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        modifier = modifier.padding(bottom = 24.dp),
        dividersColor = MaterialTheme.colorScheme.onSurface,
        leadingZero = false,
        value = pickerValue,
        onValueChange = {
            updatePickerValue(it)
        },
        textStyle = LocalTextStyle.current.copy(MaterialTheme.colorScheme.onSurface)
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
