package com.app.whakaara.receiver

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.logic.AlarmManagerWrapper
import com.app.whakaara.logic.TimerManagerWrapper
import com.app.whakaara.service.MediaPlayerService
import com.whakaara.core.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.whakaara.core.constants.NotificationUtilsConstants.MEDIA_SERVICE_RECEIVER_EXCEPTION_TAG
import com.whakaara.core.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.whakaara.core.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.whakaara.core.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaServiceReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var alarmManagerWrapper: AlarmManagerWrapper

    @Inject
    lateinit var timerManagerWrapper: TimerManagerWrapper

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        val alarmId = intent.getStringExtra(INTENT_ALARM_ID) ?: ""
        val alarmType = intent.getIntExtra(NOTIFICATION_TYPE, -1)
        if (alarmType == -1) {
            return
        }
        goAsync {
            try {
                if (alarmType == NOTIFICATION_TYPE_ALARM) {
                    alarmManagerWrapper.deleteAlarm(alarmId = alarmId)
                }
                if (alarmType == NOTIFICATION_TYPE_TIMER) {
                    timerManagerWrapper.cancelTimerAlarm()
                }
                context.stopService(Intent(context, MediaPlayerService::class.java))
            } catch (exception: Exception) {
                Log.d(MEDIA_SERVICE_RECEIVER_EXCEPTION_TAG, exception.printStackTrace().toString())
            }
        }
    }
}
