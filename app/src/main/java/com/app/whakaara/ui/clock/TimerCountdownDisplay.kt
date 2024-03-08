package com.app.whakaara.ui.clock

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.ui.theme.primaryGreen

@Composable
fun TimerCountdownDisplay(
    progress: Float,
    time: String
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(Spacings.space250),
            progress = { animatedProgress },
            color = primaryGreen,
            strokeWidth = Spacings.spaceXSmall
        )
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = time
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun TimerCountdownDisplayPreview() {
    WhakaaraTheme {
        TimerCountdownDisplay(
            progress = 1.0F,
            time = "00:00:00"
        )
    }
}
