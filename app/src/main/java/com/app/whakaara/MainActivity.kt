package com.app.whakaara

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.floatingactionbutton.FloatingButton
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils.Companion.alarmTimeTo24HourFormat
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
@ExperimentalLayoutApi
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                Main(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Main(
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
            /** Only show FAB when on alarm screen **/
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
                                        subTitle =  alarmTimeTo24HourFormat(hour = it.hour, minute = it.minute)
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

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainActivity()
}
