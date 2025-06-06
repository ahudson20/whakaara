package com.app.whakaara.ui.clock

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.app.whakaara.R
import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.preferences.TimeFormat

@Composable
fun TextClock(timeFormat: TimeFormat) {
    val textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    AndroidView(
        factory = { context ->
            android.widget.TextClock(context).apply {
                if (timeFormat == TimeFormat.TWENTY_FOUR_HOURS) {
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
                typeface = ResourcesCompat.getFont(context, R.font.azeret_mono_medium)
                isAllCaps = true
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
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS
        )
    }
}
