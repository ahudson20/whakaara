package com.app.whakaara.ui.navigation

import android.app.TimePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    viewModel: MainViewModel,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        {_, hour : Int, minute: Int ->
            val alarmLocal = Alarm(hour = hour, minute = minute, title= "$hour-$minute", vibration = true)
            viewModel.create(alarmLocal)
        },
        calendar[Calendar.HOUR_OF_DAY],
        calendar[Calendar.MINUTE],
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
                                timePickerDialog.apply {
                                    this.updateTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE))
                                }.show()
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

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    navBackStackEntry?.destination?.route = "alarm"
    TopBar(navController, hiltViewModel())
}