package com.whakaara.feature.alarm.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.spaceLarge
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.HoursUpdateEvent

@Composable
fun BottomSheetTimePicker(
    modifier: Modifier = Modifier,
    updatePickerValue: HoursUpdateEvent
) {
    HoursNumberPicker(
        modifier = modifier.padding(all = spaceLarge),
        dividersColor = MaterialTheme.colorScheme.onSurface,
        leadingZero = false,
        value = updatePickerValue.value,
        onValueChange = {
            updatePickerValue.onValueChange(it)
        },
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp
        )
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun BottomSheetTimePickerPreview() {
    WhakaaraTheme {
        BottomSheetTimePicker(
            updatePickerValue = HoursUpdateEvent(
                value = FullHours(
                    hours = 12,
                    minutes = 12
                )
            )
        )
    }
}
