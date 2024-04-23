package com.app.whakaara.ui.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.whakaara.R
import com.app.whakaara.ui.floatingactionbutton.rememberPermissionStateSafe
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space20
import com.app.whakaara.ui.theme.Spacings.space200
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.ui.theme.lightBlueAnimation
import com.app.whakaara.utils.NotificationUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationsOnboarding(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationPermissionState = rememberPermissionStateSafe(permission = Manifest.permission.POST_NOTIFICATIONS)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission(), onResult = {})

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(all = spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(space200))
        Box {
            AnimatedNotification(
                modifier = Modifier
                    .size(space200)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(25.dp))
                    .background(color = lightBlueAnimation),
                isCompleted = notificationPermissionState.status.isGranted
            )
        }
        Spacer(modifier = Modifier.height(space20))
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_notification_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_sub_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(space20))
        Button(
            enabled = !notificationPermissionState.status.isGranted,
            onClick = {
                if (notificationPermissionState.status.shouldShowRationale) {
                    NotificationUtils.snackBarPromptPermission(
                        scope = scope,
                        snackBarHostState = snackbarHostState,
                        context = context
                    )
                } else {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        ) {
            Text(
                text = if (notificationPermissionState.status.isGranted) {
                    stringResource(id = R.string.onboarding_notification_button_enabled)
                } else {
                    stringResource(id = R.string.onboarding_notification_button_not_enabled)
                }
            )
        }
    }
}

@Composable
fun AnimatedNotification(
    modifier: Modifier = Modifier,
    isCompleted: Boolean
) {
    val clipSpecs = LottieClipSpec.Progress(
        min = 0.0f,
        max = if (isCompleted) 0.975f else 0.45f
    )

    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.notification
        )
    )

    LottieAnimation(
        modifier = modifier,
        composition = preloaderLottieComposition,
        iterations = if (isCompleted) 1 else LottieConstants.IterateForever,
        clipSpec = clipSpecs,
        restartOnPlay = !isCompleted,
        speed = if (isCompleted) 0.5f else 0.4f,
        reverseOnRepeat = !isCompleted
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun NotificationsOnboardingPreview() {
    WhakaaraTheme {
        NotificationsOnboarding(
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
