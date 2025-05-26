package com.whakaara.feature.stopwatch.ui

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
import androidx.compose.ui.text.style.TextAlign
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.Spacings.spaceXLarge
import com.whakaara.core.designsystem.theme.Spacings.spaceXSmall
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.stopwatch.R

@Composable
fun StopwatchHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(start = spaceMedium, end = spaceMedium)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.stopwatch_header_lap),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .weight(3f)
                    .padding(start = spaceXLarge, end = spaceMedium),
                text = stringResource(R.string.stopwatch_header_total_time),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(2f),
                text = stringResource(R.string.stopwatch_header_lap_times),
                textAlign = TextAlign.Center
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = spaceXSmall)
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
