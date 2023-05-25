package com.app.whakaara.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.floatingactionbutton.FloatingButton
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.utils.DateUtils
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val isDialogShown = rememberSaveable { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { wasGranted ->
        if (wasGranted) {
            isDialogShown.value = !isDialogShown.value
        }
    }

    Scaffold(
        topBar = { TopBar(navController = navController) },
        bottomBar = { BottomNavigation(navController = navController) },
        floatingActionButton = {
            when (navBackStackEntry?.destination?.route) {
                "alarm" -> {
                    FloatingButton(
                        isDialogShown = isDialogShown,
                        scaffoldState = scaffoldState,
                        launcher = launcher
                    )
                    if (isDialogShown.value) {
                        TimePickerDialog(
                            onDismissRequest = { isDialogShown.value = false },
                            initialTime = LocalTime.now().noSeconds(),
                            onTimeChange = {
                                viewModel.create(
                                    Alarm(
                                        hour = it.hour,
                                        minute = it.minute,
                                        subTitle =  DateUtils.alarmTimeTo24HourFormat(hour = it.hour, minute = it.minute)
                                    )
                                )
                                isDialogShown.value = false
                            },
                            title = { Text(text = stringResource(id = R.string.time_picker_dialog_title)) },
                            is24HourFormat = true,
                        )
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(navController = navController, viewModel = viewModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainScreen(
        viewModel = hiltViewModel()
    )
}