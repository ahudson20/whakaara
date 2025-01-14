package com.whakaara.feature.alarm.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space20
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.R

@Composable
fun DismissBackground(
    modifier: Modifier = Modifier,
    dismissState: SwipeToDismissBoxState
) {
    val isSwiping by remember(dismissState) {
        derivedStateOf { dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart && dismissState.progress > 0.1f }
    }

    val color by animateColorAsState(
        when {
            isSwiping -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.background
        },
        label = "color"
    )

    val scale by animateFloatAsState(
        if (isSwiping) 1f else 0.001f,
        label = "scale"
    )

    Box(
        modifier = modifier
            .clip(shape = com.whakaara.core.designsystem.theme.Shapes.extraLarge)
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = stringResource(id = R.string.delete_icon_content_description),
            modifier = Modifier
                .scale(scale)
                .padding(horizontal = space20)
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun DismissBackgroundPreview() {
    WhakaaraTheme {
        DismissBackground(
            dismissState = rememberSwipeToDismissBoxState()
        )
    }
}
