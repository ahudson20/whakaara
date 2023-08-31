package com.app.whakaara.utils.constants

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

object NotificationUtilsConstants {
    const val CHANNEL_ID = "channel_id"
    const val CHANNEL_NAME = "channel_name"

    const val INTENT_EXTRA_ALARM = "alarm"
    const val INTENT_EXTRA_ACTION_ARBITRARY = "arbitrary"
    const val INTENT_REQUEST_CODE = 0
    const val INTENT_PACKAGE = "package"
    const val INTENT_AUTO_SILENCE = "silence"
    const val INTENT_TIME_FORMAT = "format"

    @RequiresApi(Build.VERSION_CODES.S)
    val INTENT_SCHEDULE_ALARM_PERMISSION = Intent().apply { action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM }
    private val INTENT_APP_SETTINGS = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
    val INTENT_DATE_SETTINGS = Intent(Settings.ACTION_DATE_SETTINGS).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

    const val ALARM_TITLE_MAX_CHARS = 20
    const val ALARM_SOUND_TIMEOUT_DEFAULT_MILLIS = (10 * 60000).toLong()
    const val ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES = 10

    fun getAppSettingsIntent(packageName: String) = INTENT_APP_SETTINGS.apply {
        data = Uri.fromParts(INTENT_PACKAGE, packageName, null)
    }
}
