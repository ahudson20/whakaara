package com.whakaara.feature.stopwatch

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.constants.GeneralConstants
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.di.ApplicationScope
import com.whakaara.core.di.IoDispatcher
import com.whakaara.core.di.MainDispatcher
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import com.whakaara.data.stopwatch.StopwatchRepository
import com.whakaara.feature.stopwatch.receiver.StopwatchReceiver
import com.whakaara.feature.stopwatch.util.DateUtils
import com.whakaara.model.stopwatch.Lap
import com.whakaara.model.stopwatch.StopwatchState
import com.whakaara.model.stopwatch.asExternalModel
import com.whakaara.model.stopwatch.asInternalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val app: Application,
    private val notificationManager: NotificationManager,
    @Named("stopwatch")
    private val stopwatchNotificationBuilder: NotificationCompat.Builder,
    @ApplicationScope private val coroutineScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val preferencesDataStore: PreferencesDataStoreRepository,
    private val stopwatchRepository: StopwatchRepository
): AndroidViewModel(application = app) {

    private val _stopwatchState: MutableStateFlow<StopwatchState> = MutableStateFlow(StopwatchState())
    val stopwatchState: StateFlow<StopwatchState> = _stopwatchState.asStateFlow()

    init {
        collectStopwatchStateFromReceiver()
    }

    private fun collectStopwatchStateFromReceiver() = viewModelScope.launch(ioDispatcher) {
        val localState = preferencesDataStore.readStopwatchState().first().asExternalModel()
        stopwatchRepository.stopwatchState.collectLatest { state ->
            when (state) {
                is com.whakaara.model.stopwatch.StopwatchReceiver.Idle -> {
                    Log.d("StopwatchViewModel", "StopwatchReceiver.Idle")
                }

                is com.whakaara.model.stopwatch.StopwatchReceiver.Started -> {
                    recreateStopwatchActiveFromReceiver(state = localState)
                    cancelNotification()
                    createStopwatchNotification()

                    Log.d("StopwatchViewModel", "StopwatchReceiver.Started")
                }

                is com.whakaara.model.stopwatch.StopwatchReceiver.Paused -> {
                    recreateStopwatchPausedFromReceiver(state = localState)
                    cancelNotification()
                    pauseStopwatchNotification()

                    Log.d("StopwatchViewModel", "StopwatchReceiver.Paused")
                }

                is com.whakaara.model.stopwatch.StopwatchReceiver.Stopped -> {
                    resetStopwatch()

                    Log.d("StopwatchViewModel", "StopwatchReceiver.Stopped")
                }
            }
        }
    }

    fun saveStopwatchStateForRecreation() = viewModelScope.launch(ioDispatcher) {
        if (stopwatchState.value.isActive || stopwatchState.value.isPaused) {
            preferencesDataStore.saveStopwatchState(
                state = stopwatchState.value.asInternalModel()
            )
        }
    }

    fun recreateStopwatch() = viewModelScope.launch(mainDispatcher) {
        if (stopwatchState.value == StopwatchState()) {
            val state = preferencesDataStore.readStopwatchState().first().asExternalModel()
            if (state.isActive) {
                recreateStopwatchActive(state = state)
                preferencesDataStore.clearStopwatchState()
            } else if (state.isPaused) {
                recreateStopwatchPaused(state = state)
                preferencesDataStore.clearStopwatchState()
            }
        }
    }

    fun startStopwatchNotification() {
        if (stopwatchState.value.isActive) {
            createStopwatchNotification()
        } else if (stopwatchState.value.isPaused) {
            pauseStopwatchNotification()
        }
    }

    fun cancelStopwatchNotification() {
        cancelNotification()
    }
    //endregion

    //region swmw
    fun startStopwatch() {
        if (stopwatchState.value.isActive) {
            return
        }

        coroutineScope.launch {
            val lastTimeStamp = System.currentTimeMillis()

            _stopwatchState.update {
                it.copy(
                    lastTimeStamp = lastTimeStamp,
                    isActive = true,
                    isStart = false,
                    isPaused = false
                )
            }

            while (stopwatchState.value.isActive) {
                delay(GeneralConstants.TIMER_START_DELAY_MILLIS)
                val time = stopwatchState.value.timeMillis + (System.currentTimeMillis() - stopwatchState.value.lastTimeStamp)
                _stopwatchState.update {
                    it.copy(
                        timeMillis = time,
                        lastTimeStamp = System.currentTimeMillis(),
                        formattedTime = DateUtils.formatTimeForStopwatch(
                            millis = time
                        )
                    )
                }
            }
        }
    }

    fun pauseStopwatch() {
        cancelNotification()
        _stopwatchState.update {
            it.copy(
                isActive = false,
                isPaused = true
            )
        }
    }

    fun resetStopwatch() = viewModelScope.launch(ioDispatcher) {
        cancelNotification()
        coroutineScope.coroutineContext.cancelChildren()
        _stopwatchState.update {
            it.copy(
                timeMillis = GeneralConstants.ZERO_MILLIS,
                lastTimeStamp = GeneralConstants.ZERO_MILLIS,
                formattedTime = DateUtilsConstants.STOPWATCH_STARTING_TIME,
                isActive = false,
                isStart = true,
                isPaused = false,
                lapList = mutableListOf()
            )
        }
        preferencesDataStore.clearStopwatchState()
    }

    fun lapStopwatch() {
        if (stopwatchState.value.lapList.size < GeneralConstants.MAX_NUMBER_OF_LAPS) {
            val diff: Long
            val current = stopwatchState.value.timeMillis
            diff = if (stopwatchState.value.lapList.isEmpty()) {
                current
            } else {
                current - stopwatchState.value.lapList.last().time
            }
            val nextLap = Lap(
                time = current,
                diff = diff
            )
            val newList = stopwatchState.value.lapList.plus(nextLap).toMutableList()
            _stopwatchState.update {
                it.copy(
                    lapList = newList
                )
            }
        }
    }

    fun recreateStopwatchActive(state: StopwatchState) {
        val current = System.currentTimeMillis()
        val difference = current - state.lastTimeStamp
        val time = difference + state.timeMillis
        _stopwatchState.update {
            it.copy(
                lastTimeStamp = current,
                timeMillis = time,
                formattedTime = DateUtils.formatTimeForStopwatch(
                    millis = time
                ),
                lapList = state.lapList
            )
        }
        startStopwatch()
    }

    suspend fun recreateStopwatchActiveFromReceiver(state: StopwatchState) {
        if (stopwatchState.value != StopwatchState()) {
            startStopwatch()
        } else {
            _stopwatchState.update {
                state.copy(
                    isStart = false,
                    isActive = false,
                    isPaused = false
                )
            }
            startStopwatch()
        }
        preferencesDataStore.saveStopwatchState(stopwatchState.value.asInternalModel())
    }

    fun recreateStopwatchPausedFromReceiver(state: StopwatchState) {
        if (stopwatchState.value != StopwatchState()) {
            pauseStopwatch()
        } else {
            val current = System.currentTimeMillis()
            val difference = current - state.lastTimeStamp
            val time = difference + state.timeMillis
            recreateStopwatchPaused(
                state = state.copy(
                    lastTimeStamp = current,
                    timeMillis = time,
                    formattedTime = DateUtils.formatTimeForStopwatch(
                        millis = time
                    ),
                    isPaused = true,
                    isActive = false,
                    isStart = false
                )
            )
        }

        runBlocking {
            preferencesDataStore.saveStopwatchState(stopwatchState.value.asInternalModel())
        }
    }

    fun recreateStopwatchPaused(state: StopwatchState) {
        _stopwatchState.update {
            state
        }
    }

    fun createStopwatchNotification() {
        val lastTimeStamp = System.currentTimeMillis()
        val setWhen =
            if (stopwatchState.value.isStart) {
                lastTimeStamp
            } else {
                lastTimeStamp - stopwatchState.value.timeMillis
            }

        val pauseReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_PAUSE)
        val stopReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP)

        val pauseReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = pauseReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )
        val stopReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = stopReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        notificationManager.notify(
            NotificationUtilsConstants.STOPWATCH_NOTIFICATION_ID,
            stopwatchNotificationBuilder.apply {
                clearActions()
                setWhen(setWhen)
                setUsesChronometer(true)
                setSubText(app.applicationContext.getString(R.string.stopwatch_notification_sub_text))
                addAction(
                    0,
                    app.applicationContext.getString(R.string.notification_timer_pause_action_label),
                    pauseReceiverPendingIntent
                )
                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopReceiverPendingIntent)
            }.build()
        )
    }

    fun pauseStopwatchNotification() {
        val startReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_START)
        val stopReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP)

        val playReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = startReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        val stopReceiverPendingIntent =
            PendingIntentUtils.getBroadcast(
                context = app.applicationContext,
                id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
                intent = stopReceiverIntent,
                flag = PendingIntent.FLAG_UPDATE_CURRENT
            )

        notificationManager.notify(
            NotificationUtilsConstants.STOPWATCH_NOTIFICATION_ID,
            stopwatchNotificationBuilder.apply {
                clearActions()
                setUsesChronometer(false)
                setSubText(app.applicationContext.getString(R.string.notification_sub_text_paused))
                setWhen(System.currentTimeMillis())
                addAction(0, app.applicationContext.getString(R.string.notification_timer_play_action_label), playReceiverPendingIntent)
                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopReceiverPendingIntent)
            }.build()
        )
    }

    fun cancelNotification() {
        notificationManager.cancel(NotificationUtilsConstants.STOPWATCH_NOTIFICATION_ID)
    }

    companion object {
        fun Context.getTimerReceiverIntent(intentAction: String): Intent {
            return Intent(this, StopwatchReceiver::class.java).apply {
                action = intentAction
            }
        }
    }
    //endregion
}
