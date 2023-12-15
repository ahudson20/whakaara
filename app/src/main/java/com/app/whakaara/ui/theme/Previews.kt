package com.app.whakaara.ui.theme

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Dark Mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
annotation class ThemePreviews

@Preview(name = "Default Font Size", showBackground = true, fontScale = 1f)
@Preview(name = "Large Font Size", showBackground = true, fontScale = 1.5f)
annotation class FontScalePreviews
