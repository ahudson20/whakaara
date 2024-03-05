package com.app.whakaara.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import com.app.whakaara.R
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_PACKAGE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.SERVICE_ACTION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotificationUtils {
    companion object {
        fun snackBarPromptPermission(
            scope: CoroutineScope,
            snackBarHostState: SnackbarHostState,
            context: Context
        ) {
            scope.launch {
                val result = snackBarHostState.showSnackbar(
                    message = context.getString(R.string.permission_prompt_message),
                    actionLabel = context.getString(R.string.permission_prompt_action_label),
                    duration = SnackbarDuration.Long
                )
                /**SNACKBAR PROMPT ACCEPTED**/
                if (snackBarHasBeenClicked(result)) {
                    openDeviceApplicationSettings(context)
                }
            }
        }

        private fun openDeviceApplicationSettings(context: Context) {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts(INTENT_PACKAGE, context.packageName, null)
            }
            context.startActivity(intent)
        }

        private fun snackBarHasBeenClicked(result: SnackbarResult) =
            result == SnackbarResult.ActionPerformed

        fun Context.startMediaService(autoSilenceTime: Int, action: Int, type: Int, alarmId: String? = null) {
            Intent(this, MediaPlayerService::class.java).apply {
                putExtra(INTENT_AUTO_SILENCE, autoSilenceTime)
                putExtra(SERVICE_ACTION, action)
                putExtra(NOTIFICATION_TYPE, type)
                putExtra(INTENT_ALARM_ID, alarmId)
            }.also { mediaIntent ->
                this.startService(mediaIntent)
            }
        }
    }
}
