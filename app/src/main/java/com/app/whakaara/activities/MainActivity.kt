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
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.ui.screens.MainScreen
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.AlarmViewModel
import com.whakaara.feature.stopwatch.StopwatchViewModel
import com.whakaara.feature.timer.TimerViewModel
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.TimeFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalLayoutApi
class MainActivity : ComponentActivity(), PreferencesEventCallbacks {
    private val viewModel: MainViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()
    private val stopwatchViewModel: StopwatchViewModel by viewModels()
    private val alarmViewModel: AlarmViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            !viewModel.preferencesUiState.value.isReady //viewModel.alarmState.value is AlarmState.Loading &&
        }

        setContent {
            val preferencesState by viewModel.preferencesUiState.collectAsStateWithLifecycle()
//            val alarmState by viewModel.alarmState.collectAsStateWithLifecycle()
//            val stopwatchState by viewModel.stopwatchState.collectAsStateWithLifecycle()
//            val timerState by viewModel.timerState.collectAsStateWithLifecycle()

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
//                    alarmState = alarmState,
//                    stopwatchState = stopwatchState,
//                    timerState = timerState,
//                    alarmEventCallbacks = this@MainActivity,
//                    timerEventCallbacks = this@MainActivity,
//                    stopwatchEventCallbacks = this@MainActivity,
                    preferencesEventCallbacks = this@MainActivity
                )
            }
        }
    }

    // TODO: remove callbacks, shift into respective VM's

    override fun onResume() {
        super.onResume()
//        with(viewModel) {
//            recreateTimer()
//            cancelTimerNotification()
//        }

        with(stopwatchViewModel) {
            recreateStopwatch()
            cancelStopwatchNotification()
        }

        with(timerViewModel) {
            recreateTimer()
            cancelTimerNotification()
        }
    }

    override fun onPause() {
        super.onPause()
        with(stopwatchViewModel) {
            saveStopwatchStateForRecreation()
            startStopwatchNotification()
        }

        with(timerViewModel) {
            saveTimerStateForRecreation()
            startTimerNotification()
        }
    }

    override fun updatePreferences(preferences: Preferences) {
        viewModel.updatePreferences(preferences = preferences)
    }

    override fun updateAllAlarmSubtitles(format: TimeFormat) {
        alarmViewModel.updateAllAlarmSubtitles(format = format)
    }

    override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean) {
        alarmViewModel.updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification)
    }

    //region alarm
//    override fun create(alarm: Alarm) {
//        viewModel.create(alarm = alarm)
//    }
//
//    override fun delete(alarm: Alarm) {
//        viewModel.delete(alarm = alarm)
//    }
//
//    override fun disable(alarm: Alarm) {
//        viewModel.disable(alarm = alarm)
//    }
//
//    override fun enable(alarm: Alarm) {
//        viewModel.enable(alarm = alarm)
//    }
//
//    override fun reset(alarm: Alarm) {
//        viewModel.reset(alarm = alarm)
//    }
//
//    override fun getInitialTimeToAlarm(
//        isEnabled: Boolean,
//        time: Calendar
//    ): String = viewModel.getInitialTimeToAlarm(isEnabled = isEnabled, time = time)
//
//    override fun getTimeUntilAlarmFormatted(date: Calendar): String = viewModel.getTimeUntilAlarmFormatted(date = date)
//    //endregion

    //region timer
//    override fun updateHours(newValue: String) {
//        timerViewModel.updateInputHours(newValue = newValue)
//    }
//
//    override fun updateMinutes(newValue: String) {
//        timerViewModel.updateInputMinutes(newValue = newValue)
//    }
//
//    override fun updateSeconds(newValue: String) {
//        timerViewModel.updateInputSeconds(newValue = newValue)
//    }
//
//    override fun startTimer() {
//        timerViewModel.startTimer()
//    }
//
//    override fun stopTimer() {
//        timerViewModel.resetTimer()
//    }
//
//    override fun pauseTimer() {
//        timerViewModel.pauseTimer()
//    }
//
//    override fun restartTimer(autoRestartTimer: Boolean) {
//        timerViewModel.restartTimer() //autoRestartTimer = autoRestartTimer
//    }
//    //endregion
//
//    //region stopwatch
//    override fun startStopwatch() {
////        viewModel.startStopwatch()
//    }
//
//    override fun pauseStopwatch() {
////        viewModel.pauseStopwatch()
//    }
//
//    override fun stopStopwatch() {
////        viewModel.resetStopwatch()
//    }
//
//    override fun lapStopwatch() {
////        viewModel.lapStopwatch()
//    }
//    //endregion
//
//    //region preferences
//    override fun updatePreferences(preferences: Preferences) {
//        viewModel.updatePreferences(preferences = preferences)
//    }
//
//    override fun updateAllAlarmSubtitles(format: TimeFormat) {
////        viewModel.updateAllAlarmSubtitles(format = format)
//    }
//
//    override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(shouldEnableUpcomingAlarmNotification: Boolean) {
//        viewModel.updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
//            shouldEnableUpcomingAlarmNotification = shouldEnableUpcomingAlarmNotification
//        )
//    }
    //endregion
}
