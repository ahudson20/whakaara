package com.app.whakaara.ui.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun StopwatchHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // TODO: string resources after modularization
            Text("Lap")
            Text(text = "Total time")
            Text(text = "Lap times")
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchHeaderPreview() {
    WhakaaraTheme {
        StopwatchHeader()
    }
}
