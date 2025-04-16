package com.whakaara.feature.timer.util

data class TimerUiState(
    val isTimerActive: Boolean = false,
    val isStart: Boolean = true,
    val shouldShowPlayButton: Boolean = false,
    val finishTime: String? = null
)
