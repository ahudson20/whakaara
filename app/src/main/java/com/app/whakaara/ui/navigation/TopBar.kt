package com.app.whakaara.ui.navigation

import android.app.TimePickerDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    sheetState: ModalBottomSheetState,
    viewModel: MainViewModel,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val alarmLocal = remember { Alarm(hour = -1, minute = -1, title = null, vibration = false) }

    // Declaring and initializing a calendar
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    val timePickerDialog = TimePickerDialog(
            context,
    {_, hour : Int, minute: Int ->
        alarmLocal.apply {
            println("hour: $hour minute: $minute")
            this.hour = hour
            this.minute = minute

            // TODO: for now well just init the other values
            this.title = "title-$hour-$minute"
            this.vibration = true
        }
        println(alarmLocal.hour)
        println(alarmLocal.minute)
        println(alarmLocal.title)
        viewModel.insert(alarmLocal)
//            .invokeOnCompletion {
//            alarmLocal.apply {
//                this.hour = -1
//                this.minute = -1
//                this.title = null
//                this.vibration = false
//            }
//        }
    },
    hour,
    minute,
    false
    )

    SmallTopAppBar(
        title = {},
        actions = {
            when (navBackStackEntry?.destination?.route) {
                "alarm" -> {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                timePickerDialog.show()
//                                if (sheetState.isVisible) sheetState.hide()
//                                else sheetState.show()
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "create alarm")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route = "alarm"
    TopBar(navController, rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden), hiltViewModel())
}