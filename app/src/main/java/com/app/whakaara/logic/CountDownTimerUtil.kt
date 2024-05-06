package com.app.whakaara.logic

import android.os.CountDownTimer
import com.whakaara.core.constants.GeneralConstants.TIMER_INTERVAL

class CountDownTimerUtil {
    private lateinit var timer: CountDownTimer

    fun countdown(
        period: Long,
        interval: Long = TIMER_INTERVAL,
        onTickAction: (millisUntilFinished: Long) -> Unit,
        onFinishAction: () -> Unit
    ) {
        timer =
            object : CountDownTimer(period, interval) {
                override fun onTick(millisUntilFinished: Long) {
                    onTickAction(millisUntilFinished)
                }

                override fun onFinish() {
                    onFinishAction()
                }
            }.start()
    }

    fun cancel() {
        if (::timer.isInitialized) timer.cancel()
    }
}
