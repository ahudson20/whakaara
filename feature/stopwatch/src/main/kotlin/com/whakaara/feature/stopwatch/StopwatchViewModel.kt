package com.whakaara.feature.stopwatch

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.whakaara.core.LogUtils.logD
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
import com.whakaara.model.stopwatch.StopwatchReceiverState
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
) : AndroidViewModel(application = app) {

    private val _stopwatchState: MutableStateFlow<StopwatchState> = MutableStateFlow(StopwatchState())
    val stopwatchState: StateFlow<StopwatchState> = _stopwatchState.asStateFlow()

    init {
        collectStopwatchStateFromReceiver()
    }

    private fun collectStopwatchStateFromReceiver() = viewModelScope.launch(ioDispatcher) {
        val localState = preferencesDataStore.readStopwatchState().first().asExternalModel()
        stopwatchRepository.stopwatchState.collectLatest { state ->
            handleReceiverState(receiverState = state, localState = localState)
        }
    }

    private suspend fun handleReceiverState(receiverState: StopwatchReceiverState, localState: StopwatchState) {
        when (receiverState) {
            is StopwatchReceiverState.Idle -> {
                logD("StopwatchReceiver.Idle")
            }

            is StopwatchReceiverState.Started -> {
                recreateStopwatchActiveFromReceiver(state = localState)
                cancelNotification()
                createStopwatchNotification()

                logD("StopwatchReceiver.Started")
            }

            is StopwatchReceiverState.Paused -> {
                recreateStopwatchPausedFromReceiver(state = localState)
                cancelNotification()
                pauseStopwatchNotification()

                logD("StopwatchReceiver.Paused")
            }

            is StopwatchReceiverState.Stopped -> {
                resetStopwatch()

                logD("StopwatchReceiver.Stopped")
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
            when {
                state.isActive -> {
                    recreateStopwatchActive(state = state)
                    preferencesDataStore.clearStopwatchState()
                }

                state.isPaused -> {
                    recreateStopwatchPaused(state = state)
                    preferencesDataStore.clearStopwatchState()
                }
            }
        }
    }

    fun startStopwatchNotification() {
        when {
            stopwatchState.value.isActive -> {
                createStopwatchNotification()
            }

            stopwatchState.value.isPaused -> {
                pauseStopwatchNotification()
            }
        }
    }

    fun cancelStopwatchNotification() = cancelNotification()
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

    private fun recreateStopwatchActive(state: StopwatchState) {
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

    private suspend fun recreateStopwatchActiveFromReceiver(state: StopwatchState) {
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

    private suspend fun recreateStopwatchPausedFromReceiver(state: StopwatchState) {
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
                    formattedTime = DateUtils.formatTimeForStopwatch(millis = time),
                    isPaused = true,
                    isActive = false,
                    isStart = false
                )
            )
        }
        preferencesDataStore.saveStopwatchState(stopwatchState.value.asInternalModel())
    }

    private fun recreateStopwatchPaused(state: StopwatchState) {
        _stopwatchState.update {
            state
        }
    }

    private fun createStopwatchNotification() {
        val setWhen = computeNotificationSetWhen()

        val actions = listOf(
            buildNotificationAction(
                action = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_PAUSE,
                labelRes = R.string.notification_timer_pause_action_label
            ),
            buildNotificationAction(
                action = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP,
                labelRes = R.string.notification_timer_stop_action_label
            )
        )

        showStopwatchNotification(
            useChronometer = true,
            subText = app.applicationContext.getString(R.string.stopwatch_notification_sub_text),
            setWhen = setWhen,
            actions = actions
        )
    }

    private fun pauseStopwatchNotification() {
        val actions = listOf(
            buildNotificationAction(
                action = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_START,
                labelRes = R.string.notification_timer_play_action_label
            ),
            buildNotificationAction(
                action = NotificationUtilsConstants.STOPWATCH_RECEIVER_ACTION_STOP,
                labelRes = R.string.notification_timer_stop_action_label
            )
        )

        showStopwatchNotification(
            useChronometer = false,
            subText = app.applicationContext.getString(R.string.notification_sub_text_paused),
            setWhen = System.currentTimeMillis(),
            actions = actions
        )
    }

    private fun computeNotificationSetWhen(): Long {
        val now = System.currentTimeMillis()
        return if (stopwatchState.value.isStart) now
        else now - stopwatchState.value.timeMillis
    }

    private fun buildNotificationAction(action: String, labelRes: Int): NotificationCompat.Action {
        val context = app.applicationContext
        val intent = context.getTimerReceiverIntent(action)

        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = context,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Action.Builder(
            0,
            context.getString(labelRes),
            pendingIntent
        ).build()
    }

    private fun showStopwatchNotification(
        useChronometer: Boolean,
        subText: String,
        setWhen: Long,
        actions: List<NotificationCompat.Action>
    ) {
        val builder = stopwatchNotificationBuilder.apply {
            clearActions()
            setUsesChronometer(useChronometer)
            setSubText(subText)
            setWhen(setWhen)
            actions.forEach { addAction(it) }
        }

        notificationManager.notify(
            NotificationUtilsConstants.STOPWATCH_NOTIFICATION_ID,
            builder.build()
        )
    }

    private fun cancelNotification() {
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
