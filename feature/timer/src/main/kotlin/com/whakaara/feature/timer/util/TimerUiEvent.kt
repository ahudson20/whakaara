package com.whakaara.feature.timer.util

sealed class TimerUiEvent {
    data class ShowSnackbar(val message: String) : TimerUiEvent()
}
