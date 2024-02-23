package com.app.whakaara.ui.clock

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.space250
import com.app.whakaara.ui.theme.Spacings.spaceXSmall
import com.app.whakaara.ui.theme.Spacings.spaceXxSmall
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.darkGreen
import com.app.whakaara.ui.theme.lightGreen

@Composable
fun StopwatchDisplay(
    formattedTime: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Box(
            modifier = Modifier
                .size(space250)
                .clip(CircleShape)
                .background(darkGreen)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(space250 - space10)
                    .border(width = spaceXxSmall, color = lightGreen, shape = CircleShape)
                    .clip(CircleShape)
                    .background(color = Color.Transparent)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = spaceXSmall)
            ) {
                Text(
                    style = MaterialTheme.typography.headlineLarge,
                    text = formattedTime.substring(0, formattedTime.indexOf(":", formattedTime.indexOf(":") + 1))
                )
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    text = formattedTime.split(":")[2]
                )
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun StopwatchDisplayPreview() {
    StopwatchDisplay(
        formattedTime = "00:00:000"
    )
}
