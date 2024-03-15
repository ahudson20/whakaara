package com.app.whakaara.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButton
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils.Companion.getAlarmTimeFormatted
import com.app.whakaara.utils.DateUtils.Companion.getTimeUntilAlarmFormatted
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    preferencesState: PreferencesState,
    alarmState: AlarmState,
    stopwatchState: StopwatchState,
    timerState: TimerState,
    create: (alarm: Alarm) -> Unit,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    startTimer: () -> Unit,
    stopTimer: () -> Unit,
    pauseTimer: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: Boolean) -> Unit
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
            if (wasGranted) {
                isDialogShown.value = !isDialogShown.value
            }
        }

    Scaffold(
        topBar = {
            TopBar(
                route = navBackStackEntry?.destination?.route.toString(),
                preferencesState = preferencesState,
                updatePreferences = updatePreferences,
                updateAllAlarmSubtitles = updateAllAlarmSubtitles
            )
        },
        bottomBar = { BottomNavigation(navController = navController) },
        floatingActionButton = {
            when (navBackStackEntry?.destination?.route) {
                "alarm" -> {
                    FloatingActionButton(
                        isDialogShown = isDialogShown,
                        launcher = launcher
                    )
                    if (isDialogShown.value) {
                        TimePickerDialog(
                            onDismissRequest = { isDialogShown.value = false },
                            initialTime = LocalTime.now().plusMinutes(1).noSeconds(),
                            onTimeChange = {
                                val date = Calendar.getInstance().apply {
                                    set(Calendar.HOUR_OF_DAY, it.hour)
                                    set(Calendar.MINUTE, it.minute)
                                    set(Calendar.SECOND, 0)
                                }
                                create(
                                    Alarm(
                                        date = date,
                                        subTitle = getAlarmTimeFormatted(
                                            date = date,
                                            is24HourFormatEnabled = preferencesState.preferences.is24HourFormat
                                        ),
                                        vibration = preferencesState.preferences.isVibrateEnabled,
                                        isSnoozeEnabled = preferencesState.preferences.isSnoozeEnabled,
                                        deleteAfterGoesOff = preferencesState.preferences.deleteAfterGoesOff
                                    )
                                )
                                isDialogShown.value = false
                                context.showToast(
                                    message = getTimeUntilAlarmFormatted(date = date)
                                )
                            },
                            title = { Text(text = stringResource(id = R.string.time_picker_dialog_title)) },
                            is24HourFormat = true
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                preferencesState = preferencesState,
                alarmState = alarmState,
                stopwatchState = stopwatchState,
                timerState = timerState,

                delete = delete,
                disable = disable,
                enable = enable,
                reset = reset,

                updateHours = updateHours,
                updateMinutes = updateMinutes,
                updateSeconds = updateSeconds,
                startTimer = startTimer,
                stopTimer = stopTimer,
                pauseTimer = pauseTimer,

                onStart = onStart,
                onPause = onPause,
                onStop = onStop
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun MainPreview() {
    WhakaaraTheme {
        MainScreen(
            preferencesState = PreferencesState(),
            alarmState = AlarmState.Success(),
            stopwatchState = StopwatchState(),
            timerState = TimerState(),
            create = {},
            delete = {},
            disable = {},
            enable = {},
            reset = {},
            updateHours = {},
            updateMinutes = {},
            updateSeconds = {},
            startTimer = {},
            stopTimer = {},
            pauseTimer = {},
            onStart = {},
            onPause = {},
            onStop = {},
            updatePreferences = {},
            updateAllAlarmSubtitles = {}
        )
    }
}
