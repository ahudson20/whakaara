package com.app.whakaara.ui.clock

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.app.whakaara.utils.constants.DateUtilsConstants

@Composable
fun TextClock() {
    AndroidView(
        factory = { context ->
            android.widget.TextClock(context).apply {
                format24Hour?.let {
                    this.format24Hour = DateUtilsConstants.DATE_FORMAT_24_HOUR_WITH_SECONDS
                }
                timeZone?.let { this.timeZone = it }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TextClockPreview() {
    TextClock()
}