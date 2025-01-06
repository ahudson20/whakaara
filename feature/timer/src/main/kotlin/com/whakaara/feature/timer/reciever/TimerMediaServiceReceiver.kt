package com.whakaara.feature.timer.reciever

import android.content.Context
import android.content.Intent
import android.util.Log
import com.whakaara.core.HiltBroadcastReceiver
import com.whakaara.core.LogUtils.logD
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.goAsync
import com.whakaara.data.timer.TimerRepository
import com.whakaara.feature.timer.service.TimerMediaService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerMediaServiceReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var repository: TimerRepository

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        super.onReceive(context, intent)
        goAsync {
            try {
                repository.stopTimer()
                context.stopService(Intent(context, TimerMediaService::class.java))
            } catch (exception: Exception) {
                logD(message = "Failed to reset timer, stop service", throwable = exception)
            }
        }
    }
}
