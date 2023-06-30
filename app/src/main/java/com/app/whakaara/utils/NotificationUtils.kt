package com.app.whakaara.utils

import android.app.Notification.CATEGORY_ALARM
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color.WHITE
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.utils.constants.NotificationUtilsConstants.CHANNEL_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.CHANNEL_NAME
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_PACKAGE
import com.google.android.material.R.drawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// TODO: could provide the notification builder/manager/channel via hilt
class NotificationUtils(context: Context) : ContextWrapper(context) {

    private var manager: NotificationManager? = null

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            setBypassDnd(true)
            lockscreenVisibility = VISIBILITY_PUBLIC
            setShowBadge(true)
            // setVibrationPattern
        }

        getManager().createNotificationChannel(channel)
    }

    fun getManager(): NotificationManager {
        if (manager == null) {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager as NotificationManager
    }

    fun getChannel(): NotificationChannel {
        return getManager().getNotificationChannel(CHANNEL_ID)
    }

    fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(drawable.ic_clock_black_24dp)
            .setColor(WHITE)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setAutoCancel(true)
            .setOngoing(true)
            .setCategory(CATEGORY_ALARM)
    }

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
}
