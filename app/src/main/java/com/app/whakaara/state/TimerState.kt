package com.app.whakaara.state

import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_STARTING_FORMAT
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS

data class TimerState(
    val currentTime: Long = ZERO_MILLIS,
    val celebration: Boolean = false,
    val inputHours: String = TIMER_INPUT_INITIAL_VALUE,
    val inputMinutes: String = TIMER_INPUT_INITIAL_VALUE,
    val inputSeconds: String = TIMER_INPUT_INITIAL_VALUE,
    val isTimerPaused: Boolean = false,
    val isStart: Boolean = true,
    val isTimerActive: Boolean = false,
    val progress: Float = 1.00F,
    val time: String = TIMER_STARTING_FORMAT
)
