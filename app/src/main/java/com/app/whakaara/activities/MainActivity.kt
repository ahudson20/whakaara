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
import com.app.whakaara.data.preferences.AppTheme
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.state.AlarmState
import com.app.whakaara.ui.screens.MainScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalLayoutApi
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.alarmState.value is AlarmState.Loading && !viewModel.isReady.value
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
                darkTheme = useDarkColours
            ) {
                MainScreen(
                    preferencesState = preferencesState,
                    alarmState = alarmState,
                    stopwatchState = stopwatchState,
                    timerState = timerState,
                    create = viewModel::create,
                    delete = viewModel::delete,
                    disable = viewModel::disable,
                    enable = viewModel::enable,
                    reset = viewModel::reset,
                    updateHours = viewModel::updateInputHours,
                    updateMinutes = viewModel::updateInputMinutes,
                    updateSeconds = viewModel::updateInputSeconds,
                    startTimer = viewModel::startTimer,
                    stopTimer = viewModel::resetTimer,
                    pauseTimer = viewModel::pauseTimer,
                    onStart = viewModel::startStopwatch,
                    onPause = viewModel::pauseStopwatch,
                    onStop = viewModel::resetStopwatch,
                    updatePreferences = viewModel::updatePreferences,
                    updateAllAlarmSubtitles = viewModel::updateAllAlarmSubtitles,
                    filterAlarmList = viewModel::filterAlarmList
                )
            }
        }
    }
}
