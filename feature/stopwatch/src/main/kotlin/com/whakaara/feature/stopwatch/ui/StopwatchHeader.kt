package com.whakaara.feature.stopwatch.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.stopwatch.R

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
            Text(text = stringResource(R.string.stopwatch_header_lap))
            Text(text = stringResource(R.string.stopwatch_header_total_time))
            Text(text = stringResource(R.string.stopwatch_header_lap_times))
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
