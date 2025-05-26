package com.app.whakaara.state.events

import com.app.whakaara.logic.MainViewModel
import com.whakaara.feature.alarm.AlarmViewModel
import com.whakaara.feature.stopwatch.StopwatchViewModel
import com.whakaara.feature.timer.TimerViewModel

data class AppViewModels(
    val main: MainViewModel,
    val timer: TimerViewModel,
    val stopwatch: StopwatchViewModel,
    val alarm: AlarmViewModel
)
