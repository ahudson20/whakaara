package com.app.whakaara.state

import com.whakaara.core.constants.DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
import com.whakaara.core.constants.DateUtilsConstants.TIMER_STARTING_FORMAT
import com.whakaara.core.constants.GeneralConstants.STARTING_CIRCULAR_PROGRESS
import com.whakaara.core.constants.GeneralConstants.ZERO_MILLIS

data class TimerState(
    val currentTime: Long = ZERO_MILLIS,
    val inputHours: String = TIMER_INPUT_INITIAL_VALUE,
    val inputMinutes: String = TIMER_INPUT_INITIAL_VALUE,
    val inputSeconds: String = TIMER_INPUT_INITIAL_VALUE,
    val isTimerPaused: Boolean = false,
    val isStart: Boolean = true,
    val isTimerActive: Boolean = false,
    val progress: Float = STARTING_CIRCULAR_PROGRESS,
    val time: String = TIMER_STARTING_FORMAT,
    val millisecondsFromTimerInput: Long = ZERO_MILLIS
)
