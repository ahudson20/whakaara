package com.app.whakaara.ui.floatingactionbutton

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.ui.theme.BooleanPreviewProvider
import com.app.whakaara.ui.theme.Spacings.spaceNone
import com.app.whakaara.ui.theme.Spacings.spaceXxLarge
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.NotificationUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun FloatingActionButtonPlayPauseStop(
    isPlaying: Boolean,
    isStart: Boolean,
    askForPermissions: Boolean,
    onStop: () -> Unit,
    onPause: () -> Unit,
    onStart: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceXxLarge)
    ) {
        if (!isStart) {
            FloatingActionButtonStop(onStop = onStop)
        }
        FloatingActionButtonPlayPause(
            isPlaying = isPlaying,
            askForPermissions = askForPermissions,
            onPause = onPause,
            onStart = onStart
        )
    }
}

@Composable
fun FloatingActionButtonStop(onStop: () -> Unit) {
    FloatingActionButton(
        shape = CircleShape,
        modifier = Modifier.testTag("floating action button stop"),
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
        onClick = onStop
    ) {
        Icon(
            imageVector = Icons.Filled.Stop,
            contentDescription = stringResource(id = R.string.stop_timer_icon_content_description)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun FloatingActionButtonPlayPause(
    isPlaying: Boolean,
    askForPermissions: Boolean,
    onPause: () -> Unit,
    onStart: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val notificationPermissionState = rememberPermissionStateSafe(
        permission = Manifest.permission.POST_NOTIFICATIONS
    )
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            onStart()
        }
    }

    FloatingActionButton(
        modifier = Modifier.testTag("floating action button play-pause"),
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(pressedElevation = spaceNone),
        onClick = {
            if (isPlaying) {
                onPause()
            } else {
                if (askForPermissions) {
                    when (notificationPermissionState.status) {
                        PermissionStatus.Granted -> {
                            onStart()
                        }
                        else -> {
                            /**PERMISSION DENIED - SHOW PROMPT**/
                            if (notificationPermissionState.status.shouldShowRationale) {
                                NotificationUtils.snackBarPromptPermission(
                                    scope = scope,
                                    snackBarHostState = snackbarHostState,
                                    context = context
                                )
                            } else {
                                /**FIRST TIME ACCESSING**/
                                /**OR USER DOESN'T WANT TO BE ASKED AGAIN**/
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                } else {
                    onStart()
                }
            }
        }
    ) {
        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.Pause,
                contentDescription = stringResource(id = R.string.pause_timer_icon_content_description)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(id = R.string.start_timer_icon_content_description)
            )
        }
    }
}

@Preview
@Composable
fun FloatingActionButtonPlayPauseStopPreview(
    @PreviewParameter(BooleanPreviewProvider::class) isPlaying: Boolean
) {
    WhakaaraTheme {
        FloatingActionButtonPlayPauseStop(
            isPlaying = isPlaying,
            isStart = false,
            askForPermissions = false,
            onStop = {},
            onPause = {},
            onStart = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonStopPreview() {
    WhakaaraTheme {
        FloatingActionButtonStop(
            onStop = {}
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonPlayPausePreview(
    @PreviewParameter(BooleanPreviewProvider::class) isPlaying: Boolean
) {
    WhakaaraTheme {
        FloatingActionButtonPlayPause(
            isPlaying = isPlaying,
            askForPermissions = false,
            onStart = {},
            onPause = {}
        )
    }
}
