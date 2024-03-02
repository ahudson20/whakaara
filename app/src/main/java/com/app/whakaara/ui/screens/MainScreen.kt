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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.floatingactionbutton.FloatingActionButton
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
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
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            isDialogShown.value = !isDialogShown.value
        }
    }

    val pref by viewModel.preferencesUiState.collectAsStateWithLifecycle()
    val alarms by viewModel.alarmState.collectAsStateWithLifecycle()
    val stopwatch by viewModel.stopwatchState.collectAsStateWithLifecycle()
    val timer by viewModel.timerState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopBar(
                route = navBackStackEntry?.destination?.route.toString(),
                preferencesState = pref,
                updatePreferences = viewModel::updatePreferences,
                updateAllAlarmSubtitles = viewModel::updateAllAlarmSubtitles
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
                                viewModel.create(
                                    Alarm(
                                        date = date,
                                        subTitle = getAlarmTimeFormatted(date = date, is24HourFormatEnabled = pref.preferences.is24HourFormat),
                                        vibration = pref.preferences.isVibrateEnabled,
                                        isSnoozeEnabled = pref.preferences.isSnoozeEnabled,
                                        deleteAfterGoesOff = pref.preferences.deleteAfterGoesOff
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
            NavGraph(navController = navController, viewModel = viewModel, preferencesState = pref, alarmState = alarms, stopwatchState = stopwatch, timerState = timer)
        }
    }
}

/**
 * NOTE:
 * previews don't work for composables that have VM as parameter.
 * **/
// @Composable
// @ThemePreviews
// @FontScalePreviews
// fun MainPreview() {
//     WhakaaraTheme {
//         MainScreen(
//             viewModel = hiltViewModel()
//         )
//     }
// }
