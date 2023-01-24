package com.app.whakaara

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
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
import com.marosseleng.compose.material3.datetimepickers.time.domain.noSeconds
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.util.*

@AndroidEntryPoint
@ExperimentalLayoutApi
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel : MainViewModel by viewModels()

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhakaaraTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val isDialogShown = rememberSaveable { mutableStateOf(false) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
                    skipHalfExpanded = true
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
                                            isDialogShown.value  = !isDialogShown.value
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add Alarm",
                                        )
                                    }

                                    if (isDialogShown.value) {
                                        TimePickerDialog(
                                            onDismissRequest = { isDialogShown.value = false },
                                            initialTime = LocalTime.now().noSeconds(),
                                            onTimeChange = {
                                                viewModel.create(
                                                    Alarm(
                                                        hour = it.hour,
                                                        minute = it.minute,
                                                        title = "new alarm~",
                                                        vibration = true
                                                    )
                                                )
                                                isDialogShown.value = false
                                            },
                                            title = { Text(text = "Select time") }
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
