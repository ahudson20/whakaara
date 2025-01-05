package com.whakaara.core.designsystem.theme

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.whakaara.model.alarm.Alarm
import java.util.Calendar
import java.util.UUID

@Preview(name = "Dark Mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
annotation class ThemePreviews

@Preview(name = "Default Font Size", showBackground = true, fontScale = 1f)
@Preview(name = "Large Font Size", showBackground = true, fontScale = 1.5f)
annotation class FontScalePreviews

class BooleanPreviewProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}

class RoutePreviewProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf("alarm", "timer")
}

class AlarmPreviewProvider : PreviewParameterProvider<Alarm> {
    override val values = sequenceOf(
        Alarm(
            alarmId = UUID.fromString("alarmId"),
            date = Calendar.getInstance(),
            title = "title",
            subTitle = "10:03 AM",
            vibration = true,
            isEnabled = true,
            isSnoozeEnabled = true,
            deleteAfterGoesOff = false,
            repeatDaily = false,
            daysOfWeek = mutableListOf()
        )
    )
}
