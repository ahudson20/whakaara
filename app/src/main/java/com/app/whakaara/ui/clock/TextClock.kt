package com.app.whakaara.ui.clock

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.DateUtilsConstants

@Composable
fun TextClock(
    is24HourFormat: Boolean
) {
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    AndroidView(
        factory = { context ->
            android.widget.TextClock(context).apply {
                if (is24HourFormat) {
                    format24Hour?.let {
                        this.format24Hour = DateUtilsConstants.DATE_FORMAT_24_HOUR_WITH_SECONDS
                    }
                } else {
                    format12Hour?.let {
                        this.format12Hour = DateUtilsConstants.DATE_FORMAT_12_HOUR_WITH_SECONDS
                    }
                }
                timeZone?.let { this.timeZone = it }
                textSize = 45f
                setTextColor(textColor)
            }
        }
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TextClockPreview() {
    WhakaaraTheme {
        TextClock(
            is24HourFormat = true
        )
    }
}
