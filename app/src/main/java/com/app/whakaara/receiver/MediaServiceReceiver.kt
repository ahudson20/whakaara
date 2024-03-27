package com.app.whakaara.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.logic.AlarmManagerWrapper
import com.app.whakaara.logic.TimerManagerWrapper
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.MEDIA_SERVICE_RECEIVER_EXCEPTION_TAG
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class MediaServiceReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var alarmManagerWrapper: AlarmManagerWrapper

    @Inject
    lateinit var timerManagerWrapper: TimerManagerWrapper

    override fun onReceive(context: Context, intent: Intent) {
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

// https://github.com/androidx/androidx/blob/a00488668925d695a6ae0d6168d33fdd619c0b31/glance/glance-appwidget/src/main/java/androidx/glance/appwidget/CoroutineBroadcastReceiver.kt#L35
fun BroadcastReceiver.goAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit
) {
    val coroutineScope = CoroutineScope(SupervisorJob() + coroutineContext)
    val pendingResult = goAsync()

    coroutineScope.launch {
        try {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                Log.e("goAsyncTAG", "BroadcastReceiver execution failed", t)
            } finally {
                // Nothing can be in the `finally` block after this, as this throws a
                // `CancellationException`
                coroutineScope.cancel()
            }
        } finally {
            // This must be the last call, as the process may be killed after calling this.
            try {
                pendingResult.finish()
            } catch (e: IllegalStateException) {
                // On some OEM devices, this may throw an error about "Broadcast already finished".
                // See b/257513022.
                Log.e("goAsyncTAG", "Error thrown when trying to finish broadcast", e)
            }
        }
    }
}
