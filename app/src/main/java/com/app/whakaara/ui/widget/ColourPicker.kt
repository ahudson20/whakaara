package com.app.whakaara.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space200
import com.app.whakaara.ui.theme.Spacings.space5
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun ColourPicker(
    alpha: MutableState<Float>,
    red: MutableState<Float>,
    green: MutableState<Float>,
    blue: MutableState<Float>,
    color: Color
) {
    Column {
        Row {
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(50.dp)
                    .height(space200)
                    .background(color, shape = MaterialTheme.shapes.large)
            )
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(space5)
            ) {
                ColourSlider("A", alpha, color.copy(1f))
                ColourSlider("R", red, Color.Red)
                ColourSlider("G", green, Color.Green)
                ColourSlider("B", blue, Color.Blue)
            }
        }
    }
}

@ThemePreviews
@FontScalePreviews
@Composable
fun PreviewColourPicker() {
    val alphaBackground = rememberSaveable { mutableFloatStateOf(1f) }
    val redBackground = rememberSaveable { mutableFloatStateOf(1f) }
    val greenBackground = rememberSaveable { mutableFloatStateOf(0f) }
    val blueBackground = rememberSaveable { mutableFloatStateOf(0f) }
    val colorBackground by remember {
        derivedStateOf {
            Color(
                redBackground.floatValue,
                greenBackground.floatValue,
                blueBackground.floatValue,
                alphaBackground.floatValue
            )
        }
    }

    WhakaaraTheme {
        ColourPicker(
            alpha = alphaBackground,
            red = redBackground,
            green = greenBackground,
            blue = blueBackground,
            color = colorBackground
        )
    }
}
