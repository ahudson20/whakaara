package com.app.whakaara.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.preferences.AppTheme
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.events.AlarmEventCallbacks
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.state.events.StopwatchEventCallbacks
import com.app.whakaara.state.events.TimerEventCallbacks
import com.app.whakaara.ui.screens.MainScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalLayoutApi
class MainActivity :
    ComponentActivity(),
    AlarmEventCallbacks,
    TimerEventCallbacks,
    StopwatchEventCallbacks,
    PreferencesEventCallbacks {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.alarmState.value is AlarmState.Loading && !viewModel.preferencesUiState.value.isReady
        }

        setContent {
            val preferencesState by viewModel.preferencesUiState.collectAsStateWithLifecycle()
            val alarmState by viewModel.alarmState.collectAsStateWithLifecycle()
            val stopwatchState by viewModel.stopwatchState.collectAsStateWithLifecycle()
            val timerState by viewModel.timerState.collectAsStateWithLifecycle()

            val useDarkColours = when (preferencesState.preferences.appTheme) {
                AppTheme.MODE_DAY -> false
                AppTheme.MODE_NIGHT -> true
                AppTheme.MODE_AUTO -> isSystemInDarkTheme()
            }

            WhakaaraTheme(
                darkTheme = useDarkColours,
                dynamicColor = preferencesState.preferences.dynamicTheme
            ) {
                MainScreen(
                    preferencesState = preferencesState,
                    alarmState = alarmState,
                    stopwatchState = stopwatchState,
                    timerState = timerState,
                    alarmEventCallbacks = this@MainActivity,
                    timerEventCallbacks = this@MainActivity,
                    stopwatchEventCallbacks = this@MainActivity,
                    preferencesEventCallbacks = this@MainActivity
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        with(viewModel) {
            recreateTimer()
            recreateStopwatch()
            cancelStopwatchNotification()
            cancelTimerNotification()
        }
    }

    override fun onPause() {
        super.onPause()
        with(viewModel) {
            saveTimerStateForRecreation()
            saveStopwatchStateForRecreation()
            startStopwatchNotification()
            startTimerNotification()
        }
    }

    //region alarm
    override fun create(alarm: Alarm) {
        viewModel.create(alarm = alarm)
    }

    override fun delete(alarm: Alarm) {
        viewModel.delete(alarm = alarm)
    }

    override fun disable(alarm: Alarm) {
        viewModel.disable(alarm = alarm)
    }

    override fun enable(alarm: Alarm) {
        viewModel.enable(alarm = alarm)
    }

    override fun reset(alarm: Alarm) {
        viewModel.reset(alarm = alarm)
    }
    //endregion

    //region timer
    override fun updateHours(newValue: String) {
        viewModel.updateInputHours(newValue = newValue)
    }

    override fun updateMinutes(newValue: String) {
        viewModel.updateInputMinutes(newValue = newValue)
    }

    override fun updateSeconds(newValue: String) {
        viewModel.updateInputSeconds(newValue = newValue)
    }

    override fun startTimer() {
        viewModel.startTimer()
    }

    override fun stopTimer() {
        viewModel.resetTimer()
    }

    override fun pauseTimer() {
        viewModel.pauseTimer()
    }

    override fun restartTimer(autoRestartTimer: Boolean) {
        viewModel.restartTimer(autoRestartTimer = autoRestartTimer)
    }
    //endregion

    //region stopwatch
    override fun startStopwatch() {
        viewModel.startStopwatch()
    }

    override fun pauseStopwatch() {
        viewModel.pauseStopwatch()
    }

    override fun stopStopwatch() {
        viewModel.resetStopwatch()
    }

    override fun lapStopwatch() {
        viewModel.lapStopwatch()
    }
    //endregion

    //region preferences
    override fun updatePreferences(preferences: Preferences) {
        viewModel.updatePreferences(preferences = preferences)
    }

    override fun updateAllAlarmSubtitles(format: Boolean) {
        viewModel.updateAllAlarmSubtitles(format = format)
    }

    override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
        shouldEnableUpcomingAlarmNotification: Boolean
    ) {
        viewModel.updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
            shouldEnableUpcomingAlarmNotification = shouldEnableUpcomingAlarmNotification
        )
    }
    //endregion
}
