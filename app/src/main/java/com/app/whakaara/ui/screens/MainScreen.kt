package com.app.whakaara.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.state.events.AppViewModels
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.ui.navigation.BottomNavItem
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.navigation.navigateToRootScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import com.whakaara.core.LeafScreen
import com.whakaara.core.NotificationUtils
import com.whakaara.core.RootScreen
import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.designsystem.FloatingActionButtonRow
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.core.rememberPermissionStateSafe
import com.whakaara.feature.alarm.R
import com.whakaara.feature.alarm.utils.DateUtils
import com.whakaara.feature.alarm.utils.GeneralUtils.Companion.showToast
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.timer.TimerState
import java.time.LocalTime
import java.util.Calendar

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    preferencesState: PreferencesState,
    preferencesEventCallbacks: PreferencesEventCallbacks,
    viewModels: AppViewModels,
    timerState: TimerState,
) {
    val navController = rememberNavController()
    val currentSelectedScreen by navController.currentScreenAsState()
    val currentRoute by navController.currentRouteAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val notificationPermissionState = rememberPermissionStateSafe(permission = Manifest.permission.POST_NOTIFICATIONS)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        when (currentRoute) {
            LeafScreen.Timer.route -> {
                if (wasGranted) {
                    viewModels.timer.startTimer()
                }
            }
            LeafScreen.Alarm.route -> {
                if (wasGranted) {
                    isDialogShown.value = !isDialogShown.value
                }
            }
        }
    }
    val navItems = listOf(
        BottomNavItem.Alarm,
        BottomNavItem.Timer,
        BottomNavItem.Stopwatch
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                navItems.forEachIndexed { _, bottomNavItem ->
                    item(
                        selected = bottomNavItem.rootScreen == currentSelectedScreen,
                        icon = { Icon(imageVector = bottomNavItem.icon, contentDescription = bottomNavItem.title) },
                        label = { Text(text = bottomNavItem.title) },
                        onClick = {
                            navController.navigateToRootScreen(bottomNavItem.rootScreen)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (!preferencesState.preferences.shouldShowOnboarding) {
                    TopBar(
                        route = currentRoute,
                        preferencesState = preferencesState,
                        preferencesEventCallbacks = preferencesEventCallbacks
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                when (currentRoute) {
                    LeafScreen.Timer.route -> {
                        FloatingActionButtonRow(
                            isPlaying = timerState.isTimerActive,
                            isStart = timerState.isStart,
                            isPlayButtonVisible = timerState.inputHours != DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE ||
                                timerState.inputMinutes != DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE ||
                                timerState.inputSeconds != DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
                            onStop = viewModels.timer::resetTimer,
                            onPlayPause = {
                                if (timerState.isTimerActive) {
                                    viewModels.timer.pauseTimer()
                                } else {
                                    when (notificationPermissionState.status) {
                                        PermissionStatus.Granted -> {
                                            viewModels.timer.startTimer()
                                        }

                                        else -> {
                                            if (notificationPermissionState.status.shouldShowRationale) {
                                                NotificationUtils.snackBarPromptPermission(
                                                    scope = scope,
                                                    snackBarHostState = snackbarHostState,
                                                    context = context
                                                )
                                            } else {
                                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            }
                                        }
                                    }
                                }
                            },
                            onExtraButtonClicked = {
                                viewModels.timer.restartTimer()
                            }
                        )
                    }
                    LeafScreen.Alarm.route -> {
                        FloatingActionButton(
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 0.dp
                            ),
                            onClick = {
                                when (notificationPermissionState.status) {
                                    PermissionStatus.Granted -> {
                                        isDialogShown.value = !isDialogShown.value
                                    }

                                    else -> {
                                        if (notificationPermissionState.status.shouldShowRationale) {
                                            NotificationUtils.snackBarPromptPermission(
                                                scope = scope,
                                                snackBarHostState = snackbarHostState,
                                                context = context
                                            )
                                        } else {
                                            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.floating_action_button_icon_description)
                            )
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            contentWindowInsets = WindowInsets.safeContent
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                NavGraph(
                    navController = navController,
                    shouldShowOnboarding = preferencesState.preferences.shouldShowOnboarding,
                    viewModels = viewModels
                )
            }
        }
    }

    AnimatedVisibility(isDialogShown.value) {
        TimePickerDialog(
            onDismissRequest = { isDialogShown.value = false },
            initialTime = LocalTime.now().plusMinutes(1).noSeconds(),
            onTimeChange = {
                val date = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, it.hour)
                    set(Calendar.MINUTE, it.minute)
                    set(Calendar.SECOND, 0)
                }
                viewModels.alarm.create(
                    Alarm(
                        date = date,
                        subTitle = DateUtils.getAlarmTimeFormatted(
                            date = date,
                            timeFormat = preferencesState.preferences.timeFormat
                        ),
                        vibration = preferencesState.preferences.isVibrateEnabled,
                        isSnoozeEnabled = preferencesState.preferences.isSnoozeEnabled,
                        deleteAfterGoesOff = preferencesState.preferences.deleteAfterGoesOff
                    )
                )
                isDialogShown.value = false
                context.showToast(
                    message = viewModels.alarm.getTimeUntilAlarmFormatted(date)
                )
            },
            title = { Text(text = stringResource(id = R.string.time_picker_dialog_title)) },
            is24HourFormat = preferencesState.preferences.timeFormat == TimeFormat.TWENTY_FOUR_HOURS
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun MainPreview() {
    WhakaaraTheme {
        MainScreen(
            preferencesState = PreferencesState(),
            preferencesEventCallbacks = object : PreferencesEventCallbacks {
                override fun updatePreferences(preferences: Preferences) {}

                override fun updateAllAlarmSubtitles(format: TimeFormat) {}

                override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
                    shouldEnableUpcomingAlarmNotification: Boolean
                ) {
                }
            },
            viewModels = AppViewModels(
                main = hiltViewModel(),
                timer = hiltViewModel(),
                stopwatch = hiltViewModel(),
                alarm = hiltViewModel()
            ),
            timerState = TimerState(),
        )
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): State<RootScreen> {
    val selectedItem = remember { mutableStateOf<RootScreen>(RootScreen.Alarm) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == RootScreen.Alarm.route } -> {
                    selectedItem.value = RootScreen.Alarm
                }

                destination.hierarchy.any { it.route == RootScreen.Stopwatch.route } -> {
                    selectedItem.value = RootScreen.Stopwatch
                }

                destination.hierarchy.any { it.route == RootScreen.Timer.route } -> {
                    selectedItem.value = RootScreen.Timer
                }

//                destination.hierarchy.any { it.route == RootScreen.Settings.route } -> {
//                    selectedItem.value = RootScreen.Settings
//                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}

@Stable
@Composable
private fun NavController.currentRouteAsState(): State<String?> {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = destination.route
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}
