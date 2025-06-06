package com.whakaara.feature.alarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.whakaara.core.LogUtils.logD
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.WidgetUpdater
import com.whakaara.core.constants.GeneralConstants.MAIN_ACTIVITY
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.di.IoDispatcher
import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.feature.alarm.receiver.UpcomingAlarmReceiver
import com.whakaara.feature.alarm.service.AlarmMediaService
import com.whakaara.feature.alarm.utils.DateUtils
import com.whakaara.feature.alarm.utils.DateUtils.Companion.getAlarmTimeFormatted
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.alarm.AlarmState
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.preferences.TimeFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val app: Application,
    private val alarmManager: AlarmManager,
    private val repository: AlarmRepository,
    private val preferencesRepository: PreferencesRepository,
    private val widgetUpdater: WidgetUpdater,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application = app) {

    private val _alarmState = MutableStateFlow<AlarmState>(AlarmState.Loading)
    val alarmState: StateFlow<AlarmState> = _alarmState.asStateFlow()

    private val _preferencesState = MutableStateFlow(PreferencesState())
    val preferencesState: StateFlow<PreferencesState> = _preferencesState.asStateFlow()

    init {
        getAllAlarms()
        getPreferences()
        observeTrigger()
        observeDeleteTrigger()
    }

    private fun getAllAlarms() = viewModelScope.launch {
        repository.getAllAlarmsFlow().flowOn(ioDispatcher).collect { allAlarms ->
            _alarmState.value = AlarmState.Success(alarms = allAlarms)
        }
    }

    private fun getPreferences() = viewModelScope.launch {
        preferencesRepository.getPreferencesFlow().flowOn(ioDispatcher).collect { preferences ->
            _preferencesState.value = PreferencesState(preferences = preferences, isReady = true)
        }
    }

    private fun observeTrigger() = viewModelScope.launch(ioDispatcher) {
        repository.triggerFlow.collect {
            recreateEnabledAlarms()
        }
    }

    private suspend fun recreateEnabledAlarms() {
        val preferences = preferencesRepository.getPreferences()
        val enabledAlarms = repository.getAllAlarms().filter { it.isEnabled }
        enabledAlarms.forEach { alarm ->
            createAlarm(
                alarmId = alarm.alarmId.toString(),
                autoSilenceTime = preferences.autoSilenceTime.value,
                date = alarm.date,
                upcomingAlarmNotificationEnabled = preferences.upcomingAlarmNotification,
                upcomingAlarmNotificationTime = preferences.upcomingAlarmNotificationTime.value,
                repeatAlarmDaily = alarm.repeatDaily,
                daysOfWeek = alarm.daysOfWeek
            )
        }
    }

    private fun observeDeleteTrigger() = viewModelScope.launch {
        repository.deleteAlarmTriggerFlow.collect { alarmId ->
            try {
                deleteAlarm(alarmId)
                repository.deleteAlarmById(UUID.fromString(alarmId))
            } catch (exception: Exception) {
                logD("Failed to delete alarm: $alarmId", exception)
            }
        }
    }

    fun create(localTime: LocalTime) = viewModelScope.launch(ioDispatcher) {
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, localTime.hour)
            set(Calendar.MINUTE, localTime.minute)
            set(Calendar.SECOND, 0)
        }

        val alarm = Alarm(
            date = date,
            subTitle = getAlarmTimeFormatted(
                date = date,
                timeFormat = preferencesState.value.preferences.timeFormat
            ),
            vibration = preferencesState.value.preferences.isVibrateEnabled,
            isSnoozeEnabled = preferencesState.value.preferences.isSnoozeEnabled,
            deleteAfterGoesOff = preferencesState.value.preferences.deleteAfterGoesOff
        )

        repository.insert(alarm = alarm)
        createAlarm(
            alarmId = alarm.alarmId.toString(),
            date = alarm.date,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily,
            daysOfWeek = alarm.daysOfWeek
        )
    }

    fun delete(alarm: Alarm) = viewModelScope.launch(ioDispatcher) {
        repository.delete(alarm = alarm)
        deleteAlarm(alarmId = alarm.alarmId.toString())
        cancelUpcomingAlarm(alarmId = alarm.alarmId.toString(), alarmDate = alarm.date)
    }

    fun disable(alarm: Alarm) = viewModelScope.launch(ioDispatcher) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = false))
        deleteAlarm(alarmId = alarm.alarmId.toString())
        cancelUpcomingAlarm(alarmId = alarm.alarmId.toString(), alarmDate = alarm.date)
    }

    fun enable(alarm: Alarm) = viewModelScope.launch(ioDispatcher) {
        updateExistingAlarmInDatabase(alarm.copy(isEnabled = true))
        stopStartUpdateWidget(
            alarmId = alarm.alarmId.toString(),
            date = alarm.date,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily,
            daysOfWeek = alarm.daysOfWeek
        )
    }

    fun reset(alarm: Alarm) = viewModelScope.launch(ioDispatcher) {
        updateExistingAlarmInDatabase(alarm = alarm)
        stopStartUpdateWidget(
            alarmId = alarm.alarmId.toString(),
            date = alarm.date,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily,
            daysOfWeek = alarm.daysOfWeek
        )
    }

    fun snooze(alarm: Alarm) = viewModelScope.launch(ioDispatcher) {
        val currentTimePlusTenMinutes =
            Calendar.getInstance().apply {
                add(Calendar.MINUTE, _preferencesState.value.preferences.snoozeTime.value)
            }
        stopStartUpdateWidget(
            alarmId = alarm.alarmId.toString(),
            date = currentTimePlusTenMinutes,
            autoSilenceTime = _preferencesState.value.preferences.autoSilenceTime.value,
            upcomingAlarmNotificationEnabled = _preferencesState.value.preferences.upcomingAlarmNotification,
            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
            repeatAlarmDaily = alarm.repeatDaily,
            daysOfWeek = alarm.daysOfWeek
        )
    }

    private fun updateExistingAlarmInDatabase(alarm: Alarm) = viewModelScope.launch(ioDispatcher) {
        repository.update(alarm)
    }

    fun updateAllAlarmSubtitles(format: TimeFormat) = viewModelScope.launch(ioDispatcher) {
        val state = _alarmState.value
        if (state is AlarmState.Success) {
            state.alarms.forEach {
                updateExistingAlarmInDatabase(
                    it.copy(
                        subTitle = getAlarmTimeFormatted(
                            it.date,
                            format
                        )
                    )
                )
            }
            updateWidget()
        }
    }

    fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean) = viewModelScope.launch {
        val state = _alarmState.value
        if (state is AlarmState.Success) {
            state.alarms.forEach {
                if (it.isEnabled) {
                    if (shouldEnableUpcomingAlarmNotification) {
                        setUpcomingAlarm(
                            alarmId = it.alarmId.toString(),
                            alarmDate = it.date,
                            upcomingAlarmNotificationEnabled = true,
                            upcomingAlarmNotificationTime = _preferencesState.value.preferences.upcomingAlarmNotificationTime.value,
                            repeatAlarmDaily = it.repeatDaily,
                            daysOfWeek = it.daysOfWeek
                        )
                    } else {
                        cancelUpcomingAlarm(
                            alarmId = it.alarmId.toString(),
                            alarmDate = it.date
                        )
                    }
                }
            }
        }
    }

    private fun createAlarm(
        alarmId: String,
        date: Calendar,
        autoSilenceTime: Int,
        upcomingAlarmNotificationEnabled: Boolean,
        upcomingAlarmNotificationTime: Int,
        repeatAlarmDaily: Boolean,
        daysOfWeek: MutableList<Int>
    ) {
        startAlarm(
            alarmId = alarmId,
            autoSilenceTime = autoSilenceTime,
            date = date,
            upcomingAlarmNotificationEnabled = upcomingAlarmNotificationEnabled,
            upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
            repeatAlarmDaily = repeatAlarmDaily,
            daysOfWeek = daysOfWeek
        )
        updateWidget()
    }

    private fun deleteAlarm(alarmId: String) {
        stopAlarm(alarmId = alarmId)
        updateWidget()
    }

    private fun stopStartUpdateWidget(
        alarmId: String,
        date: Calendar,
        autoSilenceTime: Int,
        upcomingAlarmNotificationEnabled: Boolean,
        upcomingAlarmNotificationTime: Int,
        repeatAlarmDaily: Boolean,
        daysOfWeek: MutableList<Int>
    ) {
        stopAlarm(alarmId = alarmId)
        cancelUpcomingAlarm(alarmId = alarmId, alarmDate = date)
        startAlarm(
            alarmId = alarmId,
            autoSilenceTime = autoSilenceTime,
            date = date,
            upcomingAlarmNotificationEnabled = upcomingAlarmNotificationEnabled,
            upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
            repeatAlarmDaily = repeatAlarmDaily,
            daysOfWeek = daysOfWeek
        )
        updateWidget()
    }

    private fun startAlarm(
        alarmId: String,
        autoSilenceTime: Int,
        date: Calendar,
        upcomingAlarmNotificationEnabled: Boolean,
        upcomingAlarmNotificationTime: Int,
        repeatAlarmDaily: Boolean,
        daysOfWeek: MutableList<Int>
    ) {
        if (!userHasNotGrantedAlarmPermission()) {
            redirectUserToSpecialAppAccessScreen()
        } else {
            setAlarm(
                alarmId = alarmId,
                autoSilenceTime = autoSilenceTime,
                date = date,
                repeatAlarmDaily = repeatAlarmDaily,
                daysOfWeek = daysOfWeek
            )
            setUpcomingAlarm(
                alarmId = alarmId,
                alarmDate = date,
                upcomingAlarmNotificationTime = upcomingAlarmNotificationTime,
                upcomingAlarmNotificationEnabled = upcomingAlarmNotificationEnabled,
                repeatAlarmDaily = repeatAlarmDaily,
                daysOfWeek = daysOfWeek
            )
        }
    }

    private fun redirectUserToSpecialAppAccessScreen() {
        Intent().apply { action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM }.also {
            app.applicationContext.startActivity(it)
        }
    }

    private fun userHasNotGrantedAlarmPermission() = alarmManager.canScheduleExactAlarms()

    private fun setAlarm(
        alarmId: String,
        autoSilenceTime: Int,
        date: Calendar,
        repeatAlarmDaily: Boolean,
        daysOfWeek: MutableList<Int>
    ) {
        val triggerTime = DateUtils.getTimeAsDate(alarmDate = date)

        val startReceiverIntent = getStartReceiverIntent(
            alarmId = alarmId,
            autoSilenceTime = autoSilenceTime,
            type = NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
        )

        val alarmPendingIntent = PendingIntentUtils.getService(
            app,
            NotificationUtilsConstants.INTENT_REQUEST_CODE,
            startReceiverIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val deepLinkIntent = Intent(Intent.ACTION_VIEW).apply {
            setClassName(
                app.applicationContext.packageName,
                MAIN_ACTIVITY
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val alarmInfoPendingIntent = PendingIntentUtils.getActivity(
            app,
            NotificationUtilsConstants.INTENT_REQUEST_CODE,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (repeatAlarmDaily || daysOfWeek.isNotEmpty()) {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                triggerTime.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmPendingIntent
            )
        } else {
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerTime.timeInMillis, alarmInfoPendingIntent),
                alarmPendingIntent
            )
        }
    }

    private fun setUpcomingAlarm(
        alarmId: String,
        alarmDate: Calendar,
        upcomingAlarmNotificationEnabled: Boolean,
        upcomingAlarmNotificationTime: Int,
        repeatAlarmDaily: Boolean,
        daysOfWeek: MutableList<Int>
    ) {
        val triggerTime = DateUtils.getTimeAsDate(alarmDate = alarmDate)
        val triggerTimeMinusTenMinutes = (triggerTime.clone() as Calendar).apply {
            add(Calendar.MINUTE, -upcomingAlarmNotificationTime)
        }

        val upcomingAlarmIntent = Intent(app, UpcomingAlarmReceiver::class.java).apply {
            action = alarmId
            putExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION, NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_START)
            putExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME, triggerTime.timeInMillis)
        }

        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = app,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = upcomingAlarmIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (triggerTimeMinusTenMinutes.timeInMillis > Calendar.getInstance().timeInMillis && upcomingAlarmNotificationEnabled) {
            if (repeatAlarmDaily || daysOfWeek.isNotEmpty()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMinusTenMinutes.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMinusTenMinutes.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    private fun getStartReceiverIntent(
        autoSilenceTime: Int,
        action: Int = NotificationUtilsConstants.PLAY,
        type: Int,
        alarmId: String? = null
    ) = Intent(app, AlarmMediaService::class.java).apply {
        this.action = alarmId
        putExtra(NotificationUtilsConstants.INTENT_AUTO_SILENCE, autoSilenceTime)
        putExtra(NotificationUtilsConstants.SERVICE_ACTION, action)
        putExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, type)
        putExtra(NotificationUtilsConstants.INTENT_ALARM_ID, alarmId)
    }

    private fun stopAlarm(alarmId: String) {
        val intent = Intent(app, AlarmMediaService::class.java).apply {
            // setting unique action allows for differentiation when deleting.
            this.action = alarmId
        }

        val pendingIntent = PendingIntentUtils.getService(
            context = app,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun updateWidget() {
        widgetUpdater.updateWidget()
    }

    private fun cancelUpcomingAlarm(
        alarmId: String,
        alarmDate: Calendar
    ) {
        val triggerTime = DateUtils.getTimeAsDate(alarmDate = alarmDate)

        val upcomingAlarmIntent = Intent(app, UpcomingAlarmReceiver::class.java).apply {
            action = alarmId
        }

        val upcomingAlarmPendingIntent = PendingIntentUtils.getBroadcast(
            context = app,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
            intent = upcomingAlarmIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        // cancel upcoming alarm
        alarmManager.cancel(upcomingAlarmPendingIntent)

        // send broadcast to cancel notification
        upcomingAlarmIntent.also { intent ->
            intent.putExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_ACTION, NotificationUtilsConstants.UPCOMING_ALARM_RECEIVER_ACTION_STOP)
            intent.putExtra(NotificationUtilsConstants.UPCOMING_ALARM_INTENT_TRIGGER_TIME, triggerTime.timeInMillis)
            app.applicationContext.sendBroadcast(intent)
        }
    }

    fun getInitialTimeToAlarm(
        isEnabled: Boolean,
        time: Calendar
    ): String {
        return if (!isEnabled) {
            app.applicationContext.getString(R.string.card_alarm_sub_title_off)
        } else {
            convertSecondsToHMm(
                seconds = TimeUnit.MILLISECONDS.toSeconds(
                    DateUtils.getDifferenceFromCurrentTimeInMillis(
                        time = time
                    )
                )
            )
        }
    }

    fun getTimeUntilAlarmFormatted(localTime: LocalTime): String {
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, localTime.hour)
            set(Calendar.MINUTE, localTime.minute)
            set(Calendar.SECOND, 0)
        }
        return convertSecondsToHMm(
            seconds = TimeUnit.MILLISECONDS.toSeconds(
                DateUtils.getDifferenceFromCurrentTimeInMillis(
                    time = date
                )
            )
        )
    }

    fun getTimeUntilAlarmFormatted(date: Calendar): String {
        return convertSecondsToHMm(
            seconds = TimeUnit.MILLISECONDS.toSeconds(
                DateUtils.getDifferenceFromCurrentTimeInMillis(
                    time = date
                )
            )
        )
    }

    private fun convertSecondsToHMm(seconds: Long): String {
        val minutes = seconds / 60 % 60
        val hours = seconds / (60 * 60) % 24
        val formattedString = StringBuilder()
        val hoursString = when {
            hours.toInt() == 0 -> ""
            else -> {
                app.applicationContext.resources.getQuantityString(
                    R.plurals.hours,
                    hours.toInt(),
                    hours.toInt()
                )
            }
        }
        val minutesString = when {
            minutes.toInt() == 0 && hours.toInt() != 0 -> ""
            minutes.toInt() == 0 && hours.toInt() == 0 -> app.applicationContext.getString(R.string.alarm_less_than_one_minute)
            else -> {
                app.applicationContext.resources.getQuantityString(
                    R.plurals.minutes,
                    minutes.toInt(),
                    minutes.toInt()
                )
            }
        }

        formattedString.append(app.applicationContext.resources.getString(R.string.time_until_alarm_formatted_prefix) + " ")
        if (hoursString.isNotBlank()) formattedString.append("$hoursString ")
        if (minutesString.isNotBlank()) formattedString.append("$minutesString ")
        return formattedString.toString().trim()
    }
}

data class HoursUpdateEvent(
    val value: Hours = FullHours(0, 0),
    val onValueChange: (Hours) -> Unit = {}
)
