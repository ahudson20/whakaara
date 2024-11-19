package com.app.whakaara.ui.clock

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space275
import com.whakaara.core.designsystem.theme.Spacings.spaceXLarge
import com.whakaara.core.designsystem.theme.Spacings.spaceXSmall
import com.whakaara.core.designsystem.theme.Spacings.spaceXxSmall
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.core.designsystem.theme.primaryGreen

@Composable
fun TimerCountdownDisplay(
    modifier: Modifier = Modifier,
    progress: Float,
    time: String,
    finishTime: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 50,
            delayMillis = 0,
            easing = LinearEasing
        ),
        label = ""
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(space275),
                progress = { animatedProgress },
                color = com.whakaara.core.designsystem.theme.primaryGreen,
                strokeWidth = spaceXSmall
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    style = MaterialTheme.typography.headlineLarge,
                    text = time
                )
                Row(
                    modifier = Modifier.offset(y = spaceXLarge),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsActive,
                        contentDescription = stringResource(id = R.string.timer_countdown_finish_time_icon_content_description)
                    )
                    Text(
                        modifier = Modifier.padding(start = spaceXxSmall),
                        text = finishTime
                    )
                }
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerCountdownDisplayPreview() {
    WhakaaraTheme {
        TimerCountdownDisplay(
            progress = 1.0F,
            time = "00:00:00",
            finishTime = "10:00 PM"
        )
    }
}
