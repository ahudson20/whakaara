package com.app.whakaara.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.whakaara.core.GeneralUtils.Companion.toColorInt
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space5
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme

@Composable
fun ColourSlider(
    label: String,
    valueState: MutableState<Float>,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space5)
    ) {
        Text(text = label)
        Slider(
            value = valueState.value,
            onValueChange = valueState.component2(),
            colors = SliderDefaults.colors(
                activeTrackColor = color
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valueState.value.toColorInt().toString(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@ThemePreviews
@FontScalePreviews
@Composable
fun ColourSliderPreview() {
    val redBackground = rememberSaveable { mutableFloatStateOf(1f) }
    WhakaaraTheme {
        ColourSlider(label = "Text Colour", valueState = redBackground, color = Color.Red)
    }
}
