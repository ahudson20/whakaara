package com.whakaara.data.timer

import com.whakaara.database.datastore.PreferencesDataStore
import com.whakaara.model.datastore.TimerStateDataStore
import com.whakaara.model.timer.TimerStateReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TimerRepositoryImpl @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : TimerRepository {
    private val _timerState = MutableStateFlow<TimerStateReceiver>(TimerStateReceiver.Idle)
    override val timerState: StateFlow<TimerStateReceiver> = _timerState.asStateFlow()
    override fun startTimer(currentTime: Long) {
        _timerState.update {
            TimerStateReceiver.Started(currentTime)
        }
    }

    override fun pauseTimer() {
        _timerState.update {
            TimerStateReceiver.Paused
        }
    }

    override suspend fun stopTimer() {
        _timerState.update {
            TimerStateReceiver.Stopped
        }
        preferencesDataStore.saveTimerData(
            state = TimerStateDataStore()
        )
    }
}
