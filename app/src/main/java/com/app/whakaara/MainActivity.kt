package com.app.whakaara

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.bottomsheet.BottomSheet
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.WhakaaraTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
@ExperimentalLayoutApi
@OptIn(ExperimentalMaterialApi::class)
class MainActivity : ComponentActivity() {

    private val viewModel : MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
                    skipHalfExpanded = true
                )
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

                ModalBottomSheetLayout(
                    sheetState = sheetState,
                    sheetContent = { BottomSheet(sheetState) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        topBar = { TopBar(navController = navController) },
                        bottomBar = { BottomNavigation(navController = navController) },
                        floatingActionButton = {
                            /** Only show FAB when on alarm screen **/
                            when (navBackStackEntry?.destination?.route) {
                                "alarm" -> {
                                    FloatingActionButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                timePickerDialog.apply {
                                                    this.updateTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE))
                                                }.show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add Alarm",
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
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainActivity()
}
