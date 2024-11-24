package com.app.whakaara.logic

//class StopwatchManagerWrapper @Inject constructor(
//    private val app: Application,
//    private val notificationManager: NotificationManager,
//    @Named("stopwatch")
//    private val stopwatchNotificationBuilder: NotificationCompat.Builder,
//    @ApplicationScope
//    private val coroutineScope: CoroutineScope,
//    private val preferencesDataStore: PreferencesDataStoreRepository
//) {
//    val stopwatchState = MutableStateFlow(StopwatchState())
//
//    fun startStopwatch() {
//        if (stopwatchState.value.isActive) {
//            return
//        }
//
//        coroutineScope.launch {
//            val lastTimeStamp = System.currentTimeMillis()
//
//            stopwatchState.update {
//                it.copy(
//                    lastTimeStamp = lastTimeStamp,
//                    isActive = true,
//                    isStart = false,
//                    isPaused = false
//                )
//            }
//
//            while (stopwatchState.value.isActive) {
//                delay(TIMER_START_DELAY_MILLIS)
//                val time = stopwatchState.value.timeMillis + (System.currentTimeMillis() - stopwatchState.value.lastTimeStamp)
//                stopwatchState.update {
//                    it.copy(
//                        timeMillis = time,
//                        lastTimeStamp = System.currentTimeMillis(),
//                        formattedTime = DateUtils.formatTimeForStopwatch(
//                            millis = time
//                        )
//                    )
//                }
//            }
//        }
//    }
//
//    fun pauseStopwatch() {
//        cancelNotification()
//        stopwatchState.update {
//            it.copy(
//                isActive = false,
//                isPaused = true
//            )
//        }
//    }
//
//    fun resetStopwatch() {
//        cancelNotification()
//        coroutineScope.coroutineContext.cancelChildren()
//        stopwatchState.update {
//            it.copy(
//                timeMillis = ZERO_MILLIS,
//                lastTimeStamp = ZERO_MILLIS,
//                formattedTime = STOPWATCH_STARTING_TIME,
//                isActive = false,
//                isStart = true,
//                isPaused = false,
//                lapList = mutableListOf()
//            )
//        }
//    }
//
//    fun lapStopwatch() {
//        if (stopwatchState.value.lapList.size < MAX_NUMBER_OF_LAPS) {
//            val diff: Long
//            val current = stopwatchState.value.timeMillis
//            diff = if (stopwatchState.value.lapList.isEmpty()) {
//                current
//            } else {
//                current - stopwatchState.value.lapList.last().time
//            }
//            val nextLap = Lap(
//                time = current,
//                diff = diff
//            )
//            val newList = stopwatchState.value.lapList.plus(nextLap).toMutableList()
//            stopwatchState.update {
//                it.copy(
//                    lapList = newList
//                )
//            }
//        }
//    }
//
//    fun recreateStopwatchActive(state: StopwatchState) {
//        val current = System.currentTimeMillis()
//        val difference = current - state.lastTimeStamp
//        val time = difference + state.timeMillis
//        stopwatchState.update {
//            it.copy(
//                lastTimeStamp = current,
//                timeMillis = time,
//                formattedTime = DateUtils.formatTimeForStopwatch(
//                    millis = time
//                ),
//                lapList = state.lapList
//            )
//        }
//        startStopwatch()
//    }
//
//    fun recreateStopwatchActiveFromReceiver(state: StopwatchState) {
//        if (stopwatchState.value != StopwatchState()) {
//            startStopwatch()
//        } else {
//            stopwatchState.update {
//                state.copy(
//                    isStart = false,
//                    isActive = false,
//                    isPaused = false
//                )
//            }
//            startStopwatch()
//        }
//        runBlocking {
//            preferencesDataStore.saveStopwatchState(stopwatchState.value.asInternalModel())
//        }
//    }
//
//    fun recreateStopwatchPausedFromReceiver(state: StopwatchState) {
//        if (stopwatchState.value != StopwatchState()) {
//            pauseStopwatch()
//        } else {
//            val current = System.currentTimeMillis()
//            val difference = current - state.lastTimeStamp
//            val time = difference + state.timeMillis
//            recreateStopwatchPaused(
//                state = state.copy(
//                    lastTimeStamp = current,
//                    timeMillis = time,
//                    formattedTime = DateUtils.formatTimeForStopwatch(
//                        millis = time
//                    ),
//                    isPaused = true,
//                    isActive = false,
//                    isStart = false
//                )
//            )
//        }
//
//        runBlocking {
//            preferencesDataStore.saveStopwatchState(stopwatchState.value.asInternalModel())
//        }
//    }
//
//    fun recreateStopwatchPaused(state: StopwatchState) {
//        stopwatchState.update {
//            state
//        }
//    }
//
//    fun createStopwatchNotification() {
//        val lastTimeStamp = System.currentTimeMillis()
//        val setWhen =
//            if (stopwatchState.value.isStart) {
//                lastTimeStamp
//            } else {
//                lastTimeStamp - stopwatchState.value.timeMillis
//            }
//
//        val pauseReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_PAUSE)
//        val stopReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_STOP)
//
//        val pauseReceiverPendingIntent =
//            PendingIntentUtils.getBroadcast(
//                context = app.applicationContext,
//                id = INTENT_REQUEST_CODE,
//                intent = pauseReceiverIntent,
//                flag = PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        val stopReceiverPendingIntent =
//            PendingIntentUtils.getBroadcast(
//                context = app.applicationContext,
//                id = INTENT_REQUEST_CODE,
//                intent = stopReceiverIntent,
//                flag = PendingIntent.FLAG_UPDATE_CURRENT
//            )
//
//        notificationManager.notify(
//            STOPWATCH_NOTIFICATION_ID,
//            stopwatchNotificationBuilder.apply {
//                clearActions()
//                setWhen(setWhen)
//                setUsesChronometer(true)
//                setSubText(app.applicationContext.getString(R.string.stopwatch_notification_sub_text))
//                addAction(
//                    0,
//                    app.applicationContext.getString(R.string.notification_timer_pause_action_label),
//                    pauseReceiverPendingIntent
//                )
//                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopReceiverPendingIntent)
//            }.build()
//        )
//    }
//
//    fun pauseStopwatchNotification() {
//        val startReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_START)
//        val stopReceiverIntent = app.applicationContext.getTimerReceiverIntent(intentAction = STOPWATCH_RECEIVER_ACTION_STOP)
//
//        val playReceiverPendingIntent =
//            PendingIntentUtils.getBroadcast(
//                context = app.applicationContext,
//                id = INTENT_REQUEST_CODE,
//                intent = startReceiverIntent,
//                flag = PendingIntent.FLAG_UPDATE_CURRENT
//            )
//
//        val stopReceiverPendingIntent =
//            PendingIntentUtils.getBroadcast(
//                context = app.applicationContext,
//                id = INTENT_REQUEST_CODE,
//                intent = stopReceiverIntent,
//                flag = PendingIntent.FLAG_UPDATE_CURRENT
//            )
//
//        notificationManager.notify(
//            STOPWATCH_NOTIFICATION_ID,
//            stopwatchNotificationBuilder.apply {
//                clearActions()
//                setUsesChronometer(false)
//                setSubText(app.applicationContext.getString(R.string.notification_sub_text_paused))
//                setWhen(System.currentTimeMillis())
//                addAction(0, app.applicationContext.getString(R.string.notification_timer_play_action_label), playReceiverPendingIntent)
//                addAction(0, app.applicationContext.getString(R.string.notification_timer_stop_action_label), stopReceiverPendingIntent)
//            }.build()
//        )
//    }
//
//    fun cancelNotification() {
//        notificationManager.cancel(STOPWATCH_NOTIFICATION_ID)
//    }
//
//    companion object {
//        fun Context.getTimerReceiverIntent(intentAction: String): Intent {
//            return Intent(this, StopwatchReceiver::class.java).apply {
//                action = intentAction
//            }
//        }
//    }
//}
