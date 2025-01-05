package com.whakaara.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.whakaara.core.designsystem.R

private val mediumFontFamily =
    FontFamily(
        Font(R.font.azeret_mono_medium)
    )

private val regularFontFamily =
    FontFamily(
        Font(R.font.azeret_mono_regular)
    )

private val defaultTypography = Typography()
val Typography =
    Typography(
        displayLarge = defaultTypography.displayLarge.copy(fontFamily = regularFontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = regularFontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = regularFontFamily),
        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = regularFontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = regularFontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = regularFontFamily),
        titleLarge = defaultTypography.titleLarge.copy(fontFamily = regularFontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = mediumFontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = mediumFontFamily),
        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = regularFontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = regularFontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = regularFontFamily),
        labelLarge = defaultTypography.labelLarge.copy(fontFamily = mediumFontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = mediumFontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = mediumFontFamily)
    )
