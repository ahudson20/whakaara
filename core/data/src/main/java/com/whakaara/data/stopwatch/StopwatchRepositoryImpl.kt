package com.whakaara.data.stopwatch

import com.whakaara.database.datastore.PreferencesDataStore
import com.whakaara.model.stopwatch.StopwatchReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class StopwatchRepositoryImpl @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore
) : StopwatchRepository {
    private val _stopwatchState = MutableStateFlow<StopwatchReceiver>(StopwatchReceiver.Idle)
    override val stopwatchState: StateFlow<StopwatchReceiver> = _stopwatchState.asStateFlow()

    override fun startStopwatch() {
        _stopwatchState.update {
            StopwatchReceiver.Started
        }
    }

    override fun pauseStopwatch() {
        _stopwatchState.update {
            StopwatchReceiver.Paused
        }
    }

    override suspend fun stopStopwatch() {
        _stopwatchState.update {
            StopwatchReceiver.Stopped
        }
        preferencesDataStore.clearStopwatchState()
    }
}
