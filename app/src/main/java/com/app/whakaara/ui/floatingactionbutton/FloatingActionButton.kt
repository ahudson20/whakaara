package com.app.whakaara.ui.floatingactionbutton

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.whakaara.R
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.NotificationUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun FloatingActionButton(
    isDialogShown: MutableState<Boolean>,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val notificationPermissionState = rememberPermissionStateSafe(permission = Manifest.permission.POST_NOTIFICATIONS)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    FloatingActionButton(
        onClick = {
            /**PERMISSION GRANTED**/
            when (notificationPermissionState.status) {
                PermissionStatus.Granted -> {
                    isDialogShown.value = !isDialogShown.value
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
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(id = R.string.floating_action_button_icon_description)
        )
    }
}

@Preview
@Composable
fun FloatingButtonPreview() {
    WhakaaraTheme {
        FloatingActionButton(
            isDialogShown = rememberSaveable { mutableStateOf(false) },
            launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}
        )
    }
}
