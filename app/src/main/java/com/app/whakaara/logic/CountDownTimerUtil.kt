package com.app.whakaara.logic

import android.os.CountDownTimer
import com.app.whakaara.utils.constants.GeneralConstants.TIMER_INTERVAL
import javax.inject.Inject

class CountDownTimerUtil @Inject constructor() {

    private lateinit var timer: CountDownTimer

    fun countdown(
        period: Long,
        interval: Long = TIMER_INTERVAL,
        onTickAction: (millisUntilFinished: Long) -> Unit,
        onFinishAction: () -> Unit
    ) {
        timer = object : CountDownTimer(period, interval) {
            override fun onTick(millisUntilFinished: Long) {
                onTickAction(millisUntilFinished)
            }

            override fun onFinish() {
                onFinishAction()
            }
        }.start()
    }

    fun cancel() {
        timer.cancel()
    }
}
