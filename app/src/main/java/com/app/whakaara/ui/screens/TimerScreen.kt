package com.app.whakaara.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun TimerScreen() {
    Text(text = "Timer Screen")
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerPreview() {
    WhakaaraTheme {
        TimerScreen()
    }
}
