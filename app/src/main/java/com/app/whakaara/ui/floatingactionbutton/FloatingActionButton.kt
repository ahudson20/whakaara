package com.app.whakaara.ui.floatingactionbutton

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.app.whakaara.utils.NotificationUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun FloatingButton(
    isDialogShown: MutableState<Boolean>,
    scaffoldState: ScaffoldState,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val context = LocalContext.current.applicationContext
    val snackbarHostState = scaffoldState.snackbarHostState
    val notificationPermissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    val scope = rememberCoroutineScope()
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
                        NotificationUtils(context).snackBarPromptPermission(
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
            Icons.Default.Add,
            contentDescription = "Add Alarm",
        )
    }
}