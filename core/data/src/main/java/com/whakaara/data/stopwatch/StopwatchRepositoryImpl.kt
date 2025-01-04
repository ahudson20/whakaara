package com.whakaara.data.stopwatch

import com.whakaara.database.datastore.PreferencesDataStore
import com.whakaara.model.stopwatch.StopwatchReceiverState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class StopwatchRepositoryImpl @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : StopwatchRepository {
    private val _stopwatchState = MutableStateFlow<StopwatchReceiverState>(StopwatchReceiverState.Idle)
    override val stopwatchState: StateFlow<StopwatchReceiverState> = _stopwatchState.asStateFlow()

    override fun startStopwatch() {
        _stopwatchState.update {
            StopwatchReceiverState.Started
        }
    }

    override fun pauseStopwatch() {
        _stopwatchState.update {
            StopwatchReceiverState.Paused
        }
    }

    override suspend fun stopStopwatch() {
        _stopwatchState.update {
            StopwatchReceiverState.Stopped
        }
        preferencesDataStore.clearStopwatchState()
    }
}
