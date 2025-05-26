package com.whakaara.feature.stopwatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.whakaara.core.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space20
import com.whakaara.core.designsystem.theme.Spacings.space275
import com.whakaara.core.designsystem.theme.Spacings.spaceSmall
import com.whakaara.core.designsystem.theme.Spacings.spaceXSmall
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.darkGreen
import com.whakaara.core.designsystem.theme.lightGreen

@Composable
fun StopwatchDisplay(
    modifier: Modifier = Modifier,
    formattedTime: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(space275)
                .clip(CircleShape)
                .background(darkGreen)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(space275 - space20)
                    .border(width = spaceXSmall, color = lightGreen, shape = CircleShape)
                    .clip(CircleShape)
                    .background(color = Color.Transparent)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = spaceSmall)
            ) {
                Text(
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 54.sp,
                        color = Color.White
                    ),
                    text = if (formattedTime.substring(0, 2) == TIMER_INPUT_INITIAL_VALUE) {
                        formattedTime.substring(3, 8)
                    } else {
                        formattedTime.substring(0, 8)
                    }
                )
                Text(
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    text = formattedTime.split(":")[3]
                )
            }
        }
    }
}

@Composable
fun StopwatchDisplayLandscape(
    modifier: Modifier = Modifier,
    formattedTime: String,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
) {
    val showFancyDisplay = windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED

    if (showFancyDisplay) {
        StopwatchDisplay(modifier = modifier, formattedTime = formattedTime)
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 54.sp,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                text = if (formattedTime.substring(0, 2) == TIMER_INPUT_INITIAL_VALUE) {
                    formattedTime.substring(3, 8)
                } else {
                    formattedTime.substring(0, 8)
                }
            )
            Text(
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                text = formattedTime.split(":")[3]
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchDisplayLandscapePreview() {
    StopwatchDisplayLandscape(
        formattedTime = "10:10:00:000"
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchDisplayPreview() {
    StopwatchDisplay(
        formattedTime = "10:10:00:000"
    )
}
