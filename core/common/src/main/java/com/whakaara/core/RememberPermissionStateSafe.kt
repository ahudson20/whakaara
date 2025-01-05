package com.whakaara.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@ExperimentalPermissionsApi
@Composable
fun rememberPermissionStateSafe(
    permission: String,
    onPermissionResult: (Boolean) -> Unit = {}
) = when {
    LocalInspectionMode.current ->
        remember {
            object : PermissionState {
                override val permission = permission
                override val status = PermissionStatus.Granted

                override fun launchPermissionRequest() = Unit
            }
        }
    else -> rememberPermissionState(permission, onPermissionResult)
}
